// File: HomeFragment.java
package com.PROJECT.kitchenkart.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PROJECT.kitchenkart.Activities.BuyerDashboardActivity;
import com.PROJECT.kitchenkart.Activities.ProductDetailActivity;
import com.PROJECT.kitchenkart.Adapters.BuyerProductAdapter;
import com.PROJECT.kitchenkart.Adapters.CategoryAdapter;
import com.PROJECT.kitchenkart.Models.Category;
import com.PROJECT.kitchenkart.Models.Product;
import com.PROJECT.kitchenkart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment for the buyer's home screen.
 * Displays a list of categories and all available products.
 *
 * @noinspection ALL
 */
public class HomeFragment extends Fragment {

    // Removed static modifier from allProducts to prevent unexpected behavior
    private List<Product> allProducts;
    private BuyerProductAdapter buyerProductAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private CollectionReference productsCollection;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        productsCollection = db.collection("products");

        // Initialize the product list here
        allProducts = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_updated_unified, container, false);

        // This fragment only shows the buyer's view
        View buyerContentContainer = view.findViewById(R.id.buyer_content_container);
        View sellerContentContainer = view.findViewById(R.id.seller_content_container);

        if (buyerContentContainer != null) {
            buyerContentContainer.setVisibility(View.VISIBLE);
        }
        if (sellerContentContainer != null) {
            sellerContentContainer.setVisibility(View.GONE);
        }

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView welcomeMessage = view.findViewById(R.id.welcome_message);
        if (welcomeMessage != null) {
            welcomeMessage.setText("ආයුබෝවන්, KitchenKart වෙත සාදරයෙන් පිළිගනිමු.");
        }

        RecyclerView rvCategories = view.findViewById(R.id.rv_categories);
        RecyclerView rvProducts = view.findViewById(R.id.rv_products);

        if (rvCategories != null) {
            LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rvCategories.setLayoutManager(categoryLayoutManager);
            populateCategories(rvCategories);
        }

        if (rvProducts != null) {
            LinearLayoutManager productLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rvProducts.setLayoutManager(productLayoutManager);

            // Initialize the adapter once, then update its data as needed.
            BuyerProductAdapter.OnItemClickListener listener = product -> {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                    // Pass the entire Product object for detailed view
                    intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
                    startActivity(intent);
                }
            };
            buyerProductAdapter = new BuyerProductAdapter(allProducts, listener);
            rvProducts.setAdapter(buyerProductAdapter);
        }

        Button btnFavorites = view.findViewById(R.id.btn_favorites);
        if (btnFavorites != null) {
            btnFavorites.setOnClickListener(v -> {
                // You can add logic to navigate to a Favorites screen here
                Toast.makeText(getContext(), "Favorites button clicked", Toast.LENGTH_SHORT).show();
            });
        }

        Button btnHistory = view.findViewById(R.id.btn_history);
        if (btnHistory != null) {
            btnHistory.setOnClickListener(v -> Toast.makeText(getContext(), "History clicked", Toast.LENGTH_SHORT).show());
        }

        Button btnFollowing = view.findViewById(R.id.btn_following);
        if (btnFollowing != null) {
            btnFollowing.setOnClickListener(v -> {
                if (getActivity() instanceof BuyerDashboardActivity) {
                    // Assuming FollowingFragment exists and the dashboard can load it
                    ((BuyerDashboardActivity) getActivity()).loadFragment(new FollowingFragment());
                    if (((BuyerDashboardActivity) getActivity()).getSupportActionBar() != null) {
                        ((BuyerDashboardActivity) getActivity()).getSupportActionBar().setTitle(R.string.following_title);
                    }
                }
            });
        }

        Button btnOther = view.findViewById(R.id.btn_orders);
        if (btnOther != null) {
            btnOther.setOnClickListener(v -> Toast.makeText(getContext(), "Orders clicked", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load products onResume to ensure fresh data when returning to the fragment
        loadAllProducts();
    }

    private void loadAllProducts() {
        currentUser = mAuth.getCurrentUser();
        if (getContext() == null || currentUser == null) {
            // Handle case where user is not logged in or context is null
            if (buyerProductAdapter != null) {
                allProducts.clear();
                buyerProductAdapter.notifyDataSetChanged();
            }
            return;
        }

        // It is crucial that the Products collection has read permissions for authenticated users
        productsCollection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allProducts.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            product.setId(document.getId());
                            allProducts.add(product);
                        }
                        if (buyerProductAdapter != null) {
                            buyerProductAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error loading products: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void populateCategories(RecyclerView rvCategories) {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Breakfast", R.drawable.ic_breakfast));
        categories.add(new Category("Curries", R.drawable.ic_curries));
        categories.add(new Category("Sweets", R.drawable.ic_sweets));
        categories.add(new Category("Rice", R.drawable.ic_rice));

        CategoryAdapter categoryAdapter = new CategoryAdapter(categories);
        rvCategories.setAdapter(categoryAdapter);
    }
}