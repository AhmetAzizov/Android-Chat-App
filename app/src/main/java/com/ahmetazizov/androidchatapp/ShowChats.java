package com.ahmetazizov.androidchatapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
    static ChatsRecyclerViewAdapter adapter;
    FirebaseFirestore db;
    MainActivity mainActivity;
    RecyclerView recyclerView;
    Button searchButton;
    TextView editTextsSearch;
    static CardView cover;
    ProgressBar loadingScreenProgressBar;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "Current User: " + MainActivity.username);

        recyclerView = view.findViewById(R.id.chatsRecyclerView);
        cover = view.findViewById(R.id.loadingScreen);
        loadingScreenProgressBar = view.findViewById(R.id.loadingScreenProgressBar);
        contacts = new ArrayList<>();
        searchButton = view.findViewById(R.id.searchButton);
        editTextsSearch = view.findViewById(R.id.editTextsSearch);

        adapter = new ChatsRecyclerViewAdapter(getContext(), contacts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser authUser = mAuth.getCurrentUser();

        // Firestore database reference
        db = FirebaseFirestore.getInstance();


        final CollectionReference usersRef = db.collection("users");




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



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchResult = editTextsSearch.getText().toString().trim();

                boolean found = false;

                for (User user : MainActivity.users) {

                    if (searchResult.equals(user.getUsername())) {

                        found = true;
                        cover.setVisibility(View.VISIBLE);
                        String newChatRef = MainActivity.username + "-" + searchResult;
                        CollectionReference colRef = db.collection("chats");

                        // Create an empty document inside "chats" collection
                        colRef.document(newChatRef)
                                .set(new HashMap<String, Object>(), SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Successfully added a new contact!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                                        Toast.makeText(getContext(), "There was an error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                    }
                }

                if (!found) Toast.makeText(getContext(), "User does not exist!", Toast.LENGTH_LONG).show();



//                    FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                    // Create a Bundle object and set the data you want to pass
//                    Bundle bundle = new Bundle();
//                    bundle.putString("myData", "Hello World");
//
//                    // Create a new instance of the fragment and set the bundle
//                    AddFragment addFragment = new AddFragment();
//                    addFragment.setArguments(bundle);
//
//                    // Replace the current fragment with the new one
//                    fragmentTransaction.replace(R.id.frameLayout, addFragment).commit();


            }
        });



        getUsers();

        getChats();
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
                    if (document.getId().toLowerCase().contains(MainActivity.username.toLowerCase())) {

                        sortUser(document.getId());

                    }
                }

                for (User user:contacts) Log.d(TAG, "sorted user: " + user.getUsername());

                if (contacts.isEmpty()) cover.setAlpha(0.0f);

                adapter.notifyDataSetChanged();
            }
        });
    }


    public void sortUser(String chatReference) {
        String[] chatRefSplit = chatReference.split("-");
        String tempUsername;

        if (chatRefSplit[1] == MainActivity.username) tempUsername = chatRefSplit[0];
        else tempUsername = chatRefSplit[1];

        for (User user : MainActivity.users) {


            if (user.getUsername().equals(tempUsername)) {


                user.setChatReference(chatReference);
                contacts.add(user);

            }
        }

    }

}