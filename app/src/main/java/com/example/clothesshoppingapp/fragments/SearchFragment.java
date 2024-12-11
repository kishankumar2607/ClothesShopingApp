package com.example.clothesshoppingapp.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothesshoppingapp.R;
import com.example.clothesshoppingapp.adapters.ProductAdapter;
import com.example.clothesshoppingapp.models.Product;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView wishListRecyclerView;
    private TextView productCountText, noProductsFoundText;
    private EditText searchView;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> filteredList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        wishListRecyclerView = view.findViewById(R.id.wishListRecyclerView);
        productCountText = view.findViewById(R.id.productCountText);
        noProductsFoundText = view.findViewById(R.id.noProductsFoundText);
        searchView = view.findViewById(R.id.searchView);

        int spanCount = getSpanCount();
        wishListRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        db = FirebaseFirestore.getInstance();
        fetchProductsFromFirestore();

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

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

                    filteredList.clear();
                    filteredList.addAll(productList);

                    updateProductCount();

                    adapter = new ProductAdapter(getContext(), filteredList, product -> {
                        Toast.makeText(getContext(), "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
                    });
                    wishListRecyclerView.setAdapter(adapter);
                }
            } else {
                Toast.makeText(getContext(), "Failed to load products: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(productList);
        } else {
            for (Product product : productList) {
                if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(product);
                }
            }
        }

        if (filteredList.isEmpty()) {
            noProductsFoundText.setVisibility(View.VISIBLE);
            wishListRecyclerView.setVisibility(View.GONE);
        } else {
            noProductsFoundText.setVisibility(View.GONE);
            wishListRecyclerView.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
        updateProductCount();
    }

    private void updateProductCount() {
        productCountText.setText(filteredList.size() + " Products");
    }

    private int getSpanCount() {
        int orientation = getResources().getConfiguration().orientation;
        return (orientation == Configuration.ORIENTATION_LANDSCAPE) ? 4 : 2;
    }
}
