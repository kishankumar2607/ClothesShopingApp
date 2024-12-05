package com.example.clothesshoppingapp.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothesshoppingapp.models.Category;
import com.example.clothesshoppingapp.adapters.CategoryAdapter;
import com.example.clothesshoppingapp.models.Product;
import com.example.clothesshoppingapp.adapters.ProductAdapter;
import com.example.clothesshoppingapp.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView categoriesRecyclerView, horizontalRecyclerView, gridRecyclerView;
    private ProductAdapter horizontalAdapter, gridAdapter;
    private TextView dealTimer, dealDate;
    private List<Product> horizontalProductList = new ArrayList<>();
    private List<Product> gridProductList = new ArrayList<>();

    private FirebaseFirestore firestore;
    private CollectionReference productsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView);
        dealTimer = view.findViewById(R.id.dealTimer);
        dealDate = view.findViewById(R.id.dealDate);
        AppCompatButton shopNowButton = view.findViewById(R.id.shopNowButton);
        AppCompatButton viewAllButton = view.findViewById(R.id.viewAllButton);
        AppCompatButton viewAllTrendingButton = view.findViewById(R.id.viewAllTrendingButton);
        AppCompatButton visitButton = view.findViewById(R.id.visitButton);
        AppCompatButton viewAllNewArrivalsButton = view.findViewById(R.id.viewAllNewArrivalsButton);

        horizontalRecyclerView = view.findViewById(R.id.horizontalRecyclerView);
        gridRecyclerView = view.findViewById(R.id.gridRecyclerView);

        firestore = FirebaseFirestore.getInstance();
        productsRef = firestore.collection("products");

        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        horizontalAdapter = new ProductAdapter(getContext(), horizontalProductList, product -> {
            Toast.makeText(getContext(), "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
        });
        horizontalRecyclerView.setAdapter(horizontalAdapter);

        gridRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        gridAdapter = new ProductAdapter(getContext(), gridProductList, product -> {
            Toast.makeText(getContext(), "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
        });
        gridRecyclerView.setAdapter(gridAdapter);

        fetchProductsFromFirestore();


        List<Category> categories = Arrays.asList(
                new Category("Beauty", R.drawable.beauty_image),
                new Category("Fashion", R.drawable.fashion_image),
                new Category("Kids", R.drawable.kids_image),
                new Category("Mens", R.drawable.mens_image),
                new Category("Womens", R.drawable.womens_image),
                new Category("Gifts", R.drawable.gifts_image)
        );

        CategoryAdapter adapter = new CategoryAdapter(categories);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoriesRecyclerView.setAdapter(adapter);

        long remainingTime = 24 * 60 * 60 * 1000;
        startCountdownTimer(remainingTime);

        setLastDate("30/11/2024");

        shopNowButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Shop Now clicked!", Toast.LENGTH_SHORT).show();
        });

        viewAllButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View all deals clicked!", Toast.LENGTH_SHORT).show();
        });

        viewAllTrendingButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View all Trending deals clicked!", Toast.LENGTH_SHORT).show();
        });

        visitButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Visit now clicked!", Toast.LENGTH_SHORT).show();
        });

        viewAllNewArrivalsButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View all clicked!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void fetchProductsFromFirestore() {
        productsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                horizontalProductList.clear();
                gridProductList.clear();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    try {
                        Product product = document.toObject(Product.class);

//                        if (product != null) {
//                            if (product.getSize() == null) {
//                                System.out.println("Size is null for product: " + product.getName());
//                            }

                            horizontalProductList.add(product);
                            gridProductList.add(product);
//                        }
                    } catch (Exception e) {
                        System.out.println("Error parsing product: " + e.getMessage());
                    }
                }

                horizontalAdapter.notifyDataSetChanged();
                gridAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error fetching data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirestoreError", task.getException().getMessage());
            }
        });
    }

    private void startCountdownTimer(long remainingTime) {
        if (remainingTime > 0) {
            new CountDownTimer(remainingTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long hours = millisUntilFinished / (60 * 60 * 1000);
                    long minutes = (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000);
                    long seconds = (millisUntilFinished % (60 * 1000)) / 1000;

                    String timeRemaining = String.format("%02dh %02dm %02ds remaining", hours, minutes, seconds);
                    dealTimer.setText(timeRemaining);
                }

                @Override
                public void onFinish() {
                    dealTimer.setText("Deal expired");
                }
            }.start();
        } else {
            dealTimer.setText("Deal expired");
        }
    }

    private void setLastDate(String date) {
        String formattedDate = "Last Date " + date;
        dealDate.setText(formattedDate);
    }

}