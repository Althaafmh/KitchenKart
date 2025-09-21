package com.PROJECT.kitchenkart.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PROJECT.kitchenkart.Adapters.OrderAdapter;
import com.PROJECT.kitchenkart.Models.Order;
import com.PROJECT.kitchenkart.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** @noinspection CommentedOutCode*/
public class CurrentOrdersFragment extends Fragment implements OrderAdapter.OnOrderActionListener {

    private List<Order> currentOrderList;

    public CurrentOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerViewCurrentOrders = view.findViewById(R.id.recyclerViewCurrentOrders);
        recyclerViewCurrentOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        currentOrderList = new ArrayList<>();
        populateSampleCurrentOrders(); // Populate with sample data
        OrderAdapter orderAdapter = new OrderAdapter(getContext(), currentOrderList, this);
        recyclerViewCurrentOrders.setAdapter(orderAdapter);
    }

    private void populateSampleCurrentOrders() {
        currentOrderList.add(new Order("101", "Kamala Perera", 750.00, "2025-07-23 10:30", "Pending", Arrays.asList("Parippu Curry", "Kottu Roti"), "123 Main St, Kandy"));
        currentOrderList.add(new Order("102", "Nimal Fernando", 400.00, "2025-07-23 11:00", "Accepted", Arrays.asList("Pol Sambol", "Hoppers x3"), "456 Beach Rd, Galle"));
        currentOrderList.add(new Order("103", "Aisha Rizwan", 600.00, "2025-07-23 12:15", "Pending", List.of("Lamprais"), "789 Lake View, Colombo"));
        currentOrderList.add(new Order("104", "Suresh Kumar", 360.00, "2025-07-23 13:45", "Accepted", Arrays.asList("String Hoppers x4", "Parippu Curry"), "101 River Side, Kandy"));
    }

    @Override
    public void onAcceptClick(Order order) {
        Toast.makeText(getContext(), "Order " + order.getOrderId() + " Accepted", Toast.LENGTH_SHORT).show();
        // Implement logic to update order status to 'Accepted' in your data source
        // Example: order.setStatus("Accepted"); // You'd need a setter in Order model for this
        // orderAdapter.notifyDataSetChanged(); // Or notifyItemChanged(position)
    }

    @Override
    public void onRejectClick(Order order) {
        Toast.makeText(getContext(), "Order " + order.getOrderId() + " Rejected", Toast.LENGTH_SHORT).show();
        // Implement logic to update order status to 'Rejected'
    }

    @Override
    public void onMarkDeliveredClick(Order order) {
        Toast.makeText(getContext(), "Order " + order.getOrderId() + " Marked as Delivered", Toast.LENGTH_SHORT).show();
        // Implement logic to update order status to 'Delivered'
        // This order might then move to PastOrdersFragment
    }

    @Override
    public void onDetailsClick(Order order) {
        Toast.makeText(getContext(), "Viewing details for Order " + order.getOrderId(), Toast.LENGTH_SHORT).show();
        // TODO: Start an OrderDetailsActivity here, passing the order ID
    }
}