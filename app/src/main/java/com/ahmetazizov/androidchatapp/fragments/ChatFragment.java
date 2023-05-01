package com.ahmetazizov.androidchatapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
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
import android.widget.Toolbar;

import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.dialogs.SendImageDialog;
import com.ahmetazizov.androidchatapp.models.FavoriteMessage;
import com.ahmetazizov.androidchatapp.recyclerview_adapters.ChatsAdapter;
import com.ahmetazizov.androidchatapp.models.Message;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.models.User;
import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatFragment extends Fragment {

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }


    private final static String TAG = "ChatFragment";
    private static final int PICK_IMAGE_REQUEST = 1;
    FirebaseFirestore db;
    ArrayList<Message> chats;
    ImageView contactImage, backButton, downArrowIcon;
    ImageView cancelSelectionButton, selectionCopyButton, selectionFavoriteButton, selectionDeleteButton;
    TextView contactName, infoLabel, selectionCount;
    User user;
    ChatsAdapter chatsAdapter;
    RecyclerView chatsRecyclerView;
    CardView sendButton, pickImageButton, downArrow;
    EditText messageInput;
    Toolbar profileInfo, selectionOptions;
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
        pickImageButton = view.findViewById(R.id.pickImageButton);
        messageInput = view.findViewById(R.id.messageInput);
        profileInfo = view.findViewById(R.id.profileInfo);
        infoLabel = view.findViewById(R.id.infoLabel);
        backButton = view.findViewById(R.id.backButton);
        downArrow = view.findViewById(R.id.downArrow);
        downArrowIcon = view.findViewById(R.id.downArrowIcon);
        selectionOptions = view.findViewById(R.id.selectionOptions);
        cancelSelectionButton = view.findViewById(R.id.cancelSelectionButton);
        selectionCount = view.findViewById(R.id.selectionCount);
        selectionCopyButton = view.findViewById(R.id.selectionCopyButton);
        selectionFavoriteButton = view.findViewById(R.id.selectionFavoriteButton);
        selectionDeleteButton = view.findViewById(R.id.selectionDeleteButton);

        chatsAdapter = new ChatsAdapter(getContext(), chats, chatsRecyclerView, selectionOptions, selectionCount);
        chatsRecyclerView.setAdapter(chatsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        // This code makes the recyclerview scrollable
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatsRecyclerView.setLayoutManager(layoutManager);

        layoutManager.setReverseLayout(true);
        chatsRecyclerView.scrollToPosition(0);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            getStatus();
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
            chatsAdapter.closeSelectionList();
        });

        selectionDeleteButton.setOnClickListener(v -> {
            List<Message> deleteList = chatsAdapter.getSelectionList();

            for (Message message : deleteList) {
                if (!message.getSender().equals(Constants.currentUser)) {
                    Toast.makeText(getContext(), "You can only delete your own messages!", Toast.LENGTH_SHORT).show();
                    chatsAdapter.closeSelectionList();
                    return;
                }
            }

            Toast.makeText(getContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();

            chatsAdapter.closeSelectionList();
        });


        selectionCopyButton.setOnClickListener(v -> {
            List<Message> copyList = chatsAdapter.getSelectionList();

            if (copyList.isEmpty()) return;

            StringBuilder copyContent = new StringBuilder();
            SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd, HH:mm");

            for (Message message : copyList) {
                long messageDateMilli = message.getExactTime().toDate().getTime();
                String messageDate = dateFormat.format(messageDateMilli);
                copyContent.append('[' + messageDate + "] " + message.getContent() + "\n");
            }


            // Get the system clipboard manager
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);

            // Create a new ClipData object to hold the text to be copied
            ClipData clip = ClipData.newPlainText("copied messages", copyContent);

            // Copy the text to the clipboard
            clipboard.setPrimaryClip(clip);

            if (copyList.size() == 1) Toast.makeText(getContext(), "Message copied", Toast.LENGTH_SHORT).show();
            else Toast.makeText(getContext(), copyList.size() + " messages copied", Toast.LENGTH_SHORT).show();

            chatsAdapter.closeSelectionList();
        });

        selectionFavoriteButton.setOnClickListener(v -> {
            final CollectionReference favMessageRef = db.collection("users").document(Constants.currentUser).collection("favorites");
            List<String> currentFavorites = new ArrayList<>();
            List<Message> sortedFavorites = new ArrayList<>();
            List<Message> favoriteList = chatsAdapter.getSelectionList();


            favMessageRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                currentFavorites.add(document.getString("id"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }).addOnSuccessListener(queryDocumentSnapshots -> {
                        for (Message message : favoriteList) {
                            if (!currentFavorites.contains(message.getId())) {
                                sortedFavorites.add(new FavoriteMessage(message.getId(), message.getSender(), message.getContent(), null, message.getChatRef(), message.getMessageType(), message.getExactTime(), null));
                            }
                        }
                        if (!sortedFavorites.isEmpty()) {
                            AtomicBoolean error = new AtomicBoolean(false);
                            for (Message sortedMessage : sortedFavorites) {
                                favMessageRef.add(sortedMessage)
                                        .addOnFailureListener(e -> {
                                            Log.w(TAG, "Error adding document", e);
                                            Toast.makeText(getContext(), "There was an error", Toast.LENGTH_SHORT).show();
                                            error.set(true);
                                        });
                            }

                            if (!error.get()) {
                                if (sortedFavorites.size() == 1) Toast.makeText(getContext(), "Message added to favorites!", Toast.LENGTH_SHORT).show();
                                else Toast.makeText(getContext(), sortedFavorites.size() + " messages added to favorites!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Message already added to favorites!", Toast.LENGTH_SHORT).show();
                        }
                    });

            chatsAdapter.closeSelectionList();
        });


        chatsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int currentPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager()))
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



        profileInfo.setOnClickListener(v -> {
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

        pickImageButton.setOnClickListener(v -> openFileChooser());

        sendButton.setOnClickListener(v -> sendChats());
    }


    private void getChats() {
        final CollectionReference dbChatRef = db.collection("chats").document(user.getChatReference()).collection("messages");

        dbChatRef.orderBy("exactTime", Query.Direction.DESCENDING).addSnapshotListener((value, e) -> {
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
                    String chatRef = user.getChatReference();
                    String messageType = document.getString("messageType");
                    Timestamp exactTime = document.getTimestamp("exactTime");

                    // Converts the timestamp into local time and into a readable hour:minute format
                    // .toDate() method automatically converts a timestamp into local time
                    long timeMilli = exactTime.toDate().getTime();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    String time = timeFormat.format(timeMilli);

                    Message message = new Message(id, sender, content, time, chatRef, messageType, exactTime);

                    chats.add(message);
                }
            }

            if (!firstTime && getContext() != null) {
                    downArrowIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.LimeGreen), PorterDuff.Mode.SRC_IN);
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

        // Creates a new Message object, fills it with specified information and sends it to the database
//        Message newMessage = new Message(Constants.currentUser, message, formattedTime, messageType, timestamp);

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender", Constants.currentUser);
        messageData.put("content", message);
        messageData.put("messageType", "text");
        messageData.put("exactTime", timestamp);


        final CollectionReference chatRef = db.collection("chats").document(user.getChatReference()).collection("messages");

        chatRef.add(messageData)
                .addOnSuccessListener(documentReference -> chatsRecyclerView.scrollToPosition(0))
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(getContext(), "There was an error sending your message", Toast.LENGTH_SHORT).show();
                });

        messageInput.setText("");

        final DocumentReference docRef = db.collection("chats").document(user.getChatReference());

        Timestamp docTimestamp = Timestamp.now();

        Map<String, Object> data = new HashMap<>();
        data.put("time", docTimestamp);

        docRef.set(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }


    private void getStatus() {
        final DocumentReference userRef = db.collection("users").document(user.getUsername());

        userRef.addSnapshotListener((value, error) -> {
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

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){

            Uri imageUri = data.getData();

            SendImageDialog sendImageDialog = SendImageDialog.newInstance(imageUri);
            sendImageDialog.show(requireActivity().getSupportFragmentManager(), "sendImageDialog");
        }
    }

}
