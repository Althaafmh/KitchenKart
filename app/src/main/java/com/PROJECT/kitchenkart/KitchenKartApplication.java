package com.PROJECT.kitchenkart;

import android.app.Application;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

/** @noinspection ALL*/
public class KitchenKartApplication extends Application {

    // Your Cloudinary upload preset name
    public static final String CLOUDINARY_UPLOAD_PRESET = "kitchnkart_image";

    @Override
    public void onCreate() {
        super.onCreate();


        // Initialize Cloudinary
        // ඔබගේ Cloudinary ගිණුමේ තොරතුරු මෙතැනට ඇතුළත් කරන්න.
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dflqwbglc"); // Replace with your Cloudinary cloud name
        config.put("api_key", "134611849111475"); // Replace with your Cloudinary API key
        config.put("api_secret", "z_mvEl8W-E6pKRjun8TGQ6BSmX4"); // Replace with your Cloudinary API secret
        MediaManager.init(this, config);
    }
}
