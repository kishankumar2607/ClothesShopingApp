package com.example.clothesshoppingapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.clothesshoppingapp.fragments.CartFragment;
import com.example.clothesshoppingapp.fragments.HomeFragment;
import com.example.clothesshoppingapp.R;
import com.example.clothesshoppingapp.fragments.SearchFragment;
import com.example.clothesshoppingapp.fragments.SettingFragment;
import com.example.clothesshoppingapp.fragments.WishListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        bottomNavigationView = findViewById(R.id.bottomNavView);
        frameLayout = findViewById(R.id.fragment_container);
        TextView headerText = findViewById(R.id.header_text);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.radical_red));

        headerText.setText(getGreeting());

        bottomNavigationView .setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.home){
                    loadFragment(new HomeFragment(), false);
                } else if (itemId == R.id.wishlist) {
                    loadFragment(new WishListFragment(), false);
                } else if (itemId == R.id.cart) {
                    loadFragment(new CartFragment(), false);
                } else if (itemId == R.id.search) {
                    loadFragment(new SearchFragment(), false);
                } else if (itemId == R.id.setting) {
                    Log.d("MainActivity", "Settings selected");
                    loadFragment(new SettingFragment(), false);
                }

                return true;
            }
        });

        loadFragment(new HomeFragment(), true);

    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized){
            fragmentTransaction.add(R.id.fragment_container, fragment);
        }else {
            fragmentTransaction.replace(R.id.fragment_container, fragment);
        }

        fragmentTransaction.commit();
    }

    private String getGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour < 12) {
            return "Good Morning";
        } else if (hour < 18) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
        }
    }
}