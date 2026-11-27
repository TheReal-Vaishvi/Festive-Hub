package com.example.festivehub;

import android.app.Application;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class FestiveHubApp extends Application {

    //this file is used to initialize cloudinary for my app
    @Override
    public void onCreate() {
        super.onCreate();

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "svwfnzkm");

        MediaManager.init(this, config);
    }
}
