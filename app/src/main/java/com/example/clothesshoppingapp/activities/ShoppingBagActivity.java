package com.example.clothesshoppingapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.clothesshoppingapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ShoppingBagActivity extends AppCompatActivity {

    private ImageView productImage, backButton;
    private TextView productName, productPrice, deliveryDetails, orderAmount, taxAmount, totalAmount;
    private Spinner sizeSpinner, quantitySpinner;
    private Button proceedToPayment;

    private double taxRate = 0.13;
    private int pricePerUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_bag);

        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        deliveryDetails = findViewById(R.id.deliveryDetails);
        orderAmount = findViewById(R.id.orderAmount);
        taxAmount = findViewById(R.id.taxAmount);
        totalAmount = findViewById(R.id.totalAmount);
        sizeSpinner = findViewById(R.id.sizeSpinner);
        quantitySpinner = findViewById(R.id.quantitySpinner);
        proceedToPayment = findViewById(R.id.proceedToPayment);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> onBackPressed());

        // Retrieve data from intent
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String name = getIntent().getStringExtra("name");
        List<String> sizes = getIntent().getStringArrayListExtra("sizes");
        pricePerUnit = getIntent().getIntExtra("price", 0);

        // Set product details
        Glide.with(this).load(imageUrl).into(productImage);
        productName.setText(name);
        productPrice.setText("₹" + pricePerUnit);

        // Populate size spinner
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sizes);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(sizeAdapter);

        // Populate quantity spinner
        List<Integer> quantities = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            quantities.add(i);
        }
        ArrayAdapter<Integer> quantityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, quantities);
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantitySpinner.setAdapter(quantityAdapter);

        // Handle quantity spinner item selection
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

        // Handle payment button click
        proceedToPayment.setOnClickListener(v -> {
            Toast.makeText(this, "Proceeding to payment...", Toast.LENGTH_SHORT).show();
        });

        updateOrderSummary();
    }

    private void updateOrderSummary() {
        int quantity = (int) quantitySpinner.getSelectedItem();
        double orderTotal = pricePerUnit * quantity;
        double tax = orderTotal * taxRate;
        double total = orderTotal + tax;

        DecimalFormat df = new DecimalFormat("₹#,##0.00");

        orderAmount.setText("Order Amount: " + df.format(orderTotal));
        taxAmount.setText("Tax: " + df.format(tax) + " (" + (int) (taxRate * 100) + "%)");
        totalAmount.setText("Total Amount: " + df.format(total));
    }
}
