package com.kdooj.phitrack01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class home extends AppCompatActivity {
    Button btnDrivar,btnPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnDrivar=findViewById(R.id.btnDriver);
        btnPassenger=findViewById(R.id.btnPassenger);

        btnDrivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent1=new Intent(home.this,driver.class);
                startActivity(homeIntent1);
            }
        });
        btnPassenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent2=new Intent(home.this,passenger.class);
                startActivity(homeIntent2);
            }
        });
    }
}