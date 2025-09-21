package com.PROJECT.kitchenkart.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.PROJECT.kitchenkart.Models.CartItem;
import com.PROJECT.kitchenkart.R;

import java.util.List;

/**
 * Adapter for displaying cart items in a RecyclerView.
 * This adapter handles the product's image, name, price, and quantity.
 * @noinspection ClassEscapesDefinedScope, ClassEscapesDefinedScope
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItem> cartItems;

    public CartAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_cart.xml layout for each item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        // Load the product image using Glide
        if (cartItem.getProductImageUrl() != null && !cartItem.getProductImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(cartItem.getProductImageUrl())
                    .placeholder(R.drawable.ic_product_placeholder)
                    .into(holder.ivItemImage);
        } else {
            // Fallback to a local resource if no URL is available
            holder.ivItemImage.setImageResource(R.drawable.ic_product_placeholder);
        }

        holder.tvItemName.setText(cartItem.getProductName());
        holder.tvItemPrice.setText(String.format("Rs. %.2f", cartItem.getPrice()));
        holder.tvItemQuantity.setText(String.format("%d", cartItem.getQuantity()));

        // Set up the delete button click listener
        holder.btnDelete.setOnClickListener(v -> {
            // TODO: Implement delete logic (e.g., remove from Firebase, update list)
            Toast.makeText(holder.itemView.getContext(), "Delete " + cartItem.getProductName(), Toast.LENGTH_SHORT).show();
            // This is a dummy action. You would implement real deletion here.
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    /**
     * ViewHolder class to hold the views for each cart item.
     */
    static class CartViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivItemImage;
        final TextView tvItemName;
        final TextView tvItemPrice;
        final TextView tvItemQuantity;
        final ImageView btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.iv_cart_item_image);
            tvItemName = itemView.findViewById(R.id.tv_cart_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_cart_item_price);
            tvItemQuantity = itemView.findViewById(R.id.tv_cart_item_quantity);
            btnDelete = itemView.findViewById(R.id.btn_delete_cart_item);
        }
    }
}
