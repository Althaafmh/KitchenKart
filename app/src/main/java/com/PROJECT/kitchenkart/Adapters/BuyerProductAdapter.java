package com.PROJECT.kitchenkart.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.PROJECT.kitchenkart.Models.Product;
import com.PROJECT.kitchenkart.R;

import java.util.List;

/**
 * Adapter for displaying products in the buyer's home screen.
 * @noinspection ALL
 */
public class BuyerProductAdapter extends RecyclerView.Adapter<BuyerProductAdapter.ProductViewHolder> {

    private final List<Product> products;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public BuyerProductAdapter(List<Product> products, OnItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onItemClick(product));
        }
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_product_placeholder)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(product.getImageResId());
        }

        holder.tvTitle.setText(product.getName());
        holder.tvPrice.setText(String.format("Rs.%.2f", product.getPrice()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivImage;
        final TextView tvTitle, tvPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_product_image);
            tvTitle = itemView.findViewById(R.id.tv_product_title);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
        }
    }
}
