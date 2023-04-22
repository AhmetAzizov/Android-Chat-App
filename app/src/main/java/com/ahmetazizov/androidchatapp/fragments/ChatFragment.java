package com.ahmetazizov.androidchatapp.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.recyclerview_adapters.ChatsAdapter;
import com.ahmetazizov.androidchatapp.models.Message;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.models.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    ImageView backButton;
    CardView downArrow;
    ImageView downArrowIcon;
    CardView selectionOptions;
    ImageView cancelSelectionButton, selectionCopyButton, selectionFavoriteButton;
    TextView selectionCount;
    boolean firstTime = true;


    public ChatsAdapter getAdapter() {
        return this.chatsAdapter;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
        } else {
            Log.e(TAG, "bundle is null!");
        }


        db = FirebaseFirestore.getInstance();
        contactImage = view.findViewById(R.id.contactImage);
        contactName = view.findViewById(R.id.contactName);
        chatsRecyclerView = view.findViewById(R.id.chatsRecyclerView);
        chats = new ArrayList<>();
        sendButton = view.findViewById(R.id.sendButton);
        messageInput = view.findViewById(R.id.messageInput);
        profileCardView = view.findViewById(R.id.profileCardView);
        infoLabel = view.findViewById(R.id.infoLabel);
        backButton = view.findViewById(R.id.backButton);
        downArrow = view.findViewById(R.id.downArrow);
        downArrowIcon = view.findViewById(R.id.downArrowIcon);
        selectionOptions = view.findViewById(R.id.selectionOptions);
        cancelSelectionButton = view.findViewById(R.id.cancelSelectionButton);
        selectionCount = view.findViewById(R.id.selectionCount);
        selectionCopyButton = view.findViewById(R.id.selectionCopyButton);
        selectionFavoriteButton = view.findViewById(R.id.selectionFavoriteButton);


        chatsAdapter = new ChatsAdapter(getContext(), chats, chatsRecyclerView, selectionOptions, selectionCount);
        chatsRecyclerView.setAdapter(chatsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        // This code makes the recyclerview scrollable
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatsRecyclerView.setLayoutManager(layoutManager);

        layoutManager.setReverseLayout(true);
        chatsRecyclerView.scrollToPosition(0);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                infoLabel.setVisibility(View.GONE);
                getStatus();
            }
        }, 4500);



        Glide.with(getContext())
                .load(user.getImageURL())
                .override(300, 300)
                .centerCrop()
                .into(contactImage);

        contactName.setText(user.getUsername());

        getChats();




        selectionOptions.setOnClickListener(null);

        cancelSelectionButton.setOnClickListener(v -> {
            chatsAdapter.clearSelection();

            selectionOptions.animate().alpha(0.0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    selectionOptions.setVisibility(View.GONE);
                }
            });
        });

        selectionCopyButton.setOnClickListener(v -> {
            List<Message> deleteList = chatsAdapter.getSelectionList();

            if (deleteList.isEmpty()) return;

            StringBuilder copyContent = new StringBuilder();
            SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd, HH:mm");

            for (Message message : deleteList) {
                String messageDate = dateFormat.format(message.getExactTime());
                copyContent.append('[' + messageDate + "] " + message.getContent() + "\n");
            }


            // Get the system clipboard manager
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);

            // Create a new ClipData object to hold the text to be copied
            ClipData clip = ClipData.newPlainText("copied messages", copyContent);

            // Copy the text to the clipboard
            clipboard.setPrimaryClip(clip);

            if (deleteList.size() == 1) Toast.makeText(getContext(), "Message copied", Toast.LENGTH_SHORT).show();
            else Toast.makeText(getContext(), deleteList.size() + " messages copied", Toast.LENGTH_SHORT).show();

            chatsAdapter.clearSelection();

            selectionOptions.animate().alpha(0.0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    selectionOptions.setVisibility(View.GONE);
                }
            });
        });

        selectionFavoriteButton.setOnClickListener(v -> {


            List<Message> favoriteList = chatsAdapter.getSelectionList();

            for (Message message : favoriteList) {
                Message favoriteMessage = new Message(message.getSender(), message.getContent(), message.getTime(), message.getExactTime());
            }
        });


        chatsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int currentPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findFirstVisibleItemPosition();

                if (currentPosition >= 1 && chatsAdapter.getItemCount() != 0) downArrow.setVisibility(View.VISIBLE);
                else {
                    downArrow.setVisibility(View.GONE);
                    downArrowIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.Gray), PorterDuff.Mode.SRC_IN);
                }
            }
        });


        downArrow.setOnClickListener(v -> {
            chatsRecyclerView.scrollToPosition(0);
            downArrowIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.Gray), PorterDuff.Mode.SRC_IN);
        });



        backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, 0);
            fragmentTransaction.replace(R.id.frameLayout, new ShowChatsFragment(), "showChatsFragment").commit();
        });



        profileCardView.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.fade_out);

            // Create a Bundle object and set the data you want to pass
            Bundle bundle1 = new Bundle();
            bundle1.putSerializable("user", user);

            // Create a new instance of the fragment and set the bundle
            ProfilePage profilePage = new ProfilePage();
            profilePage.setArguments(bundle1);

            // Replace the current fragment with the new one
            fragmentTransaction.replace(R.id.frameLayout, profilePage, "profilePage").addToBackStack(null).commit();
        });

        sendButton.setOnClickListener(v -> {
            sendChats();

//                chatsAdapter.clearDeleteButton();
        });
    }


    private void getChats() {
        final CollectionReference chatRef = db.collection("chats").document(user.getChatReference()).collection("messages");

        chatRef.orderBy("exactTime", Query.Direction.DESCENDING).addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            chats.clear();

            if (value != null) {
                for (QueryDocumentSnapshot document : value) {

                    // Retrieves all the fields and puts it to a new Message object
                    String id = document.getId();
                    String sender = document.getString("sender");
                    String content = document.getString("content");
                    String time = document.getString("time");
                    String chatRef1 = user.getChatReference();
                    Timestamp timestamp = document.getTimestamp("exactTime");
                    Date date = timestamp.toDate();

                    Message message = new Message(id, sender, content, time, chatRef1, date);

                    chats.add(message);

                }
            }

            if (!firstTime) {
                if (getContext() != null) {
                    downArrowIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.LimeGreen), PorterDuff.Mode.SRC_IN);
                }
            } else {
                firstTime = false;
            }

            chatsAdapter.notifyDataSetChanged();
        });
    }



    private void sendChats() {
        String message = messageInput.getText().toString().trim();

        if (message.isEmpty()) return;


        Timestamp timestamp = Timestamp.now();


        // This converts the current to HH:mm format
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formattedTime = sdf.format(timestamp.toDate());


        // Creates a new Message object, fills it with specified information and sends it to the database
        Message newMessage = new Message(Constants.currentUser, message, formattedTime, timestamp.toDate());


        final CollectionReference chatRef = db.collection("chats").document(user.getChatReference()).collection("messages");

        chatRef.add(newMessage)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        chatsRecyclerView.scrollToPosition(0);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getContext(), "There was an error sending your message", Toast.LENGTH_SHORT).show();
                    }
                });

        messageInput.setText("");

        final DocumentReference docRef = db.collection("chats").document(user.getChatReference());

        Timestamp docTimestamp = Timestamp.now();

        Map<String, Object> data = new HashMap<>();
        data.put("time", docTimestamp);

        docRef.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }




    private void getStatus() {
        final DocumentReference userRef = db.collection("users").document(user.getUsername());

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                if (value != null && value.exists()) {
                    String status = value.getString("isOnline");
                    Timestamp lastOnline = value.getTimestamp("lastOnline");

                    if (status.equals("true")) infoLabel.setText("Online");
                    else infoLabel.setText(lastSeen(lastOnline));

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }



    private String lastSeen(Timestamp lastOnline) {
        long lastOnlineMilli = lastOnline.toDate().getTime();
        long currentTime = System.currentTimeMillis();

        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
        int lastOnlineHour = Integer.parseInt(hourFormat.format(lastOnlineMilli));
        int currentHour = Integer.parseInt(hourFormat.format(currentTime));
        int lastOnlineMinute = Integer.parseInt(minuteFormat.format(lastOnlineMilli));
        int currentMinute = Integer.parseInt(minuteFormat.format(currentTime));



        if (Math.abs(lastOnlineMilli - currentTime) > 86400000){
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
            String date = sdf.format(new Date(lastOnlineMilli));

            return "last seen " + date;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String date = sdf.format(new Date(lastOnlineMilli));

            if (currentHour < lastOnlineHour) {

                return "last seen yesterday at " + date;

            } else if(currentHour == lastOnlineHour) {

                if (currentMinute < lastOnlineMinute) return "last seen yesterday at " + date;
                else return "last seen today at " + date;

            } else {

                return "last seen today at " + date;

            }
        }
    }


}
