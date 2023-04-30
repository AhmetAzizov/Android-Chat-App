package com.ahmetazizov.androidchatapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.recyclerview_adapters.ChatsAdapter;
import com.ahmetazizov.androidchatapp.recyclerview_adapters.FavoritesAdapter;

public class FavoritesFragment extends Fragment {

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }


    RecyclerView favoritesRecyclerview;
    FavoritesAdapter favoritesAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favoritesRecyclerview = view.findViewById(R.id.favoritesRecyclerview);

        favoritesAdapter = new FavoritesAdapter(getContext(), Constants.favorites);
        favoritesRecyclerview.setAdapter(favoritesAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        favoritesRecyclerview.setLayoutManager(layoutManager);
    }
}