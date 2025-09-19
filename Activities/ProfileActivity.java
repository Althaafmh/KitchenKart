package com.PROJECT.kitchenkart.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.PROJECT.kitchenkart.Models.User;
import com.PROJECT.kitchenkart.R;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity for managing and updating the user's profile.
 * This includes updating personal details and their profile picture using Cloudinary.
 * @noinspection ALL
 */
public class ProfileActivity extends AppCompatActivity {

    // UI elements
    private TextView tvEmail;
    private TextInputEditText etName, etPhone, etAddress;
    private Button btnUpdateProfile;
    private ProgressBar progressBar;
    private ShapeableImageView ivProfilePicture;
    private TextView tvChangeProfilePicture;
    private TextView tvMessageCount;
    private LinearLayout cardMessage;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private DocumentReference userDocRef;

    // Cloudinary configuration
    private static final String CLOUDINARY_UPLOAD_PRESET ="kitchnkart_image";

    // Activity Result Launchers for permissions and image picking
    private ActivityResultLauncher<String[]> requestPermissionsLauncher;
    private ActivityResultLauncher<String> pickImageFromGalleryLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri tempImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        userDocRef = db.collection("users").document(currentUser.getUid());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvEmail = findViewById(R.id.tvEmail);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        progressBar = findViewById(R.id.progressBar);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvChangeProfilePicture = findViewById(R.id.tvChangeProfilePicture);
        tvMessageCount = findViewById(R.id.tvMessageCount);
        cardMessage = findViewById(R.id.cardMessage);

        requestPermissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    boolean allGranted = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        allGranted = Boolean.TRUE.equals(permissions.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false)) &&
                                permissions.getOrDefault(Manifest.permission.CAMERA, false);
                    } else {
                        allGranted = permissions.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false) &&
                                permissions.getOrDefault(Manifest.permission.CAMERA, false);
                    }

                    if (allGranted) {
                        showImageSourceDialog();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Permissions denied. Cannot access gallery or camera.", Toast.LENGTH_LONG).show();
                    }
                }
        );

        pickImageFromGalleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                imageUri -> {
                    if (imageUri != null) {
                        ivProfilePicture.setImageURI(imageUri);
                        uploadImageToCloudinary(imageUri);
                    } else {
                        Toast.makeText(ProfileActivity.this, "No image selected.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success) {
                        ivProfilePicture.setImageURI(tempImageUri);
                        uploadImageToCloudinary(tempImageUri);
                    } else {
                        Toast.makeText(ProfileActivity.this, "No photo taken.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btnUpdateProfile.setOnClickListener(v -> updateUserProfile());
        ivProfilePicture.setOnClickListener(v -> checkAndRequestImagePermissions());
        tvChangeProfilePicture.setOnClickListener(v -> checkAndRequestImagePermissions());

        // Message icon එක click කළ විට ChatListActivity එක load කරන්න.
        // This is the added logic to connect the button to the new activity.
        if (cardMessage != null) {
            cardMessage.setOnClickListener(v -> {
                Intent chatListIntent = new Intent(ProfileActivity.this, ChatListActivity.class);
                startActivity(chatListIntent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadUserProfile() {
        if (currentUser == null) return;
        progressBar.setVisibility(View.VISIBLE);
        tvEmail.setText(currentUser.getEmail());

        userDocRef.get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        etName.setText(user.getName());
                        etPhone.setText(user.getPhone());
                        etAddress.setText(user.getAddress());

                        if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
                            Glide.with(ProfileActivity.this)
                                    .load(user.getProfilePictureUrl())
                                    .placeholder(R.drawable.ic_person_24)
                                    .error(R.drawable.ic_person_24)
                                    .into(ivProfilePicture);
                        } else {
                            ivProfilePicture.setImageResource(R.drawable.ic_person_24);
                        }
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "User profile data not found. Please complete your details.", Toast.LENGTH_LONG).show();
                    ivProfilePicture.setImageResource(R.drawable.ic_person_24);
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Error loading profile: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                ivProfilePicture.setImageResource(R.drawable.ic_person_24);
            }
        });
    }

    private void updateUserProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Full Name is required");
            etName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Delivery Address is required");
            etAddress.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnUpdateProfile.setEnabled(false);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);
        updates.put("address", address);

        userDocRef.update(updates)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnUpdateProfile.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Error updating profile: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkAndRequestImagePermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toArray(new String[0]));
        } else {
            showImageSourceDialog();
        }
    }

    private void showImageSourceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Select Image Source")
                .setItems(new CharSequence[]{"Gallery", "Camera"}, (dialog, which) -> {
                    if (which == 0) {
                        openGallery();
                    } else {
                        Toast.makeText(this, "Camera not yet implemented.", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void openGallery() {
        pickImageFromGalleryLauncher.launch("image/*");
    }

    private void uploadImageToCloudinary(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(this, "No image to upload.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        MediaManager.get().upload(imageUri)
                .unsigned(CLOUDINARY_UPLOAD_PRESET)
                .option("resource_type", "image")
                .option("folder", "kitchenkart/profile_pictures")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Toast.makeText(ProfileActivity.this, "Uploading image...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Optional: Update progress bar
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = (String) resultData.get("secure_url");
                        updateProfilePictureUrlInFirestore(imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this, "Image upload failed: " + error.getDescription(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                    }
                }).dispatch();
    }

    private void updateProfilePictureUrlInFirestore(String url) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("profilePictureUrl", url);

        userDocRef.update(updates)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Profile picture URL saved!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to save picture URL to profile: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                    }
                });
    }
}