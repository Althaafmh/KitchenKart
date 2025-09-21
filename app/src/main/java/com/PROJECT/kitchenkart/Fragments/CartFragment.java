package com.PROJECT.kitchenkart.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PROJECT.kitchenkart.Activities.CheckoutActivity;
import com.PROJECT.kitchenkart.Activities.LoginActivity;
import com.PROJECT.kitchenkart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment for displaying the user's shopping cart.
 * This modern version includes an empty state view and Firebase integration.
 * @noinspection ALL
 */
public class CartFragment extends Fragment {

    private RecyclerView rvCartItems;
    private TextView tvCartTotal;
    private Button btnCheckout;
    private LinearLayout emptyStateLayout;
    private ProgressBar progressBar;

    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private CollectionReference cartRef;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Check if user is logged in
        if (currentUser == null) {
            Toast.makeText(getContext(), "Please log in to view your cart.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            if (getActivity() != null) {
                getActivity().finish();
            }
            return view;
        }

        cartRef = db.collection("users").document(currentUser.getUid()).collection("cart");

        // Initialize UI components
        rvCartItems = view.findViewById(R.id.rv_cart_items);
        tvCartTotal = view.findViewById(R.id.tv_cart_total);
        btnCheckout = view.findViewById(R.id.btn_checkout);
        emptyStateLayout = view.findViewById(R.id.empty_state_layout);
        progressBar = view.findViewById(R.id.progressBar); // Assuming progressBar exists in fragment_cart.xml

        // Setup RecyclerView
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems);
        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCartItems.setAdapter(cartAdapter);

        // Set listeners
        btnCheckout.setOnClickListener(v -> {
            if (!cartItems.isEmpty()) {
                Intent intent = new Intent(getActivity(), CheckoutActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.btn_browse_food).setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
            }
        });

        // Load cart items on fragment creation
        loadCartItems();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload cart items every time the fragment becomes visible
        loadCartItems();
    }

    private void loadCartItems() {
        progressBar.setVisibility(View.VISIBLE);
        cartRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            progressBar.setVisibility(View.GONE);
            cartItems.clear();
            double total = 0;
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                CartItem item = document.toObject(CartItem.class);
                cartItems.add(item);
                total += item.getPrice() * item.getQuantity();
            }
            cartAdapter.notifyDataSetChanged();
            tvCartTotal.setText(String.format("Total: Rs. %.2f", total));

            if (cartItems.isEmpty()) {
                rvCartItems.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.VISIBLE);
                btnCheckout.setEnabled(false);
            } else {
                rvCartItems.setVisibility(View.VISIBLE);
                emptyStateLayout.setVisibility(View.GONE);
                btnCheckout.setEnabled(true);
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Error loading cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            rvCartItems.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(false);
        });
    }

    // You will need a CartItem model and a CartAdapter for this code to work
    // These are simplified placeholder classes for demonstration
    public static class CartItem {
        private String productId;
        private String productName;
        private double price;
        private int quantity;
        private String productImageUrl;

        public CartItem() {}

        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public String getProductImageUrl() { return productImageUrl; }

        public void setProductId(String productId) { this.productId = productId; }
        public void setProductName(String productName) { this.productName = productName; }
        public void setPrice(double price) { this.price = price; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
    }

    public static class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
        private final List<CartItem> items;
        public CartAdapter(List<CartItem> items) { this.items = items; }
        @NonNull @Override public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
            return new CartViewHolder(view);
        }
        @Override public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
            // Bind your data here
        }
        @Override public int getItemCount() { return items.size(); }
        static class CartViewHolder extends RecyclerView.ViewHolder {
            public CartViewHolder(@NonNull View itemView) { super(itemView); }
        }
    }
}
