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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private Context context;
    private final Runnable updateSummaryCallback;
    private FirebaseFirestore db;
    private String userId;

    public CartAdapter(Context context, List<CartItem> cartItems, Runnable updateSummaryCallback, String userId) {
        this.context = context;
        this.cartItems = cartItems;
        this.updateSummaryCallback = updateSummaryCallback;
        this.db = FirebaseFirestore.getInstance();
        this.userId = userId;
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

        Glide.with(context).load(item.getImageUrl()).into(holder.productImage);
        holder.productName.setText(item.getName());
        holder.productPrice.setText("$" + item.getPrice());
        holder.productQuantity.setText(String.valueOf(item.getQuantity()));

        // Handle increment button click
        holder.incrementButton.setOnClickListener(v -> {
            if (item.getQuantity() < 10) {
                item.setQuantity(item.getQuantity() + 1);
                updateItemInFirestore(item);
                notifyItemChanged(position);
                updateSummaryCallback.run();
            } else {
                Toast.makeText(context, "Maximum quantity is 10", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle decrement button click
        holder.decrementButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                updateItemInFirestore(item);
                notifyItemChanged(position);
                updateSummaryCallback.run();
            } else {
                Toast.makeText(context, "Minimum quantity is 1", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            deleteItemFromFirestore(item);
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
            updateSummaryCallback.run();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private void updateItemInFirestore(CartItem item) {
        if (userId == null) {
            Toast.makeText(context, "User ID is null. Cannot update item.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(userId)
                .collection("cart")
                .document(item.getName())
                .update("quantity", item.getQuantity())
                .addOnSuccessListener(aVoid -> {})
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to update quantity.", Toast.LENGTH_SHORT).show());
    }


    private void deleteItemFromFirestore(CartItem item) {
        if (userId == null) {
            Toast.makeText(context, "User ID is null. Cannot delete item.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(userId)
                .collection("cart")
                .document(item.getName())
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Item deleted successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete item.", Toast.LENGTH_SHORT).show());
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
