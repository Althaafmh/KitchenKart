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

public class PastOrdersFragment extends Fragment implements OrderAdapter.OnOrderActionListener {

    private List<Order> pastOrderList;

    public PastOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_past_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerViewPastOrders = view.findViewById(R.id.recyclerViewPastOrders);
        recyclerViewPastOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        pastOrderList = new ArrayList<>();
        populateSamplePastOrders(); // Populate with sample data
        OrderAdapter orderAdapter = new OrderAdapter(getContext(), pastOrderList, this);
        recyclerViewPastOrders.setAdapter(orderAdapter);
    }

    private void populateSamplePastOrders() {
        pastOrderList.add(new Order("098", "Dilani Silva", 550.00, "2025-07-22 09:00", "Delivered", Arrays.asList("Watalappan", "Kottu Roti"), "12 Galle Rd, Moratuwa"));
        pastOrderList.add(new Order("097", "Ranil Wickramasinghe", 200.00, "2025-07-22 14:00", "Delivered", List.of("Parippu Curry"), "34 Temple Rd, Colombo"));
        pastOrderList.add(new Order("096", "Priya Sharma", 800.00, "2025-07-21 18:00", "Cancelled", Arrays.asList("Lamprais", "Pol Sambol"), "56 Kandy Rd, Kurunegala"));
        pastOrderList.add(new Order("095", "Sameera Khan", 300.00, "2025-07-21 11:30", "Delivered", Arrays.asList("Hoppers x2", "Tea"), "78 Negombo Rd, Ja-Ela"));
    }

    @Override
    public void onAcceptClick(Order order) {
        // Not expected for past orders, but implement if needed
        Toast.makeText(getContext(), "Order " + order.getOrderId() + " already completed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRejectClick(Order order) {
        // Not expected for past orders
        Toast.makeText(getContext(), "Order " + order.getOrderId() + " already completed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkDeliveredClick(Order order) {
        // Not expected for past orders
        Toast.makeText(getContext(), "Order " + order.getOrderId() + " already delivered.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetailsClick(Order order) {
        Toast.makeText(getContext(), "Viewing details for Past Order " + order.getOrderId(), Toast.LENGTH_SHORT).show();
        // TODO: Start an OrderDetailsActivity here, passing the order ID
    }
}