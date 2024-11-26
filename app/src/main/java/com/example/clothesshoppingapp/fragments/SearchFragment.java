package com.example.clothesshoppingapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clothesshoppingapp.models.Product;
import com.example.clothesshoppingapp.adapters.ProductAdapter;
import com.example.clothesshoppingapp.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView wishListRecyclerView;
    private TextView productCountText;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        wishListRecyclerView = view.findViewById(R.id.wishListRecyclerView);
        productCountText = view.findViewById(R.id.productCountText);

        wishListRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        db = FirebaseFirestore.getInstance();
        fetchProductsFromFirestore();

        return view;
    }

    private void fetchProductsFromFirestore() {
        CollectionReference productsRef = db.collection("products");
        productsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                if (snapshot != null) {
                    productList.clear();
                    productList.addAll(snapshot.toObjects(Product.class));

                    // Update product count
                    int productCount = productList.size();
                    productCountText.setText(productCount + "+ Products");

                    // Set adapter
                    adapter = new ProductAdapter(getContext(), productList, product -> {
                        // Handle product click
                        Toast.makeText(getContext(), "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
                    });
                    wishListRecyclerView.setAdapter(adapter);
                }
            } else {
                Toast.makeText(getContext(), "Failed to load products: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}