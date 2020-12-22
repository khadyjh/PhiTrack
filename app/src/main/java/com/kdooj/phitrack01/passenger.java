package com.kdooj.phitrack01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class passenger extends AppCompatActivity {
     EditText passengerId;
    EditText passengerPass;
   private Button btnPassengerLogIn;
   private Button btnPassengerRegister;
   private TextView tvPLink;
   private TextView tvPLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        passengerId=findViewById(R.id.passengerId);
        passengerPass=findViewById(R.id.passengerPass);
        btnPassengerLogIn=findViewById(R.id.btnDLogin);
        btnPassengerRegister=findViewById(R.id.btnPRegister);
        tvPLink=findViewById(R.id.tvRLinkP);
        tvPLogin=findViewById(R.id.tvPLogin);

        btnPassengerRegister.setVisibility(View.INVISIBLE);
        btnPassengerRegister.setEnabled(false);

        tvPLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                btnPassengerLogIn.setVisibility(View.INVISIBLE);
                tvPLink.setVisibility(View.INVISIBLE);
                tvPLogin.setText("Register");
                btnPassengerRegister.setVisibility(View.VISIBLE);
                btnPassengerLogIn.setEnabled(true);

            }
        });

    }
}