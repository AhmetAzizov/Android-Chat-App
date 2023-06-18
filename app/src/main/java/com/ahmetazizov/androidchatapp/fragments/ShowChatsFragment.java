package com.ahmetazizov.androidchatapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.ahmetazizov.androidchatapp.AddContactActivity;
import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.recyclerview_adapters.ContactsRecyclerViewAdapter;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.recyclerview_adapters.SearchAdapter;
import com.ahmetazizov.androidchatapp.models.AppUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;


public class ShowChatsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_chats, container, false);

    }


    public final static String TAG = "ShowChatsFragment";
    private ArrayList<AppUser> searchResult;
    private ContactsRecyclerViewAdapter adapter;
    private SearchAdapter searchAdapter;
    private FirebaseFirestore db;
    private RecyclerView recyclerView, searchCardList;
    private CardView cover;
    private CardView rateButton, searchCard, settingsButton;
    private SearchView searchCardInput;
    private LinearLayout searchCardLayout;
    Button addContactButton;


    public ContactsRecyclerViewAdapter getAdapter() {
        return this.adapter;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Activity currentActivity = getActivity();
        recyclerView = view.findViewById(R.id.chatsRecyclerView);
        cover = view.findViewById(R.id.loadingScreen);
        searchResult = new ArrayList<>();
//        settingsButton = view.findViewById(R.id.settingsButton);
//        rateButton = view.findViewById(R.id.rateButton);
        addContactButton = view.findViewById(R.id.addContactButton);

        searchCard = view.findViewById(R.id.searchCard);
        searchCardList = view.findViewById(R.id.searchCardList);
        searchCardInput = view.findViewById(R.id.searchCardInput);
        searchCardLayout = view.findViewById(R.id.searchCardLayout);

        adapter = new ContactsRecyclerViewAdapter(getContext(), Constants.contacts, cover, currentActivity);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager contactLayoutManager = new LinearLayoutManager(getContext());
        contactLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(contactLayoutManager);

        searchAdapter = new SearchAdapter(getContext(), searchResult);
        searchCardList.setAdapter(searchAdapter);
        LinearLayoutManager searchLayoutManager = new LinearLayoutManager(getContext());
        searchLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchCardList.setLayoutManager(searchLayoutManager);

        db = FirebaseFirestore.getInstance();

//        cover.animate().alpha(0.0f).setDuration(400);

        searchCardInput.setOnClickListener(v -> searchCardInput.setIconified(false));


        searchCardInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final ChangeBounds transition = new ChangeBounds();
                transition.setDuration(400L);

                TransitionManager.beginDelayedTransition(searchCardLayout, transition);

                String searchInput = searchCardInput.getQuery().toString().trim();

                searchResult(searchInput);

                searchAdapter.notifyDataSetChanged();

                return false;
            }
        });


        // Unused code. I forgot why I wrote this code in the first place. //
        recyclerView
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                // At this point the layout is complete and the
                                // dimensions of recyclerView and any child views
                                // are known.
                                recyclerView
                                        .getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);

                            }
                        });



        addContactButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddContactActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
        });
    }









    public void searchResult(String input) {

        searchResult.clear();

        for (AppUser contact : Constants.contacts) {
            if (contact.getUsername().toLowerCase().contains(input.toLowerCase())) {
                searchResult.add(contact);
            }
        }


        if (input.isEmpty()) searchResult.clear();
    }

    public ArrayList<AppUser> getSearchResult() {
        return searchResult;
    }

    public SearchAdapter getSearchAdapter() {
        return searchAdapter;
    }
}
