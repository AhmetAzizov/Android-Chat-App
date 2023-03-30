package com.ahmetazizov.androidchatapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    public final String TAG = "MainActivity";
    public static ArrayList<String> chats;
    public static ArrayList<User> users;
    public static String username;

    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

        chats = new ArrayList<String>();
        users = new ArrayList<User>();
        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            username = currentUser.getDisplayName();

            // go to add fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ShowChats()).commit();

        } else {
            Intent intent = new Intent(MainActivity.this , AuthenticationActivity.class);
            startActivity(intent);        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


}

