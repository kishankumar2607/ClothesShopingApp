package com.example.clothesshoppingapp.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothesshoppingapp.R;
import com.example.clothesshoppingapp.adapters.ProductAdapter;
import com.example.clothesshoppingapp.models.Product;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class WishListFragment extends Fragment {

    private RecyclerView wishListRecyclerView;
    private TextView productCountText;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wish_list, container, false);

        wishListRecyclerView = view.findViewById(R.id.wishListRecyclerView);
        productCountText = view.findViewById(R.id.productCountText);

        int spanCount = getSpanCount();
        wishListRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        db = FirebaseFirestore.getInstance();
        fetchProductsFromFirestore();

        return view;
    }

    private void fetchProductsFromFirestore() {
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productList.clear();
                productList.addAll(task.getResult().toObjects(Product.class));

                if (adapter == null) {
                    adapter = new ProductAdapter(getContext(), productList, product -> {
                        Toast.makeText(getContext(), "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
                    });
                    wishListRecyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }

                productCountText.setText(productList.size() + " Products");
            } else {
                Toast.makeText(getContext(), "Failed to fetch products.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getSpanCount() {
        int orientation = getResources().getConfiguration().orientation;
        return (orientation == Configuration.ORIENTATION_LANDSCAPE) ? 4 : 2;
    }
}
