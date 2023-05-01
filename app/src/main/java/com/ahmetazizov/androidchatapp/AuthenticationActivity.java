package com.ahmetazizov.androidchatapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.ahmetazizov.androidchatapp.fragments.LoginFragment;
import com.ahmetazizov.androidchatapp.fragments.RegisterFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationActivity extends AppCompatActivity {


    private final static String TAG = "AuthenticationActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            RegisterFragment fragment = (RegisterFragment) getSupportFragmentManager().findFragmentByTag("registerFragment");

            if (fragment == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.AuthFrameLayout, new RegisterFragment(), "registerFragment").commit();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
    }
}