package com.ahmetazizov.androidchatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import com.ahmetazizov.androidchatapp.adapters.FavoritesAdapter;

public class FavoritesActivity extends AppCompatActivity {

    RecyclerView favoritesRecyclerview;
    FavoritesAdapter favoritesAdapter;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesRecyclerview = findViewById(R.id.favoritesRecyclerview);
        backButton = findViewById(R.id.backButton);

        favoritesAdapter = new FavoritesAdapter(this, Constants.favorites);
        favoritesRecyclerview.setAdapter(favoritesAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        favoritesRecyclerview.setLayoutManager(layoutManager);

        backButton.setOnClickListener(v -> {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.frameLayout, new ShowChatsFragment(), "showChatsFragment").commit();

            super.onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}