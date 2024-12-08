package com.example.clothesshoppingapp.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.clothesshoppingapp.R;
import com.example.clothesshoppingapp.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productDescription, productPrice, productOriginalPrice, productDiscount, productRating, productReviews, deliveryTime;
    private LinearLayout sizeContainer, buyNowButton, goToCartButton;
    private TextView selectedSize;
    private LinearLayout sizeLayout;

    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productDescription = findViewById(R.id.productDescription);
        productPrice = findViewById(R.id.productPrice);
        productOriginalPrice = findViewById(R.id.productOriginalPrice);
        productDiscount = findViewById(R.id.productDiscount);
        productRating = findViewById(R.id.productRating);
        productReviews = findViewById(R.id.productReviews);
        deliveryTime = findViewById(R.id.deliveryTime);

        sizeContainer = findViewById(R.id.sizeContainer);
        selectedSize = findViewById(R.id.selectedSize);
        sizeLayout = findViewById(R.id.sizeLayout);

        buyNowButton = findViewById(R.id.buyNowButton);
        goToCartButton = findViewById(R.id.goToCartButton);

        ImageView backArrow = findViewById(R.id.backButton);
        backArrow.setOnClickListener(v -> onBackPressed());

        buyNowButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, ShoppingBagActivity.class);

            // Pass all product details to ShoppingBagActivity
            intent.putExtra("imageUrl", getIntent().getStringExtra("imageUrl"));
            intent.putExtra("name", getIntent().getStringExtra("name"));
            intent.putExtra("description", getIntent().getStringExtra("description"));
            intent.putExtra("price", getIntent().getIntExtra("price", 0));
            intent.putExtra("originalPrice", getIntent().getIntExtra("originalPrice", 0));
            intent.putExtra("discount", getIntent().getStringExtra("discount"));
            intent.putExtra("rating", getIntent().getFloatExtra("rating", 0f));
            intent.putExtra("reviews", getIntent().getIntExtra("reviews", 0));
            intent.putStringArrayListExtra("sizes", (ArrayList<String>) getIntent().getStringArrayListExtra("sizes"));

            // Start ShoppingBagActivity
            startActivity(intent);
        });


        // Add the onClickListener for the "Add to Cart" button
        goToCartButton.setOnClickListener(v -> {
            CartItem item = new CartItem(
                    getIntent().getStringExtra("imageUrl"),
                    getIntent().getStringExtra("name"),
                    getIntent().getIntExtra("price", 0),
                    1
            );
            addToCart(item);
        });

        displayProductDetails();

        deliveryTime.setText("Within 1 - 2 days");

        List<String> sizes = getIntent().getStringArrayListExtra("sizes");
        populateSizes(sizes);

    }

    private void populateSizes(List<String> sizes) {
        // Filter out empty or null sizes
        if (sizes == null || sizes.isEmpty() || sizes.stream().allMatch(String::isEmpty)) {
            sizeLayout.setVisibility(View.GONE);
            selectedSize.setVisibility(View.GONE);
            return;
        }

        sizes.removeIf(String::isEmpty);

        sizeLayout.setVisibility(View.VISIBLE);
        selectedSize.setVisibility(View.VISIBLE);

        sizeContainer.removeAllViews();

        selectedSize.setText("Size: " + sizes.get(0));

        for (int i = 0; i < sizes.size(); i++) {
            String size = sizes.get(i);

            TextView sizeButton = new TextView(this);
            sizeButton.setText(size);
            sizeButton.setTextSize(14f);
            sizeButton.setPadding(20, 8, 20, 8);
            sizeButton.setBackground(getDrawable(R.drawable.size_selector));
            sizeButton.setTextColor(getColorStateList(R.color.size_button_text_selector));
            sizeButton.setClickable(true);

            if (i == 0) {
                sizeButton.setSelected(true);
                sizeButton.setBackground(getDrawable(R.drawable.size_selected_background));
            }

            // Set click listener to update selected size
            sizeButton.setOnClickListener(v -> {
                selectedSize.setText("Size: " + size);

                for (int j = 0; j < sizeContainer.getChildCount(); j++) {
                    sizeContainer.getChildAt(j).setSelected(false);
                    sizeContainer.getChildAt(j).setBackground(getDrawable(R.drawable.size_selector));
                }

                sizeButton.setSelected(true);
                sizeButton.setBackground(getDrawable(R.drawable.size_selected_background));
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            sizeButton.setLayoutParams(params);

            sizeContainer.addView(sizeButton);
        }
    }

    private void addToCart(CartItem item) {
        if (userId == null) {
            Toast.makeText(this, "User ID is null. Cannot add to cart.", Toast.LENGTH_SHORT).show();
            return;
        }

        String size = selectedSize.getText().toString().replace("Size: ", ""); // Extract selected size
        if (size.isEmpty()) {
            Toast.makeText(this, "Please select a size.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(userId )
                .collection("cart")
                .document(item.getName())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Item already exists, increment quantity
                        int currentQuantity = documentSnapshot.getLong("quantity").intValue();
                        db.collection("users")
                                .document(userId)
                                .collection("cart")
                                .document(item.getName())
                                .update("quantity", currentQuantity + item.getQuantity())
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Quantity updated in cart!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to update quantity.", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Item does not exist, add it as a new entry
                        Map<String, Object> cartData = new HashMap<>();
                        cartData.put("imageUrl", item.getImageUrl());
                        cartData.put("name", item.getName());
                        cartData.put("price", item.getPrice());
                        cartData.put("quantity", item.getQuantity());
                        cartData.put("size", size);

                        db.collection("users")
                                .document(userId)
                                .collection("cart")
                                .document(item.getName())
                                .set(cartData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Item added to cart!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to add item to cart.", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to check existing cart item.", Toast.LENGTH_SHORT).show();
                });
    }

    private void displayProductDetails() {
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");
        int price = getIntent().getIntExtra("price", 0);
        int originalPrice = getIntent().getIntExtra("originalPrice", 0);

        Glide.with(this).load(imageUrl).into(productImage);
        productName.setText(name);
        productDescription.setText(description);
        productPrice.setText("$" + price);
        productOriginalPrice.setText("$" + originalPrice);
        productOriginalPrice.setPaintFlags(productOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public static class GetStartedActivity extends AppCompatActivity {
        Button startButton;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_get_started);
            startButton = findViewById(R.id.startButton);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(GetStartedActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            });
        }
    }
}
