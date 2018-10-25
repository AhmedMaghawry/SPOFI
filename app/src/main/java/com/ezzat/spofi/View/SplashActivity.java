package com.ezzat.spofi.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.ezzat.spofi.Control.NotificationService;
import com.ezzat.spofi.Control.Utils;
import com.ezzat.spofi.R;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
        super.onCreate(savedInstanceState);

        startService(new Intent(this, NotificationService.class));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.launchActivity(getApplicationContext(), LoginActivity.class, null);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
