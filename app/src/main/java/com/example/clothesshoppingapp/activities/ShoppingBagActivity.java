package com.example.clothesshoppingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.clothesshoppingapp.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ShoppingBagActivity extends AppCompatActivity {

    private ImageView productImage, backButton;
    private TextView productName, productDescription, productPrice, deliveryDetails, orderAmount, deliveryAmount, taxAmount, totalAmount;
    private Spinner quantitySpinner;
    private Button proceedToPayment;

    private double taxRate = 0.13;
    private int deliveryFee = 30;
    private int pricePerUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_bag);

        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productDescription = findViewById(R.id.productDescription);
        deliveryAmount = findViewById(R.id.deliveryAmount);
        productPrice = findViewById(R.id.productPrice);
        deliveryDetails = findViewById(R.id.deliveryDetails);
        orderAmount = findViewById(R.id.orderAmount);
        taxAmount = findViewById(R.id.taxAmount);
        totalAmount = findViewById(R.id.totalAmount);
        quantitySpinner = findViewById(R.id.quantitySpinner);
        proceedToPayment = findViewById(R.id.proceedToPayment);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> onBackPressed());

        // Retrieve product data from the Intent
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");
        List<String> sizes = getIntent().getStringArrayListExtra("sizes");
        pricePerUnit = getIntent().getIntExtra("price", 0);

        // Display product details
        Glide.with(this).load(imageUrl).into(productImage);
        productName.setText(name);
        productDescription.setText(description != null && description.length() > 60
                ? description.substring(0, 60) + "..."
                : description);
        deliveryAmount.setText("$" + deliveryFee);
        deliveryDetails.setText("Delivery by: " + getDeliveryDate());
        productPrice.setText("$" + pricePerUnit);

        List<Integer> quantities = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            quantities.add(i);
        }
        ArrayAdapter<Integer> quantityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, quantities);
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantitySpinner.setAdapter(quantityAdapter);

        // Update the order summary whenever the quantity changes
        quantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateOrderSummary();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        proceedToPayment.setOnClickListener(v -> {
            Intent intent = new Intent(ShoppingBagActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });

        updateOrderSummary();
    }

    private void updateOrderSummary() {
        int quantity = (int) quantitySpinner.getSelectedItem();
        double orderTotal = pricePerUnit * quantity;

        int currentDeliveryFee = (orderTotal > 1000) ? 0 : deliveryFee;

        double tax = orderTotal * taxRate;
        double total = orderTotal + tax + currentDeliveryFee;

        DecimalFormat df = new DecimalFormat("$#,##0.00");

        orderAmount.setText(df.format(orderTotal));
        deliveryAmount.setText(currentDeliveryFee == 0 ? "Free" : df.format(currentDeliveryFee));
        taxAmount.setText(df.format(tax) + " (" + (int) (taxRate * 100) + "%)");
        totalAmount.setText(df.format(total));
    }

    private String getDeliveryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}
