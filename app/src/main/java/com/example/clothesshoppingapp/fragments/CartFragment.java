package com.example.clothesshoppingapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clothesshoppingapp.R;
import com.example.clothesshoppingapp.activities.CheckoutActivity;
import com.example.clothesshoppingapp.adapters.CartAdapter;
import com.example.clothesshoppingapp.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private static final List<CartItem> cartItems = new ArrayList<>();
    private CartAdapter cartAdapter;
    private FirebaseFirestore db;
    private String userId;

    private RecyclerView recyclerView;
    private TextView totalItemsTextView, subTotalTextView, taxTextView, deliveryTextView, totalTextView, emptyCartMessage;

    private static final double TAX_RATE = 0.13; // 13% tax
    private static final double DELIVERY_CHARGE = 30.00;
    private static final double FREE_DELIVERY_THRESHOLD = 1000.00;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
        }else {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
        }

        recyclerView = view.findViewById(R.id.cartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        totalItemsTextView = view.findViewById(R.id.totalItemsTextView);
        subTotalTextView = view.findViewById(R.id.subtotalTextView);
        taxTextView = view.findViewById(R.id.taxTextView);
        deliveryTextView = view.findViewById(R.id.deliveryFeeTextView);
        totalTextView = view.findViewById(R.id.totalTextView);
        emptyCartMessage = view.findViewById(R.id.emptyCartMessage);

        Button checkoutButton = view.findViewById(R.id.checkoutButton);

        checkoutButton.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getContext(), CheckoutActivity.class);
                startActivity(intent);
            }
        });

        cartAdapter = new CartAdapter(getContext(), cartItems, this::updateSummary, userId);
        recyclerView.setAdapter(cartAdapter);

        fetchCartItems();

        return view;
    }

    private void fetchCartItems() {
        db.collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    cartItems.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String imageUrl = document.getString("imageUrl");
                        String name = document.getString("name");
                        int price = document.getLong("price").intValue();
                        int quantity = document.getLong("quantity").intValue();

                        cartItems.add(new CartItem(imageUrl, name, price, quantity));
                    }
                    cartAdapter.notifyDataSetChanged();
                    updateSummary();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch cart items.", Toast.LENGTH_SHORT).show();
                });
    }


    private void updateSummary() {
        int totalItems = 0;
        double subTotal = 0.0;

        for (CartItem item : cartItems) {
            totalItems += item.getQuantity();
            subTotal += item.getQuantity() * item.getPrice();
        }

        double tax = subTotal * TAX_RATE;
        double deliveryCharge = subTotal > FREE_DELIVERY_THRESHOLD ? 0.0 : DELIVERY_CHARGE;
        double total = subTotal + tax + deliveryCharge;

        DecimalFormat df = new DecimalFormat("$#,##0.00");

        if (cartItems.isEmpty()) {
            emptyCartMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            totalItemsTextView.setText("0");
            subTotalTextView.setText("$0.00");
            taxTextView.setText("$0.00");
            deliveryTextView.setText("$0.00");
            totalTextView.setText("$0.00");
        } else {
            emptyCartMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            totalItemsTextView.setText(String.valueOf(totalItems));
            subTotalTextView.setText(df.format(subTotal));
            taxTextView.setText(df.format(tax));
            deliveryTextView.setText(deliveryCharge == 0.0 ? "Free" : df.format(deliveryCharge));
            totalTextView.setText(df.format(total));
        }
    }
}
