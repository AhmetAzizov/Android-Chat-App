package com.ahmetazizov.androidchatapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.DocumentTransform;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }









    public final static String TAG = "ChatFragment";
    FirebaseFirestore db;
    ArrayList<Message> chats;
    ImageView contactImage;
    TextView contactName;
    User user;
    ChatsAdapter chatsAdapter;
    RecyclerView chatsRecyclerView;
    CardView sendButton;
    EditText messageInput;
    CardView profileCardView;
    TextView infoLabel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("user");


        db = FirebaseFirestore.getInstance();
        contactImage = view.findViewById(R.id.contactImage);
        contactName = view.findViewById(R.id.contactName);
        chatsRecyclerView = view.findViewById(R.id.chatsRecyclerView);
        chats = new ArrayList<>();
        sendButton = view.findViewById(R.id.sendButton);
        messageInput = view.findViewById(R.id.messageInput);
        profileCardView = view.findViewById(R.id.profileCardView);
        infoLabel = view.findViewById(R.id.infoLabel);

        chatsAdapter = new ChatsAdapter(getContext(), chats);
        chatsRecyclerView.setAdapter(chatsAdapter);
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                infoLabel.setVisibility(View.GONE);
            }
        }, 5000);



        Glide.with(getContext())
                .load(user.getImageURL())
                .override(300, 300)
                .centerCrop()
                .into(contactImage);

        contactName.setText(user.getUsername());

        Log.d(TAG, "chatref: " + user.getChatReference());

        getChats();




        profileCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Create a Bundle object and set the data you want to pass
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", (Serializable) user);

                // Create a new instance of the fragment and set the bundle
                ProfilePage profilePage = new ProfilePage();
                profilePage.setArguments(bundle);


                // Replace the current fragment with the new one
                fragmentTransaction.replace(R.id.frameLayout, profilePage).commit();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString().trim();

                if (message.isEmpty()) return;

                Calendar calendar = Calendar.getInstance();
                Date now = calendar.getTime();
                SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm");
                String formattedTime = formatterTime.format(now);

                SimpleDateFormat formatterExactTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String formattedExactTime = formatterExactTime.format(now);

                Message newMessage = new Message(MainActivity.username, message, formattedTime, formattedExactTime);


                final CollectionReference chatRef = db.collection("chats").document(user.getChatReference()).collection("messages");

                chatRef.add(newMessage)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

                messageInput.setText("");
                messageInput.clearFocus();
            }
        });
    }



    public void getChats() {
        final CollectionReference chatRef = db.collection("chats").document(user.getChatReference()).collection("messages");

        chatRef.orderBy("exactTime").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                chats.clear();


                for (QueryDocumentSnapshot document : value) {
                    Message message = document.toObject(Message.class);

                    chats.add(message);

                    Log.d(TAG, "message sender: " + message.getSender());

                }

                chatsAdapter.notifyDataSetChanged();
            }
        });
    }
}