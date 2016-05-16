package com.example.sunilkumarlakkad.travelmate;

import android.app.Application;

import com.firebase.client.Firebase;

public class ApplicationFirebase extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        // other setup code
    }

}
