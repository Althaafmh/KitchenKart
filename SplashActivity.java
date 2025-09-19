package com.PROJECT.kitchenkart.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.PROJECT.kitchenkart.R; // Make sure R is correctly imported

/**
 * SplashActivity displays a splash screen for a short duration
 * before navigating to the main activity of the application.
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    // Duration of the splash screen in milliseconds
    private static final long SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to your splash screen layout
        // You'll need to create a layout file named 'activity_splash.xml' in res/layout
        setContentView(R.layout.activity_splash);

        // Use a Handler to delay the transition to the next activity
        new Handler().postDelayed(() -> {
            // Create an Intent to start the next activity (e.g., LoginActivity or MainActivity)
            // You might want to add logic here to check if the user is already logged in
            // and navigate to BuyerDashboardActivity or SellerDashboardActivity accordingly.
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);

            // Close the SplashActivity to prevent the user from navigating back to it
            finish();
        }, SPLASH_DURATION);
    }
}