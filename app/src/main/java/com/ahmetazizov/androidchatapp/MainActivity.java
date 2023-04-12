package com.ahmetazizov.androidchatapp;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    public final String TAG = "MainActivity";
    public static ArrayList<String> chats;
    public static ArrayList<User> users;
    public static String username;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        db = FirebaseFirestore.getInstance();
        chats = new ArrayList<String>();
        users = new ArrayList<User>();
        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            username = currentUser.getDisplayName();

            Map<String, Object> data = new HashMap<>();
            data.put("isOnline", "true");

            DocumentReference docRef = db.collection("users").document(username);

            docRef.update(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });


            // go to add fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ShowChats(), "showChatsFragment").commit();

        } else {
            Intent intent = new Intent(MainActivity.this , AuthenticationActivity.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");
    }


    @Override
    protected void onPause() {
        super.onPause();

        isOffline();
    }

    @Override
    protected void onStop() {
        super.onStop();

        isOffline();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isOffline();
    }

    @Override
    public void onBackPressed() {
        // Get the FragmentManager instance
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Loop through the list of fragments in the FragmentManager
        for (Fragment fragments : fragmentManager.getFragments()) {
            if (fragments != null && fragments.isVisible()) {
                String fragmentTag = fragments.getTag(); // Get the tag of the currently active fragment

                if (fragmentTag != null) {
                switch (fragmentTag) {
                    case "chatFragment":
                        ChatFragment fragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag("chatFragment");
                        fragment.getAdapter().clearDeleteButton();
                        break;

                    default: break;
                }
                }

                Log.d(TAG, "Currently active fragment tag: " + fragmentTag);
                break; // Break out of the loop after finding the active fragment
            }
        }
    }






    private void isOffline() {
        Timestamp timestamp = Timestamp.now();

        Map<String, Object> data = new HashMap<>();
        data.put("isOnline", "false");
        data.put("lastOnline", timestamp);


        DocumentReference docRef = db.collection("users").document(username);

        docRef.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}

