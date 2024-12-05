package com.example.clothesshoppingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clothesshoppingapp.R;
import com.example.clothesshoppingapp.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private Runnable updateFooterCallback;

    public CartAdapter(Context context, List<CartItem> cartItems, Runnable updateFooterCallback) {
        this.cartItems = cartItems;
        this.updateFooterCallback = updateFooterCallback;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        Glide.with(holder.itemView.getContext()).load(item.getImageUrl()).into(holder.productImage);
        holder.productName.setText(item.getName());
        holder.productPrice.setText("$" + item.getPrice());
        holder.productQuantity.setText(String.valueOf(item.getQuantity()));

        holder.incrementButton.setOnClickListener(v -> {
            if (item.getQuantity() < 10) {
                item.setQuantity(item.getQuantity() + 1);
                notifyItemChanged(position);
                updateFooterCallback.run();
            } else {
                Toast.makeText(holder.itemView.getContext(), "Maximum quantity is 10", Toast.LENGTH_SHORT).show();
            }
        });

        holder.decrementButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
                updateFooterCallback.run();
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
            updateFooterCallback.run();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage, incrementButton, decrementButton, deleteButton;
        TextView productName, productPrice, productQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
