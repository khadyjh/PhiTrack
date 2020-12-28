package com.kdooj.phitrack02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PassengerLoginActivity extends AppCompatActivity {
    private Button btnPassengerLogin;
    private Button btnPassengerRegister;
    private TextView tvPassengerlogin;
    private TextView tvPassengerLink;
    private EditText PassengerEmail;
    private EditText PasengerPassword;
    private EditText PasengerPassword1;
    private FirebaseAuth mAuth1;
    private ProgressDialog loadingBar1;
    private DatabaseReference PassengerDatabaseRef;
    private String OnPassengerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_login);



        btnPassengerLogin=findViewById(R.id.btnPassengerLogin);
        btnPassengerRegister=findViewById(R.id.btnPassengerRegister);
        tvPassengerLink=findViewById(R.id.tvPassengerlink);
        tvPassengerlogin=findViewById(R.id.tvPassengerLogin);
        PassengerEmail=findViewById(R.id.edtPassengerEmail);
        PasengerPassword=findViewById(R.id.edtPassengerpassword);
        PasengerPassword1=findViewById(R.id.edtPassengerPassword1);

        btnPassengerRegister.setVisibility(View.INVISIBLE);
        btnPassengerRegister.setEnabled(false);
        PasengerPassword1.setVisibility(View.INVISIBLE);
        PasengerPassword1.setEnabled(false);

        loadingBar1=new ProgressDialog(this);

        tvPassengerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPassengerLogin.setVisibility(View.INVISIBLE);
                tvPassengerLink.setVisibility(View.INVISIBLE);
                tvPassengerlogin.setText("Passenger Registeration");
                btnPassengerRegister.setVisibility(View.VISIBLE);
                btnPassengerRegister.setEnabled(true);
                PasengerPassword1.setVisibility(View.VISIBLE);
                PasengerPassword1.setEnabled(true);

            }
        });
        btnPassengerRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=PassengerEmail.getText().toString();
                String password =PasengerPassword.getText().toString();
                String password2 =PasengerPassword1.getText().toString();

                RegisterPssenger(email,password,password2);
            }
        });

        btnPassengerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email=PassengerEmail.getText().toString();
                String password =PasengerPassword.getText().toString();

                LoginPassenger(email,password);

            }
        });

    }

    private void LoginPassenger(String email, String password)
    {
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(PassengerLoginActivity.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
        }
        else
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(PassengerLoginActivity.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
        }
        else {


            loadingBar1.setTitle("Passenger Login");
            loadingBar1.setMessage("Please wait...");
            loadingBar1.show();
            mAuth1.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {

                                Toast.makeText(PassengerLoginActivity.this, "successfull Login", Toast.LENGTH_SHORT).show();
                                loadingBar1.dismiss();
                            }else {
                                Toast.makeText(PassengerLoginActivity.this, "unsuccessful Login", Toast.LENGTH_SHORT).show();
                                loadingBar1.dismiss();
                            }

                        }
                    });

        }
    }

    private void RegisterPssenger(String email, String password, String password2)
    {

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(PassengerLoginActivity.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
        }
        else
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(PassengerLoginActivity.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
        }
        else
        if(TextUtils.isEmpty(password2))
        {
            Toast.makeText(PassengerLoginActivity.this, "Please Confirm your Password...", Toast.LENGTH_SHORT).show();
        }else
        if(!password.equals(password2))
        {
            Toast.makeText(PassengerLoginActivity.this, "Different Password,Try again...", Toast.LENGTH_SHORT).show();
        }
        else {


            loadingBar1.setTitle("Passenger Registration");
            loadingBar1.setMessage("Please wait while we are register your data...");
            loadingBar1.show();
            mAuth1.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                               /*mAuth1=FirebaseAuth.getInstance();
                                OnPassengerId=mAuth1.getCurrentUser().getUid();
                                PassengerDatabaseRef= FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child("Passengers").child(OnPassengerId);

                                PassengerDatabaseRef.setValue(true);*/
                                Intent Passengermap=new Intent(PassengerLoginActivity.this,PassengerMapsActivity.class);
                                startActivity(Passengermap);
                                Toast.makeText(PassengerLoginActivity.this, "successfull Registration", Toast.LENGTH_SHORT).show();
                                loadingBar1.dismiss();
                            }else
                                {
                                Toast.makeText(PassengerLoginActivity.this, "unsuccessful Registration", Toast.LENGTH_SHORT).show();
                                loadingBar1.dismiss();
                                }

                        }
                    });

        }
    }
}