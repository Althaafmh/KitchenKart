package com.PROJECT.kitchenkart.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PROJECT.kitchenkart.Adapters.CartAdapter;
import com.PROJECT.kitchenkart.Models.CartItem;
import com.PROJECT.kitchenkart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * @noinspection ALL
 */
public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView rvCheckoutItems;
    private TextView tvSubtotal, tvShipping, tvTotal;
    private Button btnPlaceOrder;
    private ProgressBar progressBar;

    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private CollectionReference cartRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        cartRef = db.collection("users").document(currentUser.getUid()).collection("cart");

        // Initialize UI components
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Checkout");
        }

        rvCheckoutItems = findViewById(R.id.rv_checkout_items);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvShipping = findViewById(R.id.tv_shipping);
        tvTotal = findViewById(R.id.tv_total);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        progressBar = findViewById(R.id.progressBar);

        // Setup RecyclerView
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems);
        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        rvCheckoutItems.setAdapter(cartAdapter);

        // Load cart items and calculate totals
        loadCartForCheckout();

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void loadCartForCheckout() {
        progressBar.setVisibility(View.VISIBLE);
        cartRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            progressBar.setVisibility(View.GONE);
            cartItems.clear();
            double subtotal = 0;
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                CartItem item = document.toObject(CartItem.class);
                cartItems.add(item);
                subtotal += item.getPrice() * item.getQuantity();
            }
            cartAdapter.notifyDataSetChanged();

            double shipping = 50.00; // Example shipping cost
            double total = subtotal + shipping;

            tvSubtotal.setText(String.format("Rs. %.2f", subtotal));
            tvShipping.setText(String.format("Rs. %.2f", shipping));
            tvTotal.setText(String.format("Rs. %.2f", total));

            btnPlaceOrder.setEnabled(!cartItems.isEmpty());
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error loading cart items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void placeOrder() {
        // TODO: Implement the logic to place the order
        // This would involve:
        // 1. Creating an Order object in a 'orders' collection
        // 2. Clearing the user's cart in Firestore
        // 3. Notifying the user and the seller
        Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
