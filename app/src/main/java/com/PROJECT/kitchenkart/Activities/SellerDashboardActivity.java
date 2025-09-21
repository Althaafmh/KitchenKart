package com.PROJECT.kitchenkart.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.PROJECT.kitchenkart.Fragments.ChatFragment;
import com.PROJECT.kitchenkart.Fragments.CurrentOrdersFragment;
import com.PROJECT.kitchenkart.Fragments.SalesAnalyticsFragment;
import com.PROJECT.kitchenkart.Fragments.SellerHomeFragment;
import com.PROJECT.kitchenkart.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/** @noinspection ALL*/
public class SellerDashboardActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNav;
    private FloatingActionButton fabAddProduct;

    private SellerHomeFragment sellerHomeFragment; // Seller Home Fragment එක declare කරන්න.
    private ChatFragment chatFragment;
    private CurrentOrdersFragment currentOrdersFragment;
    private SalesAnalyticsFragment salesAnalyticsFragment;

    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seller_dashboard);

        userRole = getIntent().getStringExtra("USER_ROLE");

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                    return insets;
                });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(this::onNavigationItemSelected);

        fabAddProduct = findViewById(R.id.fab_add_product);
        fabAddProduct.setOnClickListener(v ->
                startActivity(new Intent(this, AddProductActivity.class)));

        if (savedInstanceState == null) {
            // Seller dashboard එකේදී HomeFragment එක නොව SellerHomeFragment එක load කරයි.
            sellerHomeFragment = new SellerHomeFragment();
            loadFragment(sellerHomeFragment);
            bottomNav.setSelectedItemId(R.id.nav_home);
            toolbar.setTitle(R.string.bottom_nav_home);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        int id = item.getItemId();
        if (id == R.id.nav_home) {
            if (sellerHomeFragment == null) {
                sellerHomeFragment = new SellerHomeFragment();
            }
            fragment = sellerHomeFragment;
            toolbar.setTitle(R.string.bottom_nav_home);
        } else if (id == R.id.nav_manage_products) {
            // "Manage Products" button එකට SellerHomeFragment එක load කරන්න.
            if (sellerHomeFragment == null) {
                sellerHomeFragment = new SellerHomeFragment();
            }
            fragment = sellerHomeFragment;
            toolbar.setTitle(R.string.bottom_nav_manage_products);
        } else if (id == R.id.nav_seller_orders) {
            if (currentOrdersFragment == null) {
                currentOrdersFragment = new CurrentOrdersFragment();
            }
            fragment = currentOrdersFragment;
            toolbar.setTitle(R.string.nav_seller_orders);
        } else if (id == R.id.nav_sales_analytics) {
            if (salesAnalyticsFragment == null) {
                salesAnalyticsFragment = new SalesAnalyticsFragment();
            }
            fragment = salesAnalyticsFragment;
            toolbar.setTitle(R.string.nav_sales_analytics);
        } else if (id == R.id.nav_seller_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.nav_messages) {
            if (chatFragment == null) {
                chatFragment = new ChatFragment();
            }
            fragment = chatFragment;
            toolbar.setTitle("Messages");
        }

        if (fragment != null) {
            loadFragment(fragment);
            return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.fragment_container, fragment);
        tx.commit();
    }
}
