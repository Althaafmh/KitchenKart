package com.PROJECT.kitchenkart.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.PROJECT.kitchenkart.R;

public class SellerSettingsActivity extends AppCompatActivity {

    private EditText etStoreName, etSellerName, etSellerEmail, etSellerPhone;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchNotifications, switchEmailNotifications, switchSmsNotifications;
    private Button btnSaveProfile, btnChangePassword, btnLogout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seller_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        setupToolbar();
        sharedPreferences = getSharedPreferences("SellerPrefs", MODE_PRIVATE);
        loadSavedSettings();
        setupClickListeners();
    }
    private void initViews() {
        etStoreName = findViewById(R.id.etStoreName);
        etSellerName = findViewById(R.id.etSellerName);
        etSellerEmail = findViewById(R.id.etSellerEmail);
        etSellerPhone = findViewById(R.id.etSellerPhone);

        switchNotifications = findViewById(R.id.switchNotifications);
        switchEmailNotifications = findViewById(R.id.switchEmailNotifications);
        switchSmsNotifications = findViewById(R.id.switchSmsNotifications);

        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);
    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Seller Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
    private void loadSavedSettings() {

        etStoreName.setText(sharedPreferences.getString("storeName", ""));
        etSellerName.setText(sharedPreferences.getString("sellerName", ""));
        etSellerEmail.setText(sharedPreferences.getString("sellerEmail", ""));
        etSellerPhone.setText(sharedPreferences.getString("sellerPhone", ""));


        switchNotifications.setChecked(sharedPreferences.getBoolean("notifications", true));
        switchEmailNotifications.setChecked(sharedPreferences.getBoolean("emailNotifications", true));
        switchSmsNotifications.setChecked(sharedPreferences.getBoolean("smsNotifications", false));
    }
    private void setupClickListeners() {
        btnSaveProfile.setOnClickListener(v -> saveProfileSettings());

        btnChangePassword.setOnClickListener(v -> {

            Intent intent = new Intent(SellerSettingsActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
        btnLogout.setOnClickListener(v -> logoutSeller());
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchEmailNotifications.setEnabled(isChecked);
            switchSmsNotifications.setEnabled(isChecked);

            if (!isChecked) {
                switchEmailNotifications.setChecked(false);
                switchSmsNotifications.setChecked(false);
            }
        });
    }
    private void saveProfileSettings() {

        if (etStoreName.getText().toString().trim().isEmpty()) {
            etStoreName.setError("Store name is required");
            return;
        }

        if (etSellerName.getText().toString().trim().isEmpty()) {
            etSellerName.setError("Seller name is required");
            return;
        }

        if (etSellerEmail.getText().toString().trim().isEmpty()) {
            etSellerEmail.setError("Email is required");
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("storeName", etStoreName.getText().toString().trim());
        editor.putString("sellerName", etSellerName.getText().toString().trim());
        editor.putString("sellerEmail", etSellerEmail.getText().toString().trim());
        editor.putString("sellerPhone", etSellerPhone.getText().toString().trim());

        editor.putBoolean("notifications", switchNotifications.isChecked());
        editor.putBoolean("emailNotifications", switchEmailNotifications.isChecked());
        editor.putBoolean("smsNotifications", switchSmsNotifications.isChecked());

        editor.apply();

        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void logoutSeller() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();


        Intent intent = new Intent(SellerSettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Optional: Add any cleanup or confirmation before going back
    }
}