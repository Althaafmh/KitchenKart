package com.PROJECT.kitchenkart.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.PROJECT.kitchenkart.R;

public class SalesAnalyticsActivity extends AppCompatActivity {

    TextView totalSalesText;
    TextView totalOrdersText;


    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_analytics);

        // Initialize views
        totalSalesText = findViewById(R.id.totalSalesText);
        totalOrdersText = findViewById(R.id.totalOrdersText);

        // Set dummy data
        totalSalesText.setText("13.3K");
        totalOrdersText.setText("29");


    }
}
