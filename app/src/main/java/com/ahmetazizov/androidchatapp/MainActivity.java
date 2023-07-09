package com.ahmetazizov.androidchatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;


import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmetazizov.androidchatapp.fragments.ChatColorPicker;
import com.ahmetazizov.androidchatapp.fragments.RequestFragment;
import com.ahmetazizov.androidchatapp.fragments.ShowChatsFragment;
import com.ahmetazizov.androidchatapp.models.Contact;
import com.ahmetazizov.androidchatapp.models.FavoriteImageMessage;
import com.ahmetazizov.androidchatapp.models.FavoriteTextMessage;
import com.ahmetazizov.androidchatapp.models.Request;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    private static final String TAG = "MainActivity";
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private NetworkStateReceiver networkStateReceiver;
    NavigationView navigationView;
    ShapeableImageView drawerImage;
    TextView drawerUsername, drawerEmail;
    ImageView menuButton;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onStart() {
        super.onStart();


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuButton = findViewById(R.id.menuButton);
        drawerLayout = findViewById(R.id.drawer_layout);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        View navigationHeader = navigationView.getHeaderView(0);

        drawerImage = navigationHeader.findViewById(R.id.drawer_image);
        drawerUsername = navigationHeader.findViewById(R.id.drawer_username);
        drawerEmail = navigationHeader.findViewById(R.id.drawer_email);

        getUsers();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            Constants.currentUserName = currentUser.getDisplayName();

            getRequests();
            setNavigationListener();
            getChats();  //  Get chat references of the user
            getFavorites();  // Get the favorite chats of the logged-in user
            getUserInformation();  // Get the information of logged-in user

            fillDrawerDetails(drawerImage, drawerUsername, drawerEmail);  // Fill the user details in the navigation drawer

            fillViewPager();  // Connect fragments with the tab layout

            isOnline();  // Update status of the user

            setNetworkStateReceiver();  // Register the BroadcastReceiver to listen for network state changes

            menuButton.setOnClickListener(v -> {

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });

        } else {
            Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
            startActivity(intent);
        }
    }

    public ViewPager2 getViewPager() {
        return viewPager;
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
    public void onBackPressed() {
        // This closes the navigation drawer if it is open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        int position = viewPager.getCurrentItem();

        if (position == 0) {
            ShowChatsFragment showChatsFragment = (ShowChatsFragment) viewPagerAdapter.getCurrentFragment(0);

            if (showChatsFragment != null && !showChatsFragment.getSearchResult().isEmpty()) {
                showChatsFragment.getSearchResult().clear();
                showChatsFragment.getSearchAdapter().notifyDataSetChanged();
                return;
            }
        }


//        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//
//            }
//        });

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



    private void fillDrawerDetails(ImageView drawerImage, TextView drawerUsername, TextView drawerEmail) {
        DocumentReference currentUser = db.collection("users").document(Constants.currentUserName);

        currentUser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // document data exists, extract data and fill into object

                    Contact currentUserData = document.toObject(Contact.class);

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


    private void getRequests() {
        final CollectionReference requestsRef = db.collection("users").document(Constants.currentUserName).collection("requests");

        Constants.requests.clear();

        requestsRef.orderBy("requestTime", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {

                                Request request = document.toObject(Request.class);

                                Constants.requests.add(request);
                            }
                        }


                        RequestFragment requestFragment = (RequestFragment) viewPagerAdapter.getCurrentFragment(1);

                        if (requestFragment.getRequestAdapter() != null) {
                            requestFragment.getRequestAdapter().notifyDataSetChanged();
                        }
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
                Contact user = document.toObject(Contact.class);

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
            } else if (value == null) {
                Log.w(TAG, "Value is Null");
                return;
            }

            Constants.favorites.clear();

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

            Log.d(TAG, "favorites list length: " + Constants.favorites.size());
        });
    }

    public void getChats() {
        final CollectionReference chatsRef = db.collection("chats");

        chatsRef.orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    } else if (value == null) {
                        Log.w(TAG, "Value is Null");
                        return;
                    }

                    Constants.contacts.clear();

                    for (QueryDocumentSnapshot document : value) {

                        String[] separateNames = document.getId().split("-");

                        if (separateNames[0].equalsIgnoreCase(Constants.currentUserName) || separateNames[1].equalsIgnoreCase(Constants.currentUserName)) {
                            sortUser(document.getId());  // Get contact names from the chat reference and send to contacts list
                        }
                    }

                    // ShowChatsFragment fragment = (ShowChatsFragment) getSupportFragmentManager().findFragmentByTag("showChatsFragment");
                    int position = viewPager.getCurrentItem();

                    if (position == 0) {
                        ShowChatsFragment showChatsFragment = (ShowChatsFragment) viewPagerAdapter.getCurrentFragment(0);

                        if (showChatsFragment.getAdapter() != null) {
                            showChatsFragment.getAdapter().notifyDataSetChanged();
                        }
                    }
        });
    }



    private void sortUser(String chatReference) {
        String[] chatRefSplit = chatReference.split("-");

        String tempUsername = (chatRefSplit[1].equals(Constants.currentUserName)) ? chatRefSplit[0] : chatRefSplit[1];

        for (Contact user : Constants.users) {
            if (user.getUsername().equals(tempUsername)) {
                user.setChatReference(chatReference);
                Constants.contacts.add(user);
                break;
            }
        }
    }


    private void getUserInformation() {
        DocumentReference userRef = db.collection("users").document(Constants.currentUserName);

        userRef.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    Constants.currentUser = document.toObject(Contact.class);
                    Log.d(TAG, "CurrentUser: " + Constants.currentUser.getUid());

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



    private void fillViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPagerAdapter.addFragment(new ShowChatsFragment(), "CHATS");
        viewPagerAdapter.addFragment(new RequestFragment(), "REQUESTS");

        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(viewPagerAdapter.getTitle(position));
        }).attach();
    }


    private void setNetworkStateReceiver() {
        networkStateReceiver = new NetworkStateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, intentFilter);
    }



    private void setNavigationListener() {
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_favorites:
                    drawerLayout.closeDrawer(GravityCompat.START);

                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        Intent favoritesIntent = new Intent(MainActivity.this, FavoritesActivity.class);
                        startActivity(favoritesIntent);
                        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_from_left); // For overriding activity switch animation
                    }, 190);

                    break;

                case R.id.nav_chatColor:
                    FragmentManager fragmentManager2 = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                    fragmentTransaction2.replace(R.id.frameLayout, new ChatColorPicker(), "chatColorPicker").addToBackStack(null).commit();

                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.nav_changePassword:
                    mAuth.sendPasswordResetEmail(Constants.currentUser.getEmail())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Password reset link has been sent to your email address", Toast.LENGTH_LONG).show();
                                }
                            });
                    break;
                case R.id.nav_logOut:
                    Timestamp timestamp = Timestamp.now();

                    Map<String, Object> data = new HashMap<>();
                    data.put("isOnline", "false");
                    data.put("lastOnline", timestamp);

                    DocumentReference docRef = db.collection("users").document(Constants.currentUserName);

                    docRef.update(data)
                            .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
            }

            return true;
        });
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

