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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PROJECT.kitchenkart.Activities.ProductDetailActivity;
import com.PROJECT.kitchenkart.Adapters.BuyerProductAdapter;
import com.PROJECT.kitchenkart.Adapters.CategoryAdapter;
import com.PROJECT.kitchenkart.Models.Category;
import com.PROJECT.kitchenkart.Models.Product;
import com.PROJECT.kitchenkart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment for the buyer's home screen.
 * Displays a list of categories and all available products.
 * @noinspection ALL
 */
public class HomeFragment extends Fragment {

    private static final String ARG_USER_ROLE = "user_role";
    private String userRole;
    private BuyerProductAdapter buyerProductAdapter;
    private static List<Product> allProducts;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private CollectionReference productsCollection;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String userRole) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ROLE, userRole);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userRole = getArguments().getString(ARG_USER_ROLE);
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        productsCollection = db.collection("products");

        if (allProducts == null) {
            allProducts = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_updated_unified, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Buyer UI elements
        View buyerContentContainer = view.findViewById(R.id.buyer_content_container);
        View sellerContentContainer = view.findViewById(R.id.seller_content_container);

        // This fragment only shows the buyer's view
        buyerContentContainer.setVisibility(View.VISIBLE);
        sellerContentContainer.setVisibility(View.GONE);

        TextView welcomeMessage = view.findViewById(R.id.welcome_message);
        welcomeMessage.setText("ආයුබෝවන්, KitchenKart වෙත සාදරයෙන් පිළිගනිමු.");

        RecyclerView rvCategories = view.findViewById(R.id.rv_categories);
        RecyclerView rvProducts = view.findViewById(R.id.rv_products);

        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvCategories.setLayoutManager(categoryLayoutManager);

        LinearLayoutManager productLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvProducts.setLayoutManager(productLayoutManager);

        Button btnFavorites = view.findViewById(R.id.btn_favorites);
        Button btnHistory = view.findViewById(R.id.btn_history);
        Button btnFollowing = view.findViewById(R.id.btn_following);
        Button btnOther = view.findViewById(R.id.btn_orders);

        btnFavorites.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FavoritesFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnHistory.setOnClickListener(v -> Toast.makeText(getContext(), "History clicked", Toast.LENGTH_SHORT).show());
        btnFollowing.setOnClickListener(v -> Toast.makeText(getContext(), "Following clicked", Toast.LENGTH_SHORT).show());
        btnOther.setOnClickListener(v -> Toast.makeText(getContext(), "Other clicked", Toast.LENGTH_SHORT).show());

        populateCategories(rvCategories);
        populateProducts(rvProducts);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAllProducts();
    }

    private void loadAllProducts() {
        if (getContext() == null || mAuth.getCurrentUser() == null) return;

        productsCollection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allProducts.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
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

    private void populateProducts(RecyclerView rvProducts) {
        BuyerProductAdapter.OnItemClickListener listener = product -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
                startActivity(intent);
            }
        };

        buyerProductAdapter = new BuyerProductAdapter(allProducts, listener);
        rvProducts.setAdapter(buyerProductAdapter);
    }

    public static List<Product> getAllProducts() {
        return allProducts;
    }

    // The nested adapters have been moved to their own files for clarity.
}
