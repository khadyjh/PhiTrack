package com.kdooj.phitrack01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class driver extends AppCompatActivity {
    private Button btnDriverLogIn;
    private Button btnDriverRegister;
    private TextView tvDLink;
    private TextView tvDLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        btnDriverLogIn=findViewById(R.id.btnDLogin);
        btnDriverRegister=findViewById(R.id.btnDRegister);
        tvDLink=findViewById(R.id.tvRLink);
        tvDLogin=findViewById(R.id.tvPLogin);

        btnDriverRegister.setVisibility(View.INVISIBLE);
        btnDriverRegister.setEnabled(false);
        tvDLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDriverLogIn.setVisibility(View.INVISIBLE);
                tvDLink.setVisibility(View.INVISIBLE);
                tvDLogin.setText("Register");
                btnDriverRegister.setVisibility(View.VISIBLE);
                btnDriverLogIn.setEnabled(true);
            }
        });

    }
}