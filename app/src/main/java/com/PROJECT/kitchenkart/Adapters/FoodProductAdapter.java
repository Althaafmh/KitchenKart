package com.PROJECT.kitchenkart.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.PROJECT.kitchenkart.Fragments.ManageProductsFragment;
import com.PROJECT.kitchenkart.Models.Product;
import com.bumptech.glide.Glide; // Glide පුස්තකාලය import කරන්න.
import com.PROJECT.kitchenkart.Models.FoodProduct;
import com.PROJECT.kitchenkart.R;
import com.google.android.material.button.MaterialButton;

import java.util.Collections;
import java.util.List;

/**
 * මෙම ඇඩැප්ටරය මගින් FoodProduct objects RecyclerView එකක පෙන්වයි.
 * දත්ත Firestore වෙතින් ලැබෙන බැවින්, පින්තූරය පෙන්වීමට URL එක භාවිත කෙරේ.
 * @noinspection ALL
 */
public class FoodProductAdapter extends RecyclerView.Adapter<FoodProductAdapter.ProductViewHolder> {

    private Context context = null;
    private List<FoodProduct> productList = Collections.emptyList();
    private OnProductActionListener listener = null;

    public FoodProductAdapter(List<FoodProduct> productList, ManageProductsFragment manageProductsFragment) {

    }

    public interface OnProductActionListener {
        void onEditClick(FoodProduct product);
        void onDeleteClick(FoodProduct product);
    }

    public FoodProductAdapter(List<Product> favoritesList) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        FoodProduct product = productList.get(position);

        // --- පින්තූරය පෙන්වීම සඳහා නිවැරදි කිරීම ---
        // Firestore හි ගබඩා කර ඇති URL එක භාවිතා කර Glide මගින් පින්තූරය load කරයි.
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_image_placeholder) // පින්තූරය load වන තුරු පෙන්වන placeholder එක
                    .error(R.drawable.ic_image_placeholder) // දෝෂයක් ඇති වුවහොත් පෙන්වන රූපය
                    .into(holder.ivImage);
        } else {
            // URL එකක් නොමැති නම් default placeholder එක පෙන්වයි
            holder.ivImage.setImageResource(R.drawable.ic_image_placeholder);
        }

        holder.tvTitle.setText(product.getName());
        holder.tvPrice.setText("Rs." + String.format("%.2f", product.getPrice()));

        holder.buttonEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(product);
            }
        });

        holder.buttonDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvPrice;
        MaterialButton buttonEdit, buttonDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_product_image);
            tvTitle = itemView.findViewById(R.id.tv_product_title);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
