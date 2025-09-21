package com.PROJECT.kitchenkart.Activities;

import android.content.Intent; // Make sure Intent is imported
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.PROJECT.kitchenkart.Fragments.BuyerOrdersFragment;
import com.PROJECT.kitchenkart.Fragments.CartFragment;
import com.PROJECT.kitchenkart.Fragments.HomeFragment;
// No longer directly importing ProfileFragment if ProfileActivity is used instead
// import com.PROJECT.kitchenkart.Fragments.ProfileFragment;
import com.PROJECT.kitchenkart.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/** @noinspection ALL*/
public class BuyerDashboardActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buyer_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.buyer_dashboard_title);
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);

        if (savedInstanceState == null) {
            // Load the default fragment (HomeFragment) when the activity is created
            loadFragment(new HomeFragment());
            // Set the selected item in BottomNavigationView if you want "Home" to be visually selected on start
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            loadFragment(new HomeFragment());
            toolbar.setTitle(R.string.bottom_nav_home);
            return true;
        } else if (itemId == R.id.nav_cart) {
            loadFragment(new CartFragment());
            toolbar.setTitle(R.string.bottom_nav_cart);
            return true;
        } else if (itemId == R.id.nav_orders) {
            loadFragment(new BuyerOrdersFragment());
            toolbar.setTitle(R.string.bottom_nav_orders);
            return true;
        } else if (itemId == R.id.nav_profile) {
            // LAUNCH PROFILEACTIVITY AS A SEPARATE ACTIVITY
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            // DO NOT load a fragment if you're launching a new activity
            // If you still want the bottom nav item to appear selected when returning,
            // you might need to handle onResume or onRestart, but for simple navigation,
            // launching the activity is enough.
            return true; // Mark as handled
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}