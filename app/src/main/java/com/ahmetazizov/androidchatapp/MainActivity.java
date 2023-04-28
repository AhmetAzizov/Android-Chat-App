package com.ahmetazizov.androidchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.ahmetazizov.androidchatapp.fragments.ChatFragment;
import com.ahmetazizov.androidchatapp.fragments.RegisterFragment;
import com.ahmetazizov.androidchatapp.fragments.ShowChatsFragment;
import com.ahmetazizov.androidchatapp.models.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;

    public final String TAG = "MainActivity";
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private NetworkStateReceiver networkStateReceiver;
    NavigationView navigationView;


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_favorites:
                    Toast.makeText(MainActivity.this, "favorites", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.nav_changePassword:
                    Toast.makeText(MainActivity.this, "change password", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.nav_logOut:
                    Timestamp timestamp = Timestamp.now();

                    Map<String, Object> data = new HashMap<>();
                    data.put("isOnline", "false");
                    data.put("lastOnline", timestamp);

                    DocumentReference docRef = db.collection("users").document(Constants.currentUser);

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

                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
                    startActivity(intent);
                    break;
            }


            return true;
        });


        if(currentUser != null){

            ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag("chatFragment");

            if (chatFragment == null) {
                Constants.currentUser = currentUser.getDisplayName();

                Map<String, Object> data = new HashMap<>();
                data.put("isOnline", "true");

                DocumentReference docRef = db.collection("users").document(Constants.currentUser);

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
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ShowChatsFragment(), "showChatsFragment").commit();

            }
        } else {
            Intent intent = new Intent(MainActivity.this , AuthenticationActivity.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register the BroadcastReceiver to listen for network state changes
        networkStateReceiver = new NetworkStateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, intentFilter);


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
                        if (fragment != null) {
                            if (fragment.getAdapter() != null) {
                                List<Message> deleteList = fragment.getAdapter().getSelectionList();
                                if (deleteList.size() == 0) {
                                    FragmentManager fragmentManager2 = getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager2.beginTransaction();
                                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, 0);
                                    fragmentTransaction.replace(R.id.frameLayout, new ShowChatsFragment(), "showChatsFragment").commit();
                                } else {
                                    fragment.getAdapter().clearSelection();
                                    fragment.getAdapter().closeSelectionList();
                                }
                            }
                        }
                        break;
                    case "showChatsFragment": break;

                    default: super.onBackPressed();
                }
                }

                Log.d(TAG, "Currently active fragment tag: " + fragmentTag);
                break; // Break out of the loop after finding the active fragment
            }
        }

        // This closes the navigation drawer if it is open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }






    private void isOffline() {
        Timestamp timestamp = Timestamp.now();

        Map<String, Object> data = new HashMap<>();
        data.put("isOnline", "false");
        data.put("lastOnline", timestamp);


        DocumentReference docRef = db.collection("users").document(Constants.currentUser);

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

