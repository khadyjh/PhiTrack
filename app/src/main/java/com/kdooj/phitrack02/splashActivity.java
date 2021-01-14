package com.kdooj.phitrack02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class splashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread thrrad=new Thread()
        {
            @Override
            public void run() {
                try
                {
                    sleep(5000);

                }
                catch (Exception e)
                {
                    e.printStackTrace();

                }
                finally
                {
                    Intent home=new Intent(splashActivity.this,MainActivity.class);
                    startActivity(home);

                }
            }
        };
        thrrad.start();

    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }

}