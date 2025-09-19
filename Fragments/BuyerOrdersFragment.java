package com.PROJECT.kitchenkart.Fragments;

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

import com.PROJECT.kitchenkart.Adapters.OrderAdapter;
import com.PROJECT.kitchenkart.Models.Order;
import com.PROJECT.kitchenkart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment to display a buyer's order history.
 * Fetches orders from Firestore and updates the UI accordingly.
 * @noinspection ALL
 */
public class BuyerOrdersFragment extends Fragment implements OrderAdapter.OnOrderActionListener {

    private RecyclerView recyclerViewBuyerOrders;
    private TextView tvEmptyOrdersMessage;
    private ProgressBar progressBar;

    private OrderAdapter orderAdapter;
    private List<Order> buyerOrderList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private CollectionReference ordersRef;

    public BuyerOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_orders, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "Please log in to view your orders.", Toast.LENGTH_SHORT).show();
            // Handle redirection to Login if needed
            return view;
        }

        ordersRef = db.collection("orders");

        recyclerViewBuyerOrders = view.findViewById(R.id.recyclerViewBuyerOrders);
        tvEmptyOrdersMessage = view.findViewById(R.id.tv_empty_orders_message);
        progressBar = view.findViewById(R.id.progressBar); // Assuming a ProgressBar exists in the layout

        recyclerViewBuyerOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        buyerOrderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getContext(), buyerOrderList, this);
        recyclerViewBuyerOrders.setAdapter(orderAdapter);

        loadBuyerOrders();

        return view;
    }

    private void loadBuyerOrders() {
        if (currentUser == null) return;

        progressBar.setVisibility(View.VISIBLE);
        tvEmptyOrdersMessage.setVisibility(View.GONE);
        buyerOrderList.clear();

        ordersRef.whereEqualTo("buyerId", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Order order = document.toObject(Order.class);
                            order.setOrderId(document.getId());
                            buyerOrderList.add(order);
                        }
                        orderAdapter.notifyDataSetChanged();

                        if (buyerOrderList.isEmpty()) {
                            tvEmptyOrdersMessage.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getContext(), "Error loading orders: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        tvEmptyOrdersMessage.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onAcceptClick(Order order) {
        Toast.makeText(getContext(), "Buyer cannot accept/reject orders.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRejectClick(Order order) {
        Toast.makeText(getContext(), "Buyer cannot accept/reject orders.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkDeliveredClick(Order order) {
        // Here you would implement logic for the buyer to confirm an order as delivered
        // e.g., showing a dialog or making a Firestore update.
        Toast.makeText(getContext(), "You have confirmed Order " + order.getOrderId() + " as delivered.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetailsClick(Order order) {
        Toast.makeText(getContext(), "Buyer viewing details for Order " + order.getOrderId(), Toast.LENGTH_SHORT).show();
        // TODO: Start OrderDetailsActivity for buyer's view
    }
}
