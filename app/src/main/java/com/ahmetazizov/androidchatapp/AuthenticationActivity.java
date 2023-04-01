package com.ahmetazizov.androidchatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationActivity extends AppCompatActivity {


    public final static String TAG = "AuthenticationActivity";
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
            RegisterFragment fragment = (RegisterFragment) getSupportFragmentManager().findFragmentByTag("registerFragment");

            if (fragment == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.AuthFrameLayout, new RegisterFragment(), "registerFragment").commit();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

//        Log.d("registerFragment", "activity onCreate: " + registerFragment.imageUri);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        Log.d("registerFragment", "activity onResume: " + registerFragment.imageUri);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        Log.d("registerFragment", "activity onPause: " + registerFragment.imageUri);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        Log.d("registerFragment", "activity onStop: " + registerFragment.imageUri);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        Log.d("registerFragment", "activity onDestroy: " + registerFragment.imageUri);
//    }

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