package com.PROJECT.kitchenkart.Activities;

import android.content.Intent; // Import Intent for launching new activities
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PROJECT.kitchenkart.Adapters.FoodProductAdapter;
import com.PROJECT.kitchenkart.Models.FoodProduct;
import com.PROJECT.kitchenkart.Models.Product;
import com.PROJECT.kitchenkart.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** @noinspection ALL*/
public class ManageProductsActivity extends AppCompatActivity implements FoodProductAdapter.OnProductActionListener {

    private RecyclerView recyclerViewProducts;
    private FoodProductAdapter foodProductAdapter;
    private List<FoodProduct> productList;
    private FloatingActionButton fabAddProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_products);

        // Apply window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Manage Products"); // Set title
        }
        // Handle back button click
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize RecyclerView
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));

        // Initialize product list and adapter
        productList = new ArrayList<>();
        // Add sample Sri Lankan village food data
        populateSampleData();

        List<Product> favoritesList = Collections.emptyList();
        foodProductAdapter = new FoodProductAdapter(favoritesList); // 'this' refers to OnProductActionListener
        recyclerViewProducts.setAdapter(foodProductAdapter);

        // Initialize Floating Action Button
        fabAddProduct = findViewById(R.id.fabAddProduct);
        fabAddProduct.setOnClickListener(v -> {
            // Launch the AddProductActivity when the FAB is clicked
            startActivity(new Intent(ManageProductsActivity.this, AddProductActivity.class));
            Toast.makeText(ManageProductsActivity.this, "Add New Product Clicked!", Toast.LENGTH_SHORT).show();
        });
    }

    // This method populates a list with sample FoodProduct data.
// It is assumed that 'productList' is a List<FoodProduct> that has been initialized.
    private void populateSampleData() {
        // Clear the list first to prevent duplicate entries if the method is called multiple times.
        if (productList != null) {
            productList.clear();
        }

        // Add sample food products to the list.
        // The boolean return value of the add() method is not needed, so we don't assign it to a variable.
        productList.add(new FoodProduct("1", "Parippu Curry (Lentil Curry)", "A staple Sri Lankan lentil curry, creamy and flavorful.", 200.00, "https://example.com/parrippu.jpg"));
        productList.add(new FoodProduct("2", "Pol Sambol", "Freshly grated coconut mixed with chili, lime, and fish flakes (optional).", 150.00, "https://example.com/polsambol.jpg"));
        productList.add(new FoodProduct("3", "Kottu Roti", "Chopped flatbread stir-fried with vegetables, eggs, and meat (chicken/beef).", 500.00, "https://example.com/kottu.jpg"));
        productList.add(new FoodProduct("4", "Hoppers (Appa)", "Bowl-shaped pancakes, crispy on edges, soft in center, served with egg or plain.", 100.00, "https://example.com/hoppers.jpg"));
        productList.add(new FoodProduct("5", "String Hoppers (Indi Appa)", "Steamed rice flour noodles, often served with curries.", 180.00, "https://example.com/stringhoppers.jpg"));
        productList.add(new FoodProduct("6", "Watalappan", "A traditional Sri Lankan steamed coconut custard pudding.", 250.00, "https://example.com/watalappan.jpg"));
        productList.add(new FoodProduct("7", "Lamprais", "Rice and mixed meat/vegetable curry baked in a banana leaf.", 600.00, "https://example.com/lamprais.jpg"));
    }


    @Override
    public void onEditClick(FoodProduct product) {
        Toast.makeText(this, "Edit: " + product.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Start activity to edit product details, passing product ID/data
        // Example: Intent editIntent = new Intent(this, EditProductActivity.class);
        // editIntent.putExtra("product_id", product.getId());
        // startActivity(editIntent);
    }

    @Override
    public void onDeleteClick(FoodProduct product) {
        Toast.makeText(this, "Delete: " + product.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Implement deletion logic, e.g., show confirmation dialog, remove from list, update database
        // Example:
        // productList.remove(product);
        // foodProductAdapter.notifyDataSetChanged(); // Notify adapter that data has changed
    }
}