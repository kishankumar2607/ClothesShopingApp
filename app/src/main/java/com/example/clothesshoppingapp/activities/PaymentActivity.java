package com.example.clothesshoppingapp.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.clothesshoppingapp.R;

import java.util.Calendar;

public class PaymentActivity extends AppCompatActivity {

    private EditText cardHolderName, cardNumber, expiryDate, cvv;
    private TextView errorCheckBoxMsg;
    private CheckBox termsCheckbox;
    private Button proceedToOrderButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        db = FirebaseFirestore.getInstance();

        cardHolderName = findViewById(R.id.cardHolderName);
        cardNumber = findViewById(R.id.cardNumber);
        expiryDate = findViewById(R.id.expiryDate);
        cvv = findViewById(R.id.cvv);
        termsCheckbox = findViewById(R.id.termsCheckbox); // Now initialized correctly
        proceedToOrderButton = findViewById(R.id.placeOrderButton);
        errorCheckBoxMsg = findViewById(R.id.checkBoxError);
        ImageView backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> onBackPressed());

        proceedToOrderButton.setOnClickListener(v -> {

            if (validateInputs()) {
                if (!termsCheckbox.isChecked()) {
                    errorCheckBoxMsg.setVisibility(View.VISIBLE);
                } else {
                    showSuccessModal();
                }
            }
        });
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(cardHolderName.getText().toString().trim())) {
            cardHolderName.setError("Cardholder name is required");
            cardHolderName.requestFocus();
            return false;
        }

        String cardNum = cardNumber.getText().toString().trim();
        if (TextUtils.isEmpty(cardNum)) {
            cardNumber.setError("Card number is required");
            cardNumber.requestFocus();
            return false;
        }
        if (cardNum.length() != 16) {
            cardNumber.setError("Card number must be 16 digits");
            cardNumber.requestFocus();
            return false;
        }

        String expiry = expiryDate.getText().toString().trim();
        if (TextUtils.isEmpty(expiry)) {
            expiryDate.setError("Expiry date is required");
            expiryDate.requestFocus();
            return false;
        }

        if (!expiry.matches("(0[1-9]|1[0-2])/(\\d{2})")) {
            expiryDate.setError("Invalid expiry date format (MM/YY)");
            expiryDate.requestFocus();
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR) % 100;
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        String[] expiryParts = expiry.split("/");
        int expiryMonth = Integer.parseInt(expiryParts[0]);
        int expiryYear = Integer.parseInt(expiryParts[1]);

        if (expiryYear < currentYear || (expiryYear == currentYear && expiryMonth < currentMonth)) {
            expiryDate.setError("Expiry date must be current or in the future");
            expiryDate.requestFocus();
            return false;
        }

        String cvvText = cvv.getText().toString().trim();
        if (TextUtils.isEmpty(cvvText)) {
            cvv.setError("CVV is required");
            cvv.requestFocus();
            return false;
        }
        if (cvvText.length() != 3) {
            cvv.setError("CVV must be 3 digits");
            cvv.requestFocus();
            return false;
        }

        return true;
    }


    private void showSuccessModal() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window .FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_success_payment);
        dialog.setCancelable(false);

        TextView successMessage = dialog.findViewById(R.id.successMessage);
        successMessage.setText("Payment done successfully.");

        dialog.show();

        new Handler().postDelayed(() -> {
            dialog.dismiss();
            deleteCartData();
            Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
            intent.putExtra("redirectTo", "HomeFragment");
            startActivity(intent);
            finish();
        }, 3000);
    }

    private void deleteCartData() {
        db.collection("users")
                .document("userId") // Replace with the logged-in user's unique ID
                .collection("cart")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (com.google.firebase.firestore.DocumentSnapshot document : querySnapshot.getDocuments()) {
                        document.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Successfully deleted a document
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure for deleting a single document
                                    e.printStackTrace();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure for retrieving cart data
                    e.printStackTrace();
                });
    }

}
