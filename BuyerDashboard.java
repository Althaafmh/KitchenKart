package com.example.kitchenkart; // Update with your actual package name

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    // UI Components
    private EditText searchBar;
    private Button favoritesButton, historyButton, followingButton;
    private ImageView bannerImage;
    private GridLayout productGrid;
    private LinearLayout categoriesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Make sure your XML file is named activity_home.xml

        // Initialize UI components
        initializeViews();
        
        // Set up event listeners
        setupClickListeners();
        
        // Set up search functionality
        setupSearchBar();
        
        // Load dynamic content (if needed)
        loadContent();
    }

    private void initializeViews() {
        // Find views by their IDs
        searchBar = findViewById(R.id.searchBar);
        favoritesButton = findViewById(R.id.favoritesButton); // You need to add IDs to your buttons in XML
        historyButton = findViewById(R.id.historyButton);     // Add android:id="@+id/historyButton"
        followingButton = findViewById(R.id.followingButton); // Add android:id="@+id/followingButton"
        bannerImage = findViewById(R.id.bannerImage);
        productGrid = findViewById(R.id.productGrid); // Add android:id="@+id/productGrid" to your GridLayout
        
        // Note: You should add IDs to your buttons in XML like:
        // android:id="@+id/favoritesButton"
        // android:id="@+id/historyButton" 
        // android:id="@+id/followingButton"
    }

    private void setupClickListeners() {
        // Favorites button click listener
        favoritesButton.setOnClickListener(v -> {
            // Navigate to favorites screen or show favorites
            // Example: startActivity(new Intent(HomeActivity.this, FavoritesActivity.class));
        });

        // History button click listener
        historyButton.setOnClickListener(v -> {
            // Navigate to history screen
        });

        // Following button click listener
        followingButton.setOnClickListener(v -> {
            // Navigate to following screen
        });

        // Banner image click listener (if needed)
        bannerImage.setOnClickListener(v -> {
            // Handle banner click (e.g., open promotion details)
        });

        // Category click listeners (you'll need to add IDs to your category layouts)
        setupCategoryClickListeners();
        
        // Product item click listeners
        setupProductClickListeners();
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter products based on search query
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void filterProducts(String query) {
        // Implement product filtering logic here
        // This would typically filter the products in your GridLayout
        // based on the search query
    }

    private void setupCategoryClickListeners() {
        // You need to add IDs to your category LinearLayouts in XML
        // Example: android:id="@+id/categoryBreakfast"
        
        // Find category layouts and set click listeners
        LinearLayout breakfastCategory = findViewById(R.id.categoryBreakfast);
        LinearLayout curriesCategory = findViewById(R.id.categoryCurries);
        LinearLayout sweetsCategory = findViewById(R.id.categorySweets);

        if (breakfastCategory != null) {
            breakfastCategory.setOnClickListener(v -> {
                // Filter products by breakfast category
                filterByCategory("Breakfast");
            });
        }

        if (curriesCategory != null) {
            curriesCategory.setOnClickListener(v -> {
                filterByCategory("Curries");
            });
        }

        if (sweetsCategory != null) {
            sweetsCategory.setOnClickListener(v -> {
                filterByCategory("Sweets");
            });
        }
    }

    private void setupProductClickListeners() {
        // Set click listeners for product items
        // You'll need to add IDs to your product LinearLayouts or handle dynamically
        
        // Example for the first product item:
        LinearLayout product1 = findViewById(R.id.product1); // Add ID to your product layout
        if (product1 != null) {
            product1.setOnClickListener(v -> {
                // Open product details for String Hoppers
                openProductDetails("String Hoppers", 60.00);
            });
        }
        
        // Add similar listeners for other products
    }

    private void filterByCategory(String category) {
        // Implement category-based filtering
        // This would show only products from the selected category
    }

    private void openProductDetails(String productName, double price) {
        // Navigate to product details activity
        /*
        Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
        intent.putExtra("productName", productName);
        intent.putExtra("productPrice", price);
        startActivity(intent);
        */
    }

    private void loadContent() {
        // Load dynamic content like:
        // - Banner images from server
        // - Product list from database/API
        // - User-specific data (favorites, history, etc.)
    }

    @Override
    protected void onResume() {
        super.onResume();
       
    }
}