package com.example.clothesshoppingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clothesshoppingapp.models.Product;
import com.example.clothesshoppingapp.R;
import com.example.clothesshoppingapp.activities.ProductDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private OnProductClickListener listener;

    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        String name = product.getName();
        String description = product.getDescription();

        if (name.length() > 20) {
            holder.productName.setText(name.substring(0, 20) + "...");
        } else {
            holder.productName.setText(name);
        }
        if (description.length() > 25) {
            holder.productDescription.setText(description.substring(0, 25) + "...");
        } else {
            holder.productDescription.setText(description);
        }
        holder.productPrice.setText("$" + product.getPrice());
        holder.productOriginalPrice.setText("$" + product.getOriginalPrice());
        holder.productDiscount.setText(product.getDiscount());
        holder.productRating.setText(String.valueOf(product.getRating()));
        holder.productReviews.setText("(" + product.getReviews() + " reviews)");

        holder.productOriginalPrice.setPaintFlags(holder.productOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        Glide.with(context)
                .load(product.getImageUrl())
                .into(holder.productImage);

        Log.d("ProductAdapter", "Image URL: " + product.getImageUrl());

        holder.itemView.setOnClickListener(v -> {
            if (context != null) {
                Intent intent                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                = new Intent(context, ProductDetailActivity.class);

                intent.putExtra("imageUrl", product.getImageUrl());
                intent.putExtra("name", product.getName());
                intent.putExtra("description", product.getDescription());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("originalPrice", product.getOriginalPrice());
                intent.putExtra("discount", product.getDiscount());
                intent.putExtra("rating", product.getRating());
                intent.putExtra("reviews", product.getReviews());
                intent.putExtra("sizes", new ArrayList<>(product.getSize()));

                context.startActivity(intent);
            } else {
                Log.e("ProductAdapter", "Context is null. Cannot start activity.");
            }
        });


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productDescription, productPrice, productOriginalPrice, productDiscount, productRating, productReviews;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            productPrice = itemView.findViewById(R.id.productPrice);
            productOriginalPrice = itemView.findViewById(R.id.productOriginalPrice);
            productDiscount = itemView.findViewById(R.id.productDiscount);
            productRating = itemView.findViewById(R.id.productRating);
            productReviews = itemView.findViewById(R.id.productReviews);
        }
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

}
