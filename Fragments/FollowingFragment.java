package com.PROJECT.kitchenkart.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.PROJECT.kitchenkart.Adapters.FollowingSellersAdapter;
import com.PROJECT.kitchenkart.Models.User;
import com.PROJECT.kitchenkart.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/** @noinspection ALL*/
public class FollowingFragment extends Fragment implements FollowingSellersAdapter.OnUnfollowListener {

    private static final String TAG = "FollowingFragment";

    private RecyclerView rvFollowingSellers;
    private ProgressBar progressBar;
    private TextInputEditText etSearch;
    private TextView tvTitle;

    private FollowingSellersAdapter adapter;
    private List<User> followingSellersList;
    private List<String> followingIds;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    public FollowingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize UI components
        rvFollowingSellers = view.findViewById(R.id.rv_following_sellers);
        progressBar = view.findViewById(R.id.progressBar);
        etSearch = view.findViewById(R.id.et_search);
        tvTitle = view.findViewById(R.id.tv_title);

        if (tvTitle != null) {
            tvTitle.setText(R.string.following_title);
        }

        followingSellersList = new ArrayList<>();
        followingIds = new ArrayList<>();
        adapter = new FollowingSellersAdapter(followingSellersList, this);

        if (rvFollowingSellers != null) {
            rvFollowingSellers.setLayoutManager(new LinearLayoutManager(getContext()));
            rvFollowingSellers.setAdapter(adapter);
        }

        // Load data when the view is created
        loadFollowingSellers();

        // Setup search functionality
        setupSearch();

        return view;
    }

    private void loadFollowingSellers() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "Please log in to view following sellers.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        // Fetch the user's list of followed sellers
        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null && user.getFollowing() != null && !user.getFollowing().isEmpty()) {
                            followingIds = user.getFollowing();
                            fetchSellersDetails();
                        } else {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                            // No sellers are being followed
                            Toast.makeText(getContext(), "You are not following any sellers yet.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    Log.e(TAG, "Error fetching following list: " + e.getMessage());
                    Toast.makeText(getContext(), "Error loading data.", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchSellersDetails() {
        if (followingIds == null || followingIds.isEmpty()) {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            return;
        }

        followingSellersList.clear();
        final int[] fetchedCount = {0};

        for (String sellerId : followingIds) {
            db.collection("users").document(sellerId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        User seller = documentSnapshot.toObject(User.class);
                        if (seller != null) {
                            followingSellersList.add(seller);
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        }
                        fetchedCount[0]++;
                        if (fetchedCount[0] == followingIds.size()) {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching seller details: " + e.getMessage());
                        fetchedCount[0]++;
                        if (fetchedCount[0] == followingIds.size()) {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    private void setupSearch() {
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    filter(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }
    }

    private void filter(String text) {
        List<User> filteredList = new ArrayList<>();
        if (followingSellersList != null) {
            for (User seller : followingSellersList) {
                if (seller.getName() != null && seller.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(seller);
                }
            }
        }
        if (adapter != null) {
            adapter.filterList(filteredList);
        }
    }

    @Override
    public void onUnfollowClick(User seller) {
        if (currentUser == null) {
            Toast.makeText(getContext(), "You must be logged in to unfollow.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(currentUser.getUid()).update("following",
                        FieldValue.arrayRemove(seller.getUid()))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Unfollowed " + seller.getName(), Toast.LENGTH_SHORT).show();
                    loadFollowingSellers();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error unfollowing seller: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to unfollow. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
