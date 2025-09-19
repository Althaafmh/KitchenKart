package com.PROJECT.kitchenkart.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
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

import com.PROJECT.kitchenkart.R;
import com.github.mikephil.charting.charts.BarChart; // Correct import
import com.github.mikephil.charting.charts.PieChart; // Correct import
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** @noinspection ALL*/
public class SalesAnalyticsFragment extends Fragment {

    private static final String TAG = "SalesAnalyticsFragment";

    private TextView tvTotalRevenue;
    private TextView tvTotalOrders;
    private TextView tvTopSellingProduct;
    private TextView tvSalesSummary; // For textual summary if no charts
    private ProgressBar progressBar;

    // Charts - Corrected type declarations
    private BarChart barChartDailySales;
    private PieChart pieChartProductSales;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private DecimalFormat currencyFormat = new DecimalFormat("Rs #,##0.00");

    public SalesAnalyticsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId") // Keep this if IDs are generated dynamically, otherwise consider removing
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales_analytics, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Check if user is logged in
        if (currentUser == null) {
            Toast.makeText(getContext(), "Please log in to view sales analytics.", Toast.LENGTH_SHORT).show();
            // Optional: Redirect to LoginActivity
            // startActivity(new Intent(getActivity(), LoginActivity.class));
            // getActivity().finish();
            return view; // Return early
        }

        // Initialize UI components
        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue);
        tvTotalOrders = view.findViewById(R.id.tvTotalOrders);
        tvTopSellingProduct = view.findViewById(R.id.tvTopSellingProduct);
        tvSalesSummary = view.findViewById(R.id.tvSalesSummary); // Text summary
        progressBar = view.findViewById(R.id.progressBar);

        // Initialize Charts - No need for explicit casting if types are correct
        barChartDailySales = view.findViewById(R.id.barChartDailySales);
        pieChartProductSales = view.findViewById(R.id.pieChartProductSales);

        // Load analytics data
        fetchSalesData();

        return view;
    }

    private void fetchSalesData() {
        if (currentUser == null) return;

        progressBar.setVisibility(View.VISIBLE);
        // Clear previous data
        tvTotalRevenue.setText(getString(R.string.loading));
        tvTotalOrders.setText(getString(R.string.loading));
        tvTopSellingProduct.setText(getString(R.string.loading));
        tvSalesSummary.setText(""); // Clear summary

        // Assuming orders are stored in a collection named "orders"
        // And each order document has fields like "sellerId", "totalAmount", "orderDate", "products" (array/map of product details)
        db.collection("orders")
                .whereEqualTo("sellerId", currentUser.getUid()) // Filter orders for the current seller
                // Optional: Add filtering for order status (e.g., "completed")
                // .whereEqualTo("status", "completed")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        double totalRevenue = 0;
                        int totalOrders = task.getResult().size();
                        Map<String, Integer> productSalesCount = new HashMap<>(); // Product Name -> Quantity Sold
                        Map<String, Double> dailySales = new HashMap<>(); // Date String (yyyy-MM-dd) -> Total Sales for that day

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Fetch total amount for revenue
                            Double orderTotal = document.getDouble("totalAmount");
                            if (orderTotal != null) {
                                totalRevenue += orderTotal;
                            }

                            // Fetch order date for daily sales
                            // Assuming "timestamp" field is a com.google.firebase.Timestamp
                            Timestamp timestamp = document.getTimestamp("timestamp");
                            if (timestamp != null) {
                                String dateString = sdf.format(timestamp.toDate());
                                dailySales.put(dateString, dailySales.getOrDefault(dateString, 0.0) + orderTotal);
                            }

                            // Fetch product details for top selling product
                            // This part heavily depends on your order structure.
                            // Assuming "products" is a List of Maps, where each map contains "name" and "quantity"
                            List<Map<String, Object>> productsInOrder = (List<Map<String, Object>>) document.get("products");
                            if (productsInOrder != null) {
                                for (Map<String, Object> productItem : productsInOrder) {
                                    String productName = (String) productItem.get("name");
                                    Long quantityLong = (Long) productItem.get("quantity"); // Firestore reads numbers as Long
                                    int quantity = quantityLong != null ? quantityLong.intValue() : 0;

                                    if (productName != null) {
                                        productSalesCount.put(productName, productSalesCount.getOrDefault(productName, 0) + quantity);
                                    }
                                }
                            }
                        }

                        // Update TextViews
                        tvTotalRevenue.setText(getString(R.string.total_revenue_format, currencyFormat.format(totalRevenue)));
                        tvTotalOrders.setText(getString(R.string.total_orders_format, totalOrders));

                        // Determine Top Selling Product
                        String topProduct = "N/A";
                        int maxSold = 0;
                        if (!productSalesCount.isEmpty()) {
                            for (Map.Entry<String, Integer> entry : productSalesCount.entrySet()) {
                                if (entry.getValue() > maxSold) {
                                    maxSold = entry.getValue();
                                    topProduct = entry.getKey();
                                }
                            }
                        }
                        tvTopSellingProduct.setText(getString(R.string.top_selling_product_format, topProduct, maxSold));

                        // Generate summary for TextView (if charts are not used or as a fallback)
                        StringBuilder summaryBuilder = new StringBuilder();
                        summaryBuilder.append("Daily Sales:\n");
                        dailySales.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey()) // Sort by date
                                .forEach(entry -> summaryBuilder.append(entry.getKey())
                                        .append(": ")
                                        .append(currencyFormat.format(entry.getValue()))
                                        .append("\n"));

                        summaryBuilder.append("\nProduct Sales Breakdown:\n");
                        productSalesCount.entrySet().stream()
                                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder())) // Sort by quantity sold
                                .forEach(entry -> summaryBuilder.append(entry.getKey())
                                        .append(": ")
                                        .append(entry.getValue())
                                        .append(" units\n"));

                        tvSalesSummary.setText(summaryBuilder.toString());

                        // Populate Charts (if enabled)
                        if (!dailySales.isEmpty()) {
                            setupDailySalesBarChart(dailySales);
                        } else {
                            barChartDailySales.clear(); // Correct method
                            barChartDailySales.setNoDataText("No daily sales data available."); // Correct method
                            barChartDailySales.invalidate();
                        }

                        if (!productSalesCount.isEmpty()) {
                            setupProductSalesPieChart(productSalesCount);
                        } else {
                            pieChartProductSales.clear(); // Correct method
                            pieChartProductSales.setNoDataText("No product sales data available."); // Correct method
                            pieChartProductSales.invalidate();
                        }

                    } else {
                        Toast.makeText(getContext(), "Error fetching sales data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error fetching sales data", task.getException());
                        tvSalesSummary.setText("Error loading sales data.");
                    }
                });
    }

    private void setupDailySalesBarChart(Map<String, Double> dailySales) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> dates = new ArrayList<>(dailySales.keySet());
        java.util.Collections.sort(dates); // Sort dates chronologically

        // Map date strings to indices for X-axis
        HashMap<String, Integer> dateToIndexMap = new HashMap<>();
        for (int i = 0; i < dates.size(); i++) {
            dateToIndexMap.put(dates.get(i), i);
        }

        for (String date : dates) {
            entries.add(new BarEntry(dateToIndexMap.get(date), dailySales.get(date).floatValue()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Daily Sales");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f); // Make bars a bit wider

        barChartDailySales.setData(barData);
        barChartDailySales.getDescription().setEnabled(false); // No description text
        barChartDailySales.setFitBars(true); // Make sure the bars fit the viewport

        // X-axis configuration
        XAxis xAxis = barChartDailySales.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates)); // Set date strings as labels
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Only show one label per entry
        xAxis.setLabelCount(dates.size()); // Show all labels
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setLabelRotationAngle(45); // Rotate labels if they overlap

        // Y-axis (left and right) configuration
        barChartDailySales.getAxisLeft().setAxisMinimum(0f); // Start from 0
        barChartDailySales.getAxisRight().setEnabled(false); // Disable right Y-axis

        // Legend configuration
        Legend legend = barChartDailySales.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(9f);
        legend.setTextSize(11f);
        legend.setXEntrySpace(4f);

        barChartDailySales.animateY(1000); // Animation
        barChartDailySales.invalidate(); // Refresh chart
    }

    private void setupProductSalesPieChart(Map<String, Integer> productSalesCount) {
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : productSalesCount.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Product Sales");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS); // Using a different color template
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChartProductSales.setData(pieData);
        pieChartProductSales.getDescription().setEnabled(false);
        pieChartProductSales.setDrawHoleEnabled(true);
        pieChartProductSales.setHoleColor(Color.WHITE);
        pieChartProductSales.setTransparentCircleColor(Color.WHITE);
        pieChartProductSales.setTransparentCircleAlpha(110);
        pieChartProductSales.setHoleRadius(58f);
        pieChartProductSales.setTransparentCircleRadius(61f);
        pieChartProductSales.setDrawCenterText(true);
        pieChartProductSales.setCenterText("Product Sales");
        pieChartProductSales.setCenterTextSize(14f);
        pieChartProductSales.setEntryLabelColor(Color.BLACK);
        pieChartProductSales.setEntryLabelTextSize(10f);

        // Legend configuration
        Legend legend = pieChartProductSales.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);
        legend.setTextSize(10f);

        pieChartProductSales.animateY(1400); // Animation
        pieChartProductSales.invalidate(); // Refresh chart
    }
}