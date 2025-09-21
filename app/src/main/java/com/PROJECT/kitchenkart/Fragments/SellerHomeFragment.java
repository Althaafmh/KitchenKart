package com.PROJECT.kitchenkart.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PROJECT.kitchenkart.Activities.AddProductActivity;
import com.PROJECT.kitchenkart.Adapters.SellerProductAdapter;
import com.PROJECT.kitchenkart.Models.Product;
import com.PROJECT.kitchenkart.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass for the seller's home screen.
 * @noinspection ALL
 */
public class SellerHomeFragment extends Fragment {

    private RecyclerView rvSellerProducts;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddProduct;
    private TextView tvNoProducts;

    private SellerProductAdapter sellerProductAdapter;
    private List<Product> sellerProductsList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private CollectionReference productsRef;

    public SellerHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "Please log in to manage products.", Toast.LENGTH_SHORT).show();
            return view;
        }

        productsRef = db.collection("products");

        rvSellerProducts = view.findViewById(R.id.rv_seller_products);
        progressBar = view.findViewById(R.id.progressBar);
        fabAddProduct = view.findViewById(R.id.fab_add_product);
        tvNoProducts = view.findViewById(R.id.tvNoProducts);

        sellerProductsList = new ArrayList<>();
        sellerProductAdapter = new SellerProductAdapter(sellerProductsList, product -> {
            // Seller product click logic for editing
            Intent intent = new Intent(getActivity(), AddProductActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            startActivity(intent);
        });
        rvSellerProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSellerProducts.setAdapter(sellerProductAdapter);

        fabAddProduct.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddProductActivity.class));
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSellerProducts();
    }

    private void loadSellerProducts() {
        if (currentUser == null) return;

        progressBar.setVisibility(View.VISIBLE);
        tvNoProducts.setVisibility(View.GONE);
        sellerProductsList.clear();

        productsRef
                .whereEqualTo("sellerId", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            product.setId(document.getId());
                            sellerProductsList.add(product);
                        }
                        sellerProductAdapter.notifyDataSetChanged();

                        if (sellerProductsList.isEmpty()) {
                            tvNoProducts.setVisibility(View.VISIBLE);
                        } else {
                            tvNoProducts.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(getContext(), "Error loading products: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        tvNoProducts.setVisibility(View.VISIBLE);
                    }
                });
    }
}
