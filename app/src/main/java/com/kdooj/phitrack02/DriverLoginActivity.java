package com.kdooj.phitrack02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverLoginActivity extends AppCompatActivity {
    private Button btnDriverLogin;
    private Button btnDriverRegister;
    private TextView tvDriverLink;
    private TextView tvDriverLogin;
    private EditText DriverEmail;
    private EditText DriverPassword;
    private EditText DriverPassword1;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference DriverDatabaseRef;
    private String OnDriverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        mAuth=FirebaseAuth.getInstance();



        btnDriverLogin=findViewById(R.id.btnDriverLogin);
        btnDriverRegister=findViewById(R.id.btnDriverRegister);
        tvDriverLink=findViewById(R.id.tvDriverlink);
        tvDriverLogin=findViewById(R.id.tv_Deiverlogin);
        DriverEmail=findViewById(R.id.edt_DriverEmail);
        DriverPassword=findViewById(R.id.edt_DriverPassword);
        DriverPassword1=findViewById(R.id.edt_DriverPassword1);

        loadingBar=new ProgressDialog(this);

        DriverPassword1.setVisibility(View.INVISIBLE);
        DriverPassword1.setEnabled(false);
        btnDriverRegister.setVisibility(View.INVISIBLE);
        btnDriverRegister.setEnabled(false);

        tvDriverLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDriverLogin.setVisibility(View.INVISIBLE);
                tvDriverLink.setVisibility(View.INVISIBLE);
                tvDriverLogin.setText("Driver Registeration");
                DriverPassword.setText("");
                DriverEmail.setText("");
                btnDriverRegister.setVisibility(View.VISIBLE);
                btnDriverRegister.setEnabled(true);
                DriverPassword1.setVisibility(View.VISIBLE);
                DriverPassword1.setEnabled(true);
            }
        });
        btnDriverRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email=DriverEmail.getText().toString();
                String passwoed =DriverPassword.getText().toString();
                String password2 =DriverPassword1.getText().toString();

                RegisterDriver(email,passwoed,password2);
            }
        });

        btnDriverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email=DriverEmail.getText().toString();
                String passwoed =DriverPassword.getText().toString();

                LoginDriver(email,passwoed);
            }
        });
    }

    private void LoginDriver(String email, String passwoed)
    {
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(DriverLoginActivity.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
        }
        else
        if(TextUtils.isEmpty(passwoed))
        {
            Toast.makeText(DriverLoginActivity.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
        }else
            {


            loadingBar.setTitle("Driver Login");
            loadingBar.setMessage("Please wait...");
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email,passwoed)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(DriverLoginActivity.this, "successfull Login", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent drivermap=new Intent(DriverLoginActivity.this,DriverMapsActivity.class);
                                startActivity(drivermap);
                            }else {
                                Toast.makeText(DriverLoginActivity.this, "unsuccessful login,try agin", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }
                    });

        }
    }

    private void RegisterDriver(String email,String passwoed,String password2)
    {


        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(DriverLoginActivity.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
        }
        else
        if(TextUtils.isEmpty(passwoed))
        {
            Toast.makeText(DriverLoginActivity.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
        }
        else
           if(TextUtils.isEmpty(password2))
        {
            Toast.makeText(DriverLoginActivity.this, "Please Confirm your Password...", Toast.LENGTH_SHORT).show();
        }else
            if(!passwoed.equals(password2))
            {
                Toast.makeText(DriverLoginActivity.this, "Different Password,Try again...", Toast.LENGTH_SHORT).show();
            }
            else {


                loadingBar.setTitle("Driver Registration");
                loadingBar.setMessage("Please wait while we are register your data...");
                loadingBar.show();
                mAuth.createUserWithEmailAndPassword(email,passwoed)
                     .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             if(task.isSuccessful())
                             {
                                 mAuth=FirebaseAuth.getInstance();
                                 OnDriverId=mAuth.getCurrentUser().getUid();
                                 DriverDatabaseRef= FirebaseDatabase.getInstance().getReference()
                                         .child("Users").child("Drivers").child(OnDriverId);
                                 DriverDatabaseRef.setValue(true);

                                 Intent drivermap=new Intent(DriverLoginActivity.this,DriverMapsActivity.class);
                                 startActivity(drivermap);

                                 Toast.makeText(DriverLoginActivity.this, "successfull Registration", Toast.LENGTH_SHORT).show();
                                 loadingBar.dismiss();


                             }else {
                                 Toast.makeText(DriverLoginActivity.this, "Faill", Toast.LENGTH_SHORT).show();
                                 loadingBar.dismiss();
                             }

                         }
                     });

            }
    }


}