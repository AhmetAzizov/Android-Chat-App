package com.ahmetazizov.androidchatapp.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.MainActivity;
import com.ahmetazizov.androidchatapp.recyclerview_adapters.ContactsRecyclerViewAdapter;
import com.ahmetazizov.androidchatapp.dialogs.RatingDialog;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.recyclerview_adapters.SearchAdapter;
import com.ahmetazizov.androidchatapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowChatsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShowChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowChatsFragment newInstance(String param1, String param2) {
        ShowChatsFragment fragment = new ShowChatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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
    private ProgressBar loadingScreenProgressBar;
    private CardView rateButton, searchCard, settingsButton;
    private SearchView searchCardInput;
    private LinearLayout searchCardLayout;
    Button addContactButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.chatsRecyclerView);
        cover = view.findViewById(R.id.loadingScreen);
        loadingScreenProgressBar = view.findViewById(R.id.loadingScreenProgressBar);
        searchResult = new ArrayList<>();
        settingsButton = view.findViewById(R.id.settingsButton);
        rateButton = view.findViewById(R.id.rateButton);
        addContactButton = view.findViewById(R.id.addContactButton);

        searchCard = view.findViewById(R.id.searchCard);
        searchCardList = view.findViewById(R.id.searchCardList);
        searchCardInput = view.findViewById(R.id.searchCardInput);
        searchCardLayout = view.findViewById(R.id.searchCardLayout);

        adapter = new ContactsRecyclerViewAdapter(getContext(), Constants.contacts, cover);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager contactLayoutManager = new LinearLayoutManager(getContext());
        contactLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(contactLayoutManager);

        searchAdapter = new SearchAdapter(getContext(), searchResult);
        searchCardList.setAdapter(searchAdapter);
        LinearLayoutManager searchLayoutManager = new LinearLayoutManager(getContext());
        searchLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchCardList.setLayoutManager(searchLayoutManager);

        // Firestore database reference
        db = FirebaseFirestore.getInstance();


        cover.animate().alpha(0.0f).setDuration(400);



        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@androidx.annotation.NonNull RecyclerView recyclerView, @androidx.annotation.NonNull RecyclerView.ViewHolder viewHolder, @androidx.annotation.NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@androidx.annotation.NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        }).attachToRecyclerView(recyclerView);


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


        settingsButton.setOnClickListener(v -> {
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.frameLayout, new UserProfilePage(), "userProfilePage").commit();

            MainActivity activity = (MainActivity) getActivity();

            if (activity != null) {
                DrawerLayout drawerLayout = activity.findViewById(R.id.drawer_layout);

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        rateButton.setOnClickListener(v -> {
            RatingDialog ratingDialog = new RatingDialog(getContext());
            ratingDialog.show();
        });

        addContactButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, new AddContactFragment(), "addContactFragment").addToBackStack(null).commit();
        });

        getChats();
    }









    public void searchResult(String input) {

        searchResult.clear();

        for (User contact : Constants.users) {

            if (contact.getUsername().equals(Constants.currentUser)) continue;

            if (contact.getUsername().toLowerCase().contains(input.toLowerCase())) {

                searchResult.add(contact);

                Log.d(TAG, "searchResult chatreference: " + contact.getChatReference());
            }

        }

        if (input.isEmpty()) searchResult.clear();
    }


    public void getChats() {
        final CollectionReference chatsRef = db.collection("chats");

        chatsRef.orderBy("time", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                Constants.chats.clear();
                Constants.contacts.clear();

                for (QueryDocumentSnapshot document : value) {

                    String[] separateNames = document.getId().split("-");

                    if (separateNames[0].equalsIgnoreCase(Constants.currentUser) || separateNames[1].equalsIgnoreCase(Constants.currentUser)) {

                        sortUser(document.getId());
                    }
                }


                if (Constants.contacts.isEmpty()) cover.setAlpha(0.0f);

                adapter.notifyDataSetChanged();
            }
        });
    }


    private void sortUser(String chatReference) {
        String[] chatRefSplit = chatReference.split("-");
//        String tempUsername;

//        if (chatRefSplit[1].equals(Constants.currentUser)) tempUsername = chatRefSplit[0];
//        else tempUsername = chatRefSplit[1];

        String tempUsername = (chatRefSplit[1].equals(Constants.currentUser)) ? chatRefSplit[0] : chatRefSplit[1];


        for (User user : Constants.users) {
            if (user.getUsername().equals(tempUsername)) {
                user.setChatReference(chatReference);
                Constants.contacts.add(user);
            }
        }
    }


//    private void setChatListener(String ref) {
//        final CollectionReference chatRef = db.collection("chats").document(ref).collection("messages");
//
//        chatRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//
//                for (QueryDocumentSnapshot document : value) {
//                    Log.d(TAG, "sender: " + document.getString("sender") + "      content " + document.getString("content"));
//                }
//
//            }
//        });
//    }
}
