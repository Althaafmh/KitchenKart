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
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.PROJECT.kitchenkart.Models.Product; // Use the correct Product model
import com.PROJECT.kitchenkart.R;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * මෙම Activity එක මගින් seller කෙනෙකුට අලුත් ආහාර නිෂ්පාදන එකතු කිරීමට හැකියාව ලැබේ.
 * ආකෘතියේ දත්ත ඇතුළත් කිරීම, image එකක් තෝරා ගැනීම, Cloudinary වෙත image එක upload කිරීම,
 * සහ නිෂ්පාදනයේ දත්ත Firebase Firestore වෙත ගබඩා කිරීම මෙහිදී සිදු කෙරේ.
 * @noinspection ALL
 */
public class AddProductActivity extends AppCompatActivity {

    // UI elements
    private TextInputEditText etProductName, etDescription, etPrice, etQuantity;
    private ShapeableImageView ivProductImage;
    private Button btnUploadImage, btnSubmit, btnAddMoreProducts;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private CollectionReference productsCollection;

    // Cloudinary configuration (දැනට මෙය Hard-code කර ඇත, Production app එකකදී Constants භාවිතා කරන්න)
    private static final String CLOUDINARY_UPLOAD_PRESET = "kitchnkart_image"; // Replace with your actual unsigned preset
    private Uri selectedImageUri;

    // Activity Result Launchers
    private ActivityResultLauncher<String[]> requestPermissionsLauncher;
    private ActivityResultLauncher<String> pickImageFromGalleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Check if seller is logged in
        if (currentUser == null) {
            startActivity(new Intent(AddProductActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Get Firestore collection reference
        productsCollection = db.collection("products");

        // Initialize UI components (IDs corrected to match a common naming convention)
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etProductName = findViewById(R.id.et_product_name);
        etDescription = findViewById(R.id.et_description);
        etPrice = findViewById(R.id.et_price);
        etQuantity = findViewById(R.id.et_quantity);
        ivProductImage = findViewById(R.id.iv_product_image);
        btnUploadImage = findViewById(R.id.btn_upload_image);
        btnSubmit = findViewById(R.id.btn_submit);
        btnAddMoreProducts = findViewById(R.id.btn_add_more_products);
        progressBar = findViewById(R.id.progressBar);

        // --- Initialize ActivityResultLaunchers ---
        requestPermissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    boolean allGranted = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        allGranted = permissions.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false);
                    } else {
                        allGranted = permissions.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false);
                    }

                    if (allGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(AddProductActivity.this, "Permission denied. Cannot access gallery.", Toast.LENGTH_LONG).show();
                    }
                }
        );

        pickImageFromGalleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                imageUri -> {
                    if (imageUri != null) {
                        selectedImageUri = imageUri;
                        ivProductImage.setImageURI(imageUri);
                        btnUploadImage.setText("Image Selected");
                    } else {
                        Toast.makeText(AddProductActivity.this, "No image selected.", Toast.LENGTH_SHORT).show();
                        btnUploadImage.setText("Upload Image");
                    }
                }
        );

        // --- Set Listeners ---
        btnUploadImage.setOnClickListener(v -> checkAndRequestImagePermissions());
        btnSubmit.setOnClickListener(v -> addNewProduct());
        btnAddMoreProducts.setOnClickListener(v -> resetForm());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void resetForm() {
        etProductName.setText("");
        etDescription.setText("");
        etPrice.setText("");
        etQuantity.setText("");
        ivProductImage.setImageResource(R.drawable.ic_image_placeholder); // Placeholder image
        selectedImageUri = null;
        btnUploadImage.setText("Upload Image");
        etProductName.requestFocus();
        Toast.makeText(this, "Form cleared. Ready to add a new product.", Toast.LENGTH_SHORT).show();
    }

    private void addNewProduct() {
        String name = etProductName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etProductName.setError("Product Name is required");
            etProductName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(priceStr)) {
            etPrice.setError("Price is required");
            etPrice.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(quantityStr)) {
            etQuantity.setError("Quantity is required");
            etQuantity.requestFocus();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);

            progressBar.setVisibility(View.VISIBLE);
            btnSubmit.setEnabled(false);

            if (selectedImageUri != null) {
                uploadImageToCloudinary(name, description, price, quantity);
            } else {
                Toast.makeText(this, "Please upload a product image", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format for price or quantity", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            btnSubmit.setEnabled(true);
        }
    }

    private void uploadImageToCloudinary(String name, String description, double price, int quantity) {
        MediaManager.get().upload(selectedImageUri)
                .unsigned(CLOUDINARY_UPLOAD_PRESET)
                .option("folder", "kitchenkart/product_images")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Toast.makeText(AddProductActivity.this, "Uploading image...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Optional: Update progress bar
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = (String) resultData.get("secure_url");
                        saveProductToFirestore(name, description, price, quantity, imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        progressBar.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                        Toast.makeText(AddProductActivity.this, "Image upload failed: " + error.getDescription(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                    }
                }).dispatch();
    }

    private void saveProductToFirestore(String name, String description, double price, int quantity, String imageUrl) {
        // Use the Product model and correctly set sellerId
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setSellerId(currentUser.getUid()); // Correctly set sellerId
        product.setImageUrl(imageUrl);

        productsCollection.add(product)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    Toast.makeText(AddProductActivity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                    resetForm();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    Toast.makeText(AddProductActivity.this, "Error adding product: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void checkAndRequestImagePermissions() {
        List<String> permissionsToRequest = new ArrayList<>();
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
            openGallery();
        }
    }

    private void openGallery() {
        pickImageFromGalleryLauncher.launch("image/*");
    }
}
