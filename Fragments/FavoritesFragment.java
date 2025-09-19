// FavoritesFragment.java
package com.PROJECT.kitchenkart.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PROJECT.kitchenkart.Adapters.FoodProductAdapter;
import com.PROJECT.kitchenkart.R;
import com.PROJECT.kitchenkart.Models.Product;

import java.util.ArrayList;
import java.util.List;

/** @noinspection SpellCheckingInspection, FieldCanBeLocal , ConstantValue */
public class FavoritesFragment extends Fragment {

    private RecyclerView rvFavorites;
    private LinearLayout emptyStateLayout;
    private Button btnBrowseFood;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.favorites_toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Favorites");

        rvFavorites = view.findViewById(R.id.rv_favorites);
        emptyStateLayout = view.findViewById(R.id.empty_state_layout);
        btnBrowseFood = view.findViewById(R.id.btn_browse_food);

        // Favorites ලැයිස්තුව load කර පෙන්වන්න
        loadFavorites();

        btnBrowseFood.setOnClickListener(v -> {
            // "Browse Food" බොත්තම click කළ විට HomeFragment එකට යොමු කරන්න
            // (Navigate back to the HomeFragment)
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
            }
        });
    }

    private void loadFavorites() {
        // TODO: මෙහිදී Firebase Firestore හෝ Room database එකෙන් Favorites දත්ත ලබා ගන්න.
        // (TODO: Fetch Favorites data from Firebase Firestore or Room database here.)

        List<Product> favoritesList = new ArrayList<>();
        // favoritesList.add(new Product("Polsambal & String Hoppers", 100.00, R.drawable.ic_product_placeholder));
        // ඔබට favorites තිබේ නම්, ඒවා මෙහිදී එකතු කරන්න

        if (favoritesList.isEmpty()) {
            rvFavorites.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            rvFavorites.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            rvFavorites.setLayoutManager(layoutManager);

            FoodProductAdapter adapter = new FoodProductAdapter(favoritesList);
            rvFavorites.setAdapter(adapter);
        }
    }
}
