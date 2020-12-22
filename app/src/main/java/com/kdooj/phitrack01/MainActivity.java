package com.kdooj.phitrack01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    Intent home=new Intent(MainActivity.this,home.class);
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