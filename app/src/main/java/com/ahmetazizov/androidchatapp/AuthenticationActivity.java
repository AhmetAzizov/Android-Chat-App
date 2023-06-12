package com.ahmetazizov.androidchatapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.ahmetazizov.androidchatapp.fragments.LoginFragment;
import com.ahmetazizov.androidchatapp.fragments.RegisterFragment;
import com.ahmetazizov.androidchatapp.fragments.ShowChatsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationActivity extends AppCompatActivity {


    private final static String TAG = "AuthenticationActivity";
    private FirebaseAuth mAuth;
//    TabLayout tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mAuth = FirebaseAuth.getInstance();
//        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Check if user is signed in and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
//            RegisterFragment fragment = (RegisterFragment) getSupportFragmentManager().findFragmentByTag("registerFragment");
//
//            if (fragment == null) {
//                getSupportFragmentManager().beginTransaction().replace(R.id.AuthFrameLayout, new RegisterFragment(), "registerFragment").commit();
//            }

            fillViewPager();
        }
    }




    private void fillViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPagerAdapter.addFragment(new RegisterFragment(), "REGISTER");
        viewPagerAdapter.addFragment(new LoginFragment(), "LOGIN");

        viewPager.setAdapter(viewPagerAdapter);

//        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
//            tab.setText(viewPagerAdapter.getTitle(position));
//        }).attach();
    }

}