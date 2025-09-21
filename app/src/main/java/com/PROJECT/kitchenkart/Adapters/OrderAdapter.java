package com.PROJECT.kitchenkart.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.PROJECT.kitchenkart.Models.Order;
import com.PROJECT.kitchenkart.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/** @noinspection ALL*/
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onAcceptClick(Order order);
        void onRejectClick(Order order);
        void onMarkDeliveredClick(Order order);
        void onDetailsClick(Order order);
    }

    public OrderAdapter(Context context, List<Order> orderList, OnOrderActionListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.textViewOrderId.setText(String.format("Order #%s", order.getOrderId()));
        holder.textViewCustomerName.setText(String.format("Customer: %s", order.getCustomerName()));
        holder.textViewOrderDate.setText(String.format("Date: %s", order.getOrderDate()));
        holder.textViewOrderItems.setText(String.format("Items: %s", String.join(", ", order.getItemNames())));
        holder.textViewTotalAmount.setText(String.format("Total: LKR %.2f", order.getTotalAmount()));
        holder.textViewOrderStatus.setText(order.getStatus());

        // Set status text color and background based on status
        int statusColor;
        int backgroundColor;
        switch (order.getStatus()) {
            case "Pending":
                statusColor = ContextCompat.getColor(context, R.color.color_status_pending);
                backgroundColor = ContextCompat.getColor(context, R.color.color_status_bg_pending);
                break;
            case "Accepted":
                statusColor = ContextCompat.getColor(context, R.color.color_status_accepted);
                backgroundColor = ContextCompat.getColor(context, R.color.color_status_bg_accepted);
                break;
            case "Delivered":
                statusColor = ContextCompat.getColor(context, R.color.color_status_delivered);
                backgroundColor = ContextCompat.getColor(context, R.color.color_status_bg_delivered);
                break;
            case "Cancelled":
                statusColor = ContextCompat.getColor(context, R.color.color_status_cancelled);
                backgroundColor = ContextCompat.getColor(context, R.color.color_status_bg_cancelled);
                break;
            default:
                statusColor = ContextCompat.getColor(context, R.color.colorTextSecondary);
                backgroundColor = ContextCompat.getColor(context, R.color.white); // Default background
                break;
        }
        holder.textViewOrderStatus.setTextColor(statusColor);
        GradientDrawable drawable = (GradientDrawable) holder.textViewOrderStatus.getBackground();
        if (drawable != null) {
            drawable.setColor(backgroundColor);
        }

        // Handle button visibility based on order status
        holder.buttonAccept.setVisibility(View.GONE);
        holder.buttonReject.setVisibility(View.GONE);
        holder.buttonMarkDelivered.setVisibility(View.GONE);
        holder.buttonDetails.setVisibility(View.VISIBLE); // Always show details button

        if (listener != null) {
            holder.buttonDetails.setOnClickListener(v -> listener.onDetailsClick(order));

            if (order.getStatus().equals("Pending")) {
                holder.buttonAccept.setVisibility(View.VISIBLE);
                holder.buttonReject.setVisibility(View.VISIBLE);
                holder.buttonAccept.setOnClickListener(v -> listener.onAcceptClick(order));
                holder.buttonReject.setOnClickListener(v -> listener.onRejectClick(order));
            } else if (order.getStatus().equals("Accepted")) {
                holder.buttonMarkDelivered.setVisibility(View.VISIBLE);
                holder.buttonMarkDelivered.setOnClickListener(v -> listener.onMarkDeliveredClick(order));
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOrderId, textViewCustomerName, textViewOrderDate,
                textViewOrderItems, textViewTotalAmount, textViewOrderStatus;
        MaterialButton buttonAccept, buttonReject, buttonMarkDelivered, buttonDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrderId = itemView.findViewById(R.id.textViewOrderId);
            textViewCustomerName = itemView.findViewById(R.id.textViewCustomerName);
            textViewOrderDate = itemView.findViewById(R.id.textViewOrderDate);
            textViewOrderItems = itemView.findViewById(R.id.textViewOrderItems);
            textViewTotalAmount = itemView.findViewById(R.id.textViewTotalAmount);
            textViewOrderStatus = itemView.findViewById(R.id.textViewOrderStatus);
            buttonAccept = itemView.findViewById(R.id.buttonAccept);
            buttonReject = itemView.findViewById(R.id.buttonReject);
            buttonMarkDelivered = itemView.findViewById(R.id.buttonMarkDelivered);
            buttonDetails = itemView.findViewById(R.id.buttonDetails);
        }
    }
}