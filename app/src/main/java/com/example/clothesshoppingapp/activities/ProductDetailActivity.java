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
import com.example.clothesshoppingapp.fragments.CartFragment;
import com.example.clothesshoppingapp.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productDescription, productPrice, productOriginalPrice, productDiscount, productRating, productReviews, deliveryTime;
    private LinearLayout sizeContainer, buyNowButton, goToCartButton;
    private TextView selectedSize;
    private LinearLayout sizeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

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

        goToCartButton.setOnClickListener(v -> {
            CartFragment.addToCart(new CartItem(
                    getIntent().getStringExtra("imageUrl"),
                    getIntent().getStringExtra("name"),
                    getIntent().getIntExtra("price", 0),
                    1
            ));
            Toast.makeText(this, "Item added to cart", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
            intent.putExtra("openFragment", "CartFragment");
            startActivity(intent);
        });

        displayProductDetails();

        // Retrieve data from intent
        try {
            String imageUrl = getIntent().getStringExtra("imageUrl");
            String name = getIntent().getStringExtra("name");
            String description = getIntent().getStringExtra("description");
            int price = getIntent().getIntExtra("price", 0);
            int originalPrice = getIntent().getIntExtra("originalPrice", 0);
            String discount = getIntent().getStringExtra("discount");
            float rating = getIntent().getFloatExtra("rating", 0f);
            int reviews = getIntent().getIntExtra("reviews", 0);

            Glide.with(this).load(imageUrl).into(productImage);
            productName.setText(name != null ? name : "No Name Available");
            productDescription.setText(description != null ? description : "No Description Available");
            productPrice.setText("$" + price);
            productOriginalPrice.setText("$" + originalPrice);
            productDiscount.setText(discount != null ? discount : "No Discount");
            productRating.setText(String.valueOf(rating));
            productReviews.setText("(" + reviews + " reviews)");

            // Strike through original price
            productOriginalPrice.setPaintFlags(productOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            deliveryTime.setText("Within 1 - 2 days");

        } catch (Exception e) {
            Toast.makeText(this, "Failed to load product details", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

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
