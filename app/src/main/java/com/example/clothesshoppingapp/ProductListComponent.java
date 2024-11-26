package com.example.clothesshoppingapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothesshoppingapp.adapters.ProductAdapter;
import com.example.clothesshoppingapp.models.Product;

import java.util.List;

public class ProductListComponent extends LinearLayout {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;

    public ProductListComponent(Context context) {
        super(context);
        init(context);
    }

    public ProductListComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProductListComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.component_product_list, this, true);
        recyclerView = findViewById(R.id.productRecyclerView);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
    }

    public void setProductData(Context context, List<Product> productList, ProductAdapter.OnProductClickListener listener) {
        adapter = new ProductAdapter(context, productList, listener);
        recyclerView.setAdapter(adapter);
    }
}

