package com.PROJECT.kitchenkart.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.PROJECT.kitchenkart.Models.Category;
import com.PROJECT.kitchenkart.R;

import java.util.List;

/**
 * Adapter for displaying a list of food categories in a RecyclerView.
 * This adapter is used in the HomeFragment.
 * @noinspection ALL
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categories;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    public CategoryAdapter(List<Category> categories, OnItemClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    // A simplified constructor without a listener for cases where it's not needed.
    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
        this.listener = null;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        // Set up click listener if one is provided
        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onItemClick(category));
        }

        holder.ivImage.setImageResource(category.getImageResId());
        holder.tvTitle.setText(category.getTitle());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     * ViewHolder class to hold the views for each category item.
     */
    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivImage;
        final TextView tvTitle;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_category_image);
            tvTitle = itemView.findViewById(R.id.tv_category_title);
        }
    }
}
