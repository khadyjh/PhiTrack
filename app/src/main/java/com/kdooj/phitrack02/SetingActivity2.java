package com.kdooj.phitrack02;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetingActivity2 extends AppCompatActivity {
    private String getType;
    private CircleImageView profileImgeView;
    private EditText drivName,drivPhone,driveBusNum;
    private ImageView saveBtn,closeBtn;
    private TextView imgeChanger;
    private  String checker="";
    private Uri imgUri;
    private  String myUrl="";
    private StorageTask uploadTask;
    private StorageReference storageProfilePicRef;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting2);

        getType=getIntent().getStringExtra("type");

        mAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child( getType);
        storageProfilePicRef= FirebaseStorage.getInstance().getReference().child("Profile Pic");


        profileImgeView=findViewById(R.id.profile_image);

        drivName=findViewById(R.id.edtDriveName);
        drivPhone=findViewById(R.id.edtDrivPhone);
        driveBusNum=findViewById(R.id.edtBusNum);

        saveBtn=findViewById(R.id.saveBtn);
        closeBtn=findViewById(R.id.closebtn);

        imgeChanger=findViewById(R.id.tvChangeImage);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SetingActivity2.this,DriverMapsActivity.class));
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(checker.equals("clicked"))
                {
                    validateControlars();
                }
                else
                    {
                        validateAndSaveOnlyInfo();
                    }
            }
        });

        imgeChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";
                CropImage.activity().setAspectRatio(1,1).start(SetingActivity2.this);
            }
        });
        getUserInfo();
    }

    private void validateAndSaveOnlyInfo()
    {
        if(TextUtils.isEmpty(drivName.getText().toString()))
        {
            Toast.makeText(this, "Please provide your Name", Toast.LENGTH_SHORT).show();
        }else
        if(TextUtils.isEmpty(drivPhone.getText().toString()))
        {
            Toast.makeText(this, "Please provide your Phone Number", Toast.LENGTH_SHORT).show();
        }else
        if(TextUtils.isEmpty(driveBusNum.getText().toString()))
        {
            Toast.makeText(this, "Please provide your Bus Number", Toast.LENGTH_SHORT).show();
        }else
        {
            HashMap<String,Object>userMap=new HashMap<>();
            userMap.put("uid",mAuth.getCurrentUser().getUid());
            userMap.put("name",drivName.getText().toString());
            userMap.put("phone",drivPhone.getText().toString());
            userMap.put("busNumber",driveBusNum.getText().toString());


            databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
            startActivity(new Intent(SetingActivity2.this,DriverMapsActivity.class));

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imgUri=result.getUri();
            profileImgeView.setImageURI(imgUri);
        }
        else
        {
            startActivity(new Intent(SetingActivity2.this,DriverMapsActivity.class));
            Toast.makeText(this, "Error,Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateControlars()
    {
        if(TextUtils.isEmpty(drivName.getText().toString()))
        {
            Toast.makeText(this, "Please provide your Name", Toast.LENGTH_SHORT).show();
        }else
        if(TextUtils.isEmpty(drivPhone.getText().toString()))
        {
            Toast.makeText(this, "Please provide your Phone Number", Toast.LENGTH_SHORT).show();
        }else
        if(TextUtils.isEmpty(driveBusNum.getText().toString()))
        {
            Toast.makeText(this, "Please provide your Bus Number", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadProfilePic();
        }
    }

    private void uploadProfilePic()
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Settings account info");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        if(imgUri!=null)
        {
            final StorageReference fileRef=storageProfilePicRef.child(mAuth.getCurrentUser()
                    .getUid() + ".jpg");
            uploadTask = fileRef.putFile(imgUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                   if(!task.isSuccessful())
                   {
                       throw task.getException();
                   }
                   return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri=task.getResult();
                        myUrl=downloadUri.toString();

                        HashMap<String,Object>userMap=new HashMap<>();
                        userMap.put("uid",mAuth.getCurrentUser().getUid());
                        userMap.put("name",drivName.getText().toString());
                        userMap.put("phone",drivPhone.getText().toString());
                        userMap.put("busNumber",driveBusNum.getText().toString());
                        userMap.put("image",myUrl);

                        databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
                        progressDialog.dismiss();
                        startActivity(new Intent(SetingActivity2.this,DriverMapsActivity.class));

                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "Image not selected ", Toast.LENGTH_SHORT).show();

        }
    }

    private void getUserInfo()
    {
        databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists() && snapshot.getChildrenCount()>0)
                {
                    String name=snapshot.child("name").getValue().toString();
                    String phone=snapshot.child("phone").getValue().toString();
                    drivName.setText(name);
                    drivPhone.setText(phone);
                    String busNumber=snapshot.child("busNumber").getValue().toString();
                    driveBusNum.setText(busNumber);
                    if(snapshot.hasChild("image"))
                    {
                        String image=snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profileImgeView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}