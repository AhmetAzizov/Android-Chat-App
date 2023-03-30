package com.ahmetazizov.androidchatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationActivity extends AppCompatActivity {


    public final static String TAG = "mainActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            MainActivity.username = currentUser.getDisplayName();

            Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
            startActivity(intent);

        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.AuthFrameLayout, new registerFragment()).commit();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);


    }


//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        //Save the fragment's instance
//        getSupportFragmentManager().putFragment(outState, "myFragmentName", myFragment);
//
//        Log.d(TAG, "onSaveInstanceState: " + myFragment);
//    }
}