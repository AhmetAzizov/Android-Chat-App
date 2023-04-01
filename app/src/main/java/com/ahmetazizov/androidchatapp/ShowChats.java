package com.ahmetazizov.androidchatapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowChats#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowChats extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShowChats() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowChats.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowChats newInstance(String param1, String param2) {
        ShowChats fragment = new ShowChats();
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
















    public final static String TAG = "ShowChats";
    private FirebaseAuth mAuth;
    public static ArrayList<User> contacts;
    private ArrayList<User> searchResult;
    static ContactsRecyclerViewAdapter adapter;
    SearchAdapter searchAdapter;
    FirebaseFirestore db;
    MainActivity mainActivity;
    RecyclerView recyclerView;
    Button logOutButton;
    static CardView cover;
    ProgressBar loadingScreenProgressBar;
    CardView settingsButton;

    CardView addContactCard;
    TextView txtAddContact;
    Button buttonAddContact;
    LinearLayout addContactLayout;

    CardView searchCard;
    RecyclerView searchCardList;
    SearchView searchCardInput;
    LinearLayout searchCardLayout;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "Current User: " + MainActivity.username);

        recyclerView = view.findViewById(R.id.chatsRecyclerView);
        cover = view.findViewById(R.id.loadingScreen);
        loadingScreenProgressBar = view.findViewById(R.id.loadingScreenProgressBar);
        contacts = new ArrayList<>();
        searchResult = new ArrayList<>();
        settingsButton = view.findViewById(R.id.settingsButton);

        searchCard = view.findViewById(R.id.searchCard);
        searchCardList = view.findViewById(R.id.searchCardList);
        searchCardInput = view.findViewById(R.id.searchCardInput);
        searchCardLayout = view.findViewById(R.id.searchCardLayout);

        adapter = new ContactsRecyclerViewAdapter(getContext(), contacts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchAdapter = new SearchAdapter(getContext(), searchResult);
        searchCardList.setAdapter(searchAdapter);
        searchCardList.setLayoutManager(new LinearLayoutManager(getContext()));

        logOutButton = view.findViewById(R.id.logOutButton);
//        addContactCard = view.findViewById(R.id.addContactCard);
//        txtAddContact = view.findViewById(R.id.txtAddContact);
//        buttonAddContact = view.findViewById(R.id.buttonAddContact);
//        addContactLayout = view.findViewById(R.id.addContactLayout);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser authUser = mAuth.getCurrentUser();

        // Firestore database reference
        db = FirebaseFirestore.getInstance();


        final CollectionReference usersRef = db.collection("users");








//        addContactCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int status = (txtAddContact.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
//
//                TransitionManager.beginDelayedTransition(addContactLayout, new AutoTransition());
//                txtAddContact.setVisibility(status);
//                buttonAddContact.setVisibility(status);
//            }
//        });


//        searchCardInput.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        searchCardInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCardInput.setIconified(false);
            }
        });


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



//        buttonAddContact.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String searchResult = txtAddContact.getText().toString().trim();
//
//                boolean found = false;
//
//
//                for (User contact : contacts) {
//
//                    if (searchResult.equalsIgnoreCase(contact.getUsername())) {
//                        return;
//                    }
//
//                }
//
//
//                for (User user : MainActivity.users) {
//
//                    if (searchResult.equals(user.getUsername())) {
//
//                        found = true;
//                        cover.setVisibility(View.VISIBLE);
//                        String newChatRef = MainActivity.username + "-" + searchResult;
//                        CollectionReference colRef = db.collection("chats");
//
//                        // Create an empty document inside "chats" collection
//                        colRef.document(newChatRef)
//                                .set(new HashMap<String, Object>(), SetOptions.merge())
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        Toast.makeText(getContext(), "Successfully added a new contact!", Toast.LENGTH_SHORT).show();
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@androidx.annotation.NonNull Exception e) {
//                                        Toast.makeText(getContext(), "There was an error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                                    }
//                                });
//                    }
//                }
//
//                if (!found) Toast.makeText(getContext(), "User does not exist!", Toast.LENGTH_LONG).show();
//
//
//
////                    FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
////                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////
////                    // Create a Bundle object and set the data you want to pass
////                    Bundle bundle = new Bundle();
////                    bundle.putString("myData", "Hello World");
////
////                    // Create a new instance of the fragment and set the bundle
////                    AddFragment addFragment = new AddFragment();
////                    addFragment.setArguments(bundle);
////
////                    // Replace the current fragment with the new one
////                    fragmentTransaction.replace(R.id.frameLayout, addFragment).commit();
//
//
//            }
//        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.frameLayout, new UserProfilePage()).commit();
            }
        });



        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                Intent intent = new Intent(getContext(), AuthenticationActivity.class);
                startActivity(intent);
            }
        });



        getUsers();

        getChats();
    }















    public void searchResult(String input) {

        searchResult.clear();

        for (User contact : MainActivity.users) {

            if (contact.getUsername().equals(MainActivity.username)) continue;

            if (contact.getUsername().toLowerCase().contains(input.toLowerCase())) {

                searchResult.add(contact);

                Log.d(TAG, "searchResult chatreference: " + contact.getChatReference());
            }

        }

        if (input.isEmpty()) searchResult.clear();
    }


    public void getUsers() {
        final CollectionReference usersRef = db.collection("users");

        usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                MainActivity.users.clear();


                for (QueryDocumentSnapshot document : value) {
                    User user = document.toObject(User.class);

                    MainActivity.users.add(user);

                }
            }
        });
    }


    public void getChats() {
        final CollectionReference chatsRef = db.collection("chats");

        chatsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                mainActivity.chats.clear();
                contacts.clear();

                for (QueryDocumentSnapshot document : value) {

                    String[] separateNames = document.getId().split("-");

                    if (separateNames[0].equalsIgnoreCase(MainActivity.username) || separateNames[1].equalsIgnoreCase(MainActivity.username)) {

                        Log.d(TAG, "chat: " + document.getId());

                        sortUser(document.getId());

                    }
                }

                for (User user:MainActivity.users) Log.d(TAG, "user: " + user.getUsername());
                for (User user:contacts) Log.d(TAG, "sorted user: " + user.getUsername());


                if (contacts.isEmpty()) cover.setAlpha(0.0f);

                adapter.notifyDataSetChanged();
            }
        });
    }


    public void sortUser(String chatReference) {
        String[] chatRefSplit = chatReference.split("-");
        String tempUsername;

        if (chatRefSplit[1].equals(MainActivity.username)) tempUsername = chatRefSplit[0];
        else tempUsername = chatRefSplit[1];


        for (User user : MainActivity.users) {

            if (user.getUsername().equals(tempUsername)) {
                user.setChatReference(chatReference);
                contacts.add(user);
            }

        }

    }


}
