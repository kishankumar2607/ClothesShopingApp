package com.example.clothesshoppingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clothesshoppingapp.R;

import java.util.regex.Pattern;

public class CheckoutActivity extends AppCompatActivity {

    private EditText fullName, phoneNumber, emailAddress, pinCode, address, city;
    private Button saveButton;

    // Regex for Email and Phone Number Validation
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final String PHONE_REGEX = "^[0-9]{10}$";
    private  static final String PINCODE_REGEX = "^[A-Za-z0-9]{6}$";
    private static final String NAME_REGEX = "^[A-Za-z ]+$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        fullName = findViewById(R.id.fullName);
        phoneNumber = findViewById(R.id.phoneNumber);
        emailAddress = findViewById(R.id.emailAddress);
        pinCode = findViewById(R.id.pinCode);
        address = findViewById(R.id.address);
        city = findViewById(R.id.city);
        saveButton = findViewById(R.id.saveButton);

        ImageView backArrow = findViewById(R.id.backButton);
        backArrow.setOnClickListener(v -> onBackPressed());

        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                Intent intent = new Intent(CheckoutActivity.this, PaymentActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateInputs() {
        String name = fullName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            fullName.setError("Full Name is required");
            fullName.requestFocus();
            return false;
        }
        if (name.length() < 3 || !name.matches(NAME_REGEX)) {
            fullName.setError("Full Name must be at least 3 characters and only contain letters");
            fullName.requestFocus();
            return false;
        }

        String phone = phoneNumber.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            phoneNumber.setError("Phone Number is required");
            phoneNumber.requestFocus();
            return false;
        }
        if (!Pattern.matches(PHONE_REGEX, phone)) {
            phoneNumber.setError("Invalid Phone Number. Must be exactly 10 digits.");
            phoneNumber.requestFocus();
            return false;
        }

        String email = emailAddress.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailAddress.setError("Email Address is required");
            emailAddress.requestFocus();
            return false;
        }
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            emailAddress.setError("Invalid Email Format");
            emailAddress.requestFocus();
            return false;
        }

        String pin = pinCode.getText().toString().trim();
        if (TextUtils.isEmpty(pin)) {
            pinCode.setError("Pin Code / Area Code is required");
            pinCode.requestFocus();
            return false;
        }
        if (!Pattern.matches(PINCODE_REGEX, pin)) {
            pinCode.setError("Invalid Pin Code. Must be exactly 6 alphanumeric characters.");
            pinCode.requestFocus();
            return false;
        }

        String addr = address.getText().toString().trim();
        if (TextUtils.isEmpty(addr)) {
            address.setError("Delivery Address is required");
            address.requestFocus();
            return false;
        }
        if (addr.length() < 10) {
            address.setError("Address must be at least 10 characters long");
            address.requestFocus();
            return false;
        }

        String cityName = city.getText().toString().trim();
        if (TextUtils.isEmpty(cityName)) {
            city.setError("City is required");
            city.requestFocus();
            return false;
        }
        if (cityName.length() < 3 || !cityName.matches(NAME_REGEX)) {
            city.setError("City must be at least 3 characters and only contain letters");
            city.requestFocus();
            return false;
        }

        return true;
    }

}
