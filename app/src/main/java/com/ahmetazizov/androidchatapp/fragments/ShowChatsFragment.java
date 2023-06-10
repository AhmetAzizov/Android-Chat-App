package com.ahmetazizov.androidchatapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.ahmetazizov.androidchatapp.MainActivity;
import com.ahmetazizov.androidchatapp.recyclerview_adapters.ContactsRecyclerViewAdapter;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.recyclerview_adapters.SearchAdapter;
import com.ahmetazizov.androidchatapp.models.User;
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
    private ArrayList<User> searchResult;
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

        Log.d(TAG, "contactlist: " + Constants.contacts);

        // Firestore database reference
        db = FirebaseFirestore.getInstance();


        cover.animate().alpha(0.0f).setDuration(400);

//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@androidx.annotation.NonNull RecyclerView recyclerView, @androidx.annotation.NonNull RecyclerView.ViewHolder viewHolder, @androidx.annotation.NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@androidx.annotation.NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//            }
//        }).attachToRecyclerView(recyclerView);


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

        // Unused code //
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


//        settingsButton.setOnClickListener(v -> {
////            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
////            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////            fragmentTransaction.replace(R.id.frameLayout, new UserProfilePage(), "userProfilePage").commit();
//
//            MainActivity activity = (MainActivity) getActivity();
//
//            if (activity != null) {
//                DrawerLayout drawerLayout = activity.findViewById(R.id.drawer_layout);
//
//                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                    drawerLayout.closeDrawer(GravityCompat.START);
//                } else {
//                    drawerLayout.openDrawer(GravityCompat.START);
//                }
//            }
//        });

//        rateButton.setOnClickListener(v -> {
//            RatingDialog ratingDialog = new RatingDialog(getContext());
//            ratingDialog.show();
//            });

        addContactButton.setOnClickListener(v -> {
//            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_from_left);
//            fragmentTransaction.replace(R.id.frameLayout, new AddContactFragment(), "addContactFragment").addToBackStack(null).commit();

            Intent intent = new Intent(requireContext(), AddContactActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.zoom_out);
        });
    }









    public void searchResult(String input) {

        searchResult.clear();

        for (User contact : Constants.contacts) {
            if (contact.getUsername().toLowerCase().contains(input.toLowerCase())) {
                searchResult.add(contact);
            }
        }


        if (input.isEmpty()) searchResult.clear();
    }

    public ArrayList<User> getSearchResult() {
        return searchResult;
    }

    public SearchAdapter getSearchAdapter() {
        return searchAdapter;
    }
}
