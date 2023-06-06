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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmetazizov.androidchatapp.FavoritesActivity;
import com.ahmetazizov.androidchatapp.fragments.ChatColorPicker;
import com.ahmetazizov.androidchatapp.fragments.ChatFragment;
import com.ahmetazizov.androidchatapp.fragments.FavoritesFragment;
import com.ahmetazizov.androidchatapp.fragments.ShowChatsFragment;
import com.ahmetazizov.androidchatapp.models.FavoriteImageMessage;
import com.ahmetazizov.androidchatapp.models.FavoriteTextMessage;
import com.ahmetazizov.androidchatapp.models.Message;
import com.ahmetazizov.androidchatapp.models.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    private static final String TAG = "MainActivity";
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private NetworkStateReceiver networkStateReceiver;
    NavigationView navigationView;
    ImageView drawerImage;
    TextView drawerUsername, drawerEmail;


    @Override
    protected void onStart() {
        super.onStart();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        View navigationHeader = navigationView.getHeaderView(0);

        drawerImage = navigationHeader.findViewById(R.id.drawer_image);
        drawerUsername = navigationHeader.findViewById(R.id.drawer_username);
        drawerEmail = navigationHeader.findViewById(R.id.drawer_email);

        getUsers();

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_favorites:
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////                    fragmentTransaction.setCustomAnimations(0, R.anim.enter_from_left);
//                    fragmentTransaction.replace(R.id.frameLayout, new FavoritesFragment(), "favoritesFragment").addToBackStack(null).commit();

                    Intent favoritesIntent = new Intent(MainActivity.this, FavoritesActivity.class);
                    startActivity(favoritesIntent);
                    overridePendingTransition(0, 0); // For disabling activity interface animation

                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.nav_chatColor:
                    FragmentManager fragmentManager2 = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                    fragmentTransaction2.replace(R.id.frameLayout, new ChatColorPicker(), "chatColorPicker").addToBackStack(null).commit();

                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.nav_changePassword:
                    Toast.makeText(MainActivity.this, "change password", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.nav_logOut:
                    Timestamp timestamp = Timestamp.now();

                    Map<String, Object> data = new HashMap<>();
                    data.put("isOnline", "false");
                    data.put("lastOnline", timestamp);

                    DocumentReference docRef = db.collection("users").document(Constants.currentUserName);

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

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            Constants.currentUserName = currentUser.getDisplayName();

            getChats();
            getFavorites();
            getUserInformation();

            fillDrawerDetails(drawerImage, drawerUsername, drawerEmail);

            ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag("chatFragment");

            if (chatFragment == null) {

                isOnline();

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
    protected void onResume() {
        super.onResume();

        isOnline();
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
                                    fragment.getAdapter().closeSelectionList();
                                }
                            }
                        }
                        break;
                    case "showChatsFragment":
                        ShowChatsFragment showChatsFragment = (ShowChatsFragment) getSupportFragmentManager().findFragmentByTag("showChatsFragment");

                        if (showChatsFragment != null && !showChatsFragment.getSearchResult().isEmpty()) {
                            showChatsFragment.getSearchResult().clear();
                            showChatsFragment.getSearchAdapter().notifyDataSetChanged();
                        } else {
                            MaterialAlertDialogBuilder exitDialog = new MaterialAlertDialogBuilder(this, R.style.exitDialogTheme);

                            exitDialog
                                    .setTitle("Are you sure you want to exit the application?")
                                            .setPositiveButton("Yes", (dialog, which) -> {
                                                isOffline();
                                                super.onBackPressed();
                                            })
                                            .setNegativeButton("No", (dialog, which) -> {
                                                dialog.dismiss();
                                            })
                                            .show();
                        }

                        break;

                    default:
                        super.onBackPressed();
                }
                }
                break; // Break out of the loop after finding the active fragment
            }
        }

        // This closes the navigation drawer if it is open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }






    private void fillDrawerDetails(ImageView drawerImage, TextView drawerUsername, TextView drawerEmail) {
        DocumentReference currentUser = db.collection("users").document(Constants.currentUserName);

        currentUser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // document data exists, extract data and fill into object

                    User currentUserData = document.toObject(User.class);

                    Glide.with(this)
                            .load(currentUserData.getImageURL())
                            .override(1000, 1000)
                            .centerCrop()
                            .into(drawerImage);

                    drawerUsername.setText(currentUserData.getUsername());
                    drawerEmail.setText(currentUserData.getEmail());

                } else {
                    Log.d(TAG, "Error fetching user data");
                    Toast.makeText(MainActivity.this, "Error fetching user data", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d(TAG, "Error fetching user data");
                Toast.makeText(MainActivity.this, "Error fetching user data", Toast.LENGTH_LONG).show();
            }
        });
    }



    public void getUsers() {
        final CollectionReference usersRef = db.collection("users");

        usersRef.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            Constants.users.clear();


            for (QueryDocumentSnapshot document : value) {
                User user = document.toObject(User.class);

                Constants.users.add(user);
            }
        });
    }


    private void getFavorites() {
        final CollectionReference favoritesRef = db.collection("users").document(Constants.currentUserName).collection("favorites");

        favoritesRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }
//                       setTheme(R.style.Theme_AndroidChatApp);


            Constants.favorites.clear();

            if (value != null) {
                for (QueryDocumentSnapshot document : value) {

                      String id = document.getString("id");
                      String sender = document.getString("sender");
                      String receiver = document.getString("receiver");
                      String content = document.getString("content");
                      String imageURL = document.getString("url");
                      String chatRef = document.getString("chatRef");
                      String messageType = document.getString("messageType");
                      Timestamp exactTime = document.getTimestamp("exactTime");
                      String selfId = document.getId();

                      long timeMilli = exactTime.toDate().getTime();
                      SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                      String time = timeFormat.format(timeMilli);

                      if (messageType.equals("text")) {
                          Constants.favorites.add(new FavoriteTextMessage(id, sender, receiver, content, time, chatRef, messageType, exactTime, selfId));
                      } else {
                          Constants.favorites.add(new FavoriteImageMessage(id, sender, receiver, imageURL, time, chatRef, messageType, exactTime, selfId));
                      }

                }
            }

            Log.d(TAG, "favorites list length: " + Constants.favorites.size());
        });

    }

    public void getChats() {
        final CollectionReference chatsRef = db.collection("chats");

        chatsRef.orderBy("time", Query.Direction.DESCENDING).addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            Constants.contacts.clear();

            for (QueryDocumentSnapshot document : value) {

                String[] separateNames = document.getId().split("-");

                if (separateNames[0].equalsIgnoreCase(Constants.currentUserName) || separateNames[1].equalsIgnoreCase(Constants.currentUserName)) {
                    sortUser(document.getId());
                }
            }

            ShowChatsFragment fragment = (ShowChatsFragment) getSupportFragmentManager().findFragmentByTag("showChatsFragment");

            if (fragment != null) {
                fragment.getAdapter().notifyDataSetChanged();
            }
        });
    }


//    private void getRequests() {
//        final CollectionReference requestsRef = db.collection("users").document(Constants.currentUserName).collection("requests");
//
//        requestsRef.orderBy("requestTime", Query.Direction.DESCENDING)
//                .get()
//                .addOnCompleteListener(task -> {
//
//                });
//    }


    private void getUserInformation() {
        DocumentReference userRef = db.collection("users").document(Constants.currentUserName);

        userRef.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    Constants.currentUser = document.toObject(User.class);

                } else {
                    Log.d(TAG, "No such document found!");
                }
            } else {
                Toast.makeText(this, "Error on getting user data", Toast.LENGTH_SHORT).show();
            }

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error on getting user data", Toast.LENGTH_SHORT).show();
        });
    }


    private void sortUser(String chatReference) {
        String[] chatRefSplit = chatReference.split("-");

        String tempUsername = (chatRefSplit[1].equals(Constants.currentUserName)) ? chatRefSplit[0] : chatRefSplit[1];

        for (User user : Constants.users) {
            if (user.getUsername().equals(tempUsername)) {
                user.setChatReference(chatReference);
                Constants.contacts.add(user);
                break;
            }
        }
    }

    private void isOffline() {
        Timestamp timestamp = Timestamp.now();

        Map<String, Object> data = new HashMap<>();
        data.put("isOnline", "false");
        data.put("lastOnline", timestamp);

        DocumentReference docRef = db.collection("users").document(Constants.currentUserName);

        docRef.update(data)
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    private void isOnline() {
        Map<String, Object> data = new HashMap<>();
        data.put("isOnline", "true");

        DocumentReference docRef = db.collection("users").document(Constants.currentUserName);

        docRef.update(data)
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

    }
}

