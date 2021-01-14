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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
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

import java.util.List;

public class DriverMapsActivity extends FragmentActivity implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastlocation;
    LocationRequest locationRequest;
    private Marker currentUsersLocationMarker;
    private static final int Request_User_Location_Code = 99;
    Button DriverlogOutBtn;
    Button DriverSettingBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private boolean CurrentDriverLogoutStatus=false;
    private DatabaseReference AssignedPassengerRef,AssignedPassengerPickupRef;
    private String DriverId,PassengerId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        DriverId=mAuth.getCurrentUser().getUid();
        DriverlogOutBtn=findViewById(R.id.btnDriverLogout);
        DriverSettingBtn=findViewById(R.id.btnDriverSetting);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DriverlogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentDriverLogoutStatus=true;
                DisconnectDriver();
                mAuth.signOut();
                LogoutDriver();
            }
        });

        GetAssignedPassengerReq();

        DriverSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setIntent=new Intent(DriverMapsActivity.this,SetingActivity2.class);
                setIntent.putExtra("type","Drivers");
                startActivity(setIntent);
            }
        });
    }

    private void GetAssignedPassengerReq()
    {
        AssignedPassengerRef=FirebaseDatabase.getInstance().getReference().child("Users")
                .child("Drivers").child(DriverId).child("PassengerRideId");
        AssignedPassengerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    PassengerId=snapshot.getValue().toString();
                    GetAssignedPassengerPickUpLocation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetAssignedPassengerPickUpLocation()
    {
        AssignedPassengerPickupRef=FirebaseDatabase.getInstance().getReference().child("Passengers Request")
                .child(PassengerId).child("l");
        AssignedPassengerPickupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                List<Object>PassengerLocationMap=(List<Object>)snapshot.getValue();
                double locationLat =0;
                double locationLng =0;


                if(PassengerLocationMap.get(0) != null)
                {
                    locationLat=Double.parseDouble(PassengerLocationMap.get(0).toString());
                }
                if(PassengerLocationMap.get(1) != null)
                {
                    locationLng=Double.parseDouble(PassengerLocationMap.get(1).toString());
                }
                LatLng DriverLatLng=new LatLng(locationLat,locationLng);
                mMap.addMarker(new MarkerOptions().position(DriverLatLng).title("PickUp Location"));
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

    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);

            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(getApplicationContext() != null)
        {
            lastlocation=location;
            LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());


           /* MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Driver current location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            currentUsersLocationMarker = mMap.addMarker(markerOptions);*/
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference DriverAvailableRef = FirebaseDatabase.getInstance().getReference("Available Driver");
            GeoFire geoFireAval=new GeoFire(DriverAvailableRef);

            DatabaseReference DriverWorkingRef =FirebaseDatabase.getInstance().getReference().child("Drivers Working");
            GeoFire geoFireWork=new GeoFire(DriverWorkingRef);

            switch (PassengerId)
            {
                case "":
                    geoFireWork.removeLocation(userId);
                    geoFireAval.setLocation(userId,new GeoLocation(location.getLatitude(),location.getLongitude()));
                    break;
                default:
                    geoFireAval.removeLocation(userId);
                    geoFireWork.setLocation(userId,new GeoLocation(location.getLatitude(),location.getLongitude()));
                    break;
            }


        }




        /*LocationHelper helper=new LocationHelper(
          location.getLatitude(),
          location.getLongitude()
        );
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("Available Driver").child(userId).child("Location").setValue(helper)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(DriverMapsActivity.this, "saved", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(DriverMapsActivity.this, "not saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        lastlocation = location;
        if (currentUsersLocationMarker != null) {
            currentUsersLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("user current location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));





        currentUsersLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(14));
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }*/

        //GLocation(location);



    }

    /*private void GLocation(Location location) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference DriverAvailableRef = FirebaseDatabase.getInstance().getReference("Drivers Available");

        GeoFire geoFire = new GeoFire(DriverAvailableRef);
        geoFire.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
    }*/

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

    }
    private void LogoutDriver() {
        Intent logoutIntent=new Intent(DriverMapsActivity.this,DriverLoginActivity.class);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logoutIntent);
        finish();

    }
    @Override
    protected void onStop() {
        super.onStop();
        if(!CurrentDriverLogoutStatus) {

            DisconnectDriver();
        }
    }

    private void DisconnectDriver() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference DriverAvailableRef = FirebaseDatabase.getInstance().getReference("Available Driver");
        GeoFire geoFire = new GeoFire(DriverAvailableRef);
        geoFire.removeLocation(userId);
    }


}