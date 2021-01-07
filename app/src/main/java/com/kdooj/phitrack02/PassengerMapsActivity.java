package com.kdooj.phitrack02;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class PassengerMapsActivity extends FragmentActivity implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastlocation;
    LocationRequest locationRequest;
    //private Marker currentUsersLocationMarker;
  //  private static final int Request_User_Location_Code = 99;
    private FirebaseAuth mAuth2;
   // private FirebaseUser currentUser;

    private Button btnPassengerLogout;
    private  Button btnStart;
    private  DatabaseReference DriverAvailableRef;
    private LatLng PassengerPikupLocation;
    private  int radius=1;
    private boolean DriverFound=false;
    private String DriverFoundId;
    private String PassengerID;
    private DatabaseReference PassengerDatabaseRef;
    private DatabaseReference DriverRef;
    private DatabaseReference DriverLocationRef;
    Marker DriverMark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_maps);

        mAuth2=FirebaseAuth.getInstance();
       // currentUser=mAuth2.getCurrentUser();
        PassengerID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        PassengerDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Passengers Request");
        DriverAvailableRef=FirebaseDatabase.getInstance().getReference().child("Available Driver");
        DriverLocationRef=FirebaseDatabase.getInstance().getReference().child("Drivers Working");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync( this );

        //DriverLocationRef=FirebaseDatabase.getInstance().getReference().child("Available Driver");

        btnPassengerLogout=findViewById(R.id.btnPassengerLogout);
        btnStart=findViewById(R.id.btnStart);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                GeoFire geoFire=new GeoFire(PassengerDatabaseRef);
                geoFire.setLocation(PassengerID,new GeoLocation(lastlocation.getLatitude(),lastlocation.getLongitude()));
                PassengerPikupLocation =new LatLng(lastlocation.getLatitude(),lastlocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(PassengerPikupLocation).title("Pickup Passenger from here"));
                btnStart.setText("Getting your bus...");
                GetClosestBus();
            }
        });

        btnPassengerLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth2.signOut();
                LogoutPassenger();
            }
        });
    }

    private void GetClosestBus()
    {
        GeoFire geofire=new GeoFire(DriverAvailableRef);
        GeoQuery geoQuery=geofire.queryAtLocation(new GeoLocation(PassengerPikupLocation.latitude,PassengerPikupLocation.longitude),radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location)
            {
                if(!DriverFound)
                {
                    DriverFound=true;
                    DriverFoundId=key;

                    DriverRef =FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(DriverFoundId);
                    HashMap driverMap=new HashMap();
                    driverMap.put("PassengerRideId",PassengerID);
                    DriverRef.updateChildren(driverMap);

                    GettingDriverLocation();
                    btnStart.setText("Looking for Driver...");
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady()
            {
                if(!DriverFound)
                {
                    radius=radius+1;
                    GetClosestBus();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error)
            {

            }
        });
    }

    private void GettingDriverLocation()
    {
        DriverLocationRef.child(DriverFoundId).child("l")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.exists())
                        {
                            List<Object> driverLocationMap=(List<Object>) snapshot.getValue();
                            double locationLat =0;
                            double locationLng =0;
                            btnStart.setText("Driver Found");

                            if(driverLocationMap.get(0) != null)
                            {
                               locationLat=Double.parseDouble(driverLocationMap.get(0).toString());
                            }
                            if(driverLocationMap.get(1) != null)
                            {
                                locationLng=Double.parseDouble(driverLocationMap.get(1).toString());
                            }

                            LatLng DriverLatLng=new LatLng(locationLat,locationLng);
                            if(DriverMark != null)
                            {
                                DriverMark.remove();
                            }
                            Location location1=new Location("");
                            location1.setLatitude(PassengerPikupLocation.latitude);
                            location1.setLongitude(PassengerPikupLocation.longitude);

                            Location location2=new Location("");
                            location2.setLatitude(DriverLatLng.latitude);
                            location2.setLongitude(DriverLatLng.longitude);

                            float Distance = location1.distanceTo(location2);
                            btnStart.setText("Driver Found " + String.valueOf(Distance));

                            DriverMark=mMap.addMarker(new MarkerOptions().position(DriverLatLng).title("Your Driver is here"));

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        // Add a marker in Sydney and move the camera

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location)
    {
       /* LocationHelper helper=new LocationHelper(
                location.getLatitude(),
                location.getLongitude()
        );
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("Customer Location").child(userId).child("Location").setValue(helper)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(PassengerMapsActivity.this, "saved", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(PassengerMapsActivity.this, "not saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

        lastlocation = location;
       /* if (currentUsersLocationMarker != null) {
            currentUsersLocationMarker.remove();
        }*/
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
       /* MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("PickUp Passenger From here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));





        currentUsersLocationMarker = mMap.addMarker(markerOptions);*/
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(14));
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();


    }
    private void LogoutPassenger()
    {
        Intent logoutIntent=new Intent(PassengerMapsActivity.this,PassengerLoginActivity.class);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logoutIntent);
        finish();
    }
}
