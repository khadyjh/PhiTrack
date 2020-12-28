package com.kdooj.phitrack02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
   private Button btnDriver,btnPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnDriver=findViewById(R.id.btn_Driver);
        btnPassenger=findViewById(R.id.btn_Passenger);

        btnDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent driverIntent=new Intent(MainActivity.this,DriverLoginActivity.class);
                startActivity(driverIntent);

            }
        });

        btnPassenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent passengerIntent=new Intent(MainActivity.this,PassengerLoginActivity.class);
                startActivity(passengerIntent);
            }
        });
    }
}