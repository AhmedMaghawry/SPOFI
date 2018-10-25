package com.ezzat.spofi;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class SPOFI extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
