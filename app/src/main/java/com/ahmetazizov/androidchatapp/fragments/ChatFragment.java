package com.ahmetazizov.androidchatapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.ahmetazizov.androidchatapp.dialogs.SendImageDialog;
import com.ahmetazizov.androidchatapp.models.AppUser;
import com.ahmetazizov.androidchatapp.models.FavoriteImageMessage;
import com.ahmetazizov.androidchatapp.models.FavoriteTextMessage;
import com.ahmetazizov.androidchatapp.models.ImageMessage;
import com.ahmetazizov.androidchatapp.models.Message;
import com.ahmetazizov.androidchatapp.models.TextMessage;
import com.ahmetazizov.androidchatapp.adapters.ChatsAdapter;
import com.ahmetazizov.androidchatapp.R;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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



    private static final String FILE_NAME = "chatColor.txt";

    private static final int MAX_WIDTH = 7000;
    private static final int MAX_HEIGHT = 7000;

    private final static String TAG = "ChatFragment";
    private static final int PICK_IMAGE_REQUEST = 1;
    FirebaseFirestore db;
    ArrayList<Message> chats;
    ImageView contactImage, backButton, downArrowIcon;
    ImageView cancelSelectionButton, selectionCopyButton, selectionFavoriteButton, selectionDeleteButton;
    TextView contactName, infoLabel, selectionCount;
    AppUser user;
    ChatsAdapter chatsAdapter;
    RecyclerView chatsRecyclerView;
    CardView sendButton, downArrow;
    ShapeableImageView pickImageButton;
    EditText messageInput;
    MaterialToolbar selectionOptions, profileInfo;
    boolean firstTime = true;

    ConstraintLayout background;

    public ChatsAdapter getAdapter() {
        return this.chatsAdapter;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            user = (AppUser) bundle.getSerializable("user");
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
        background = view.findViewById(R.id.background);


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




        String currentColor = loadFile().trim();

        switch (currentColor) {
            case "red": background.setBackgroundColor(getResources().getColor(R.color.md_red_400)); break;
            case "green": background.setBackgroundColor(getResources().getColor(R.color.md_green_400)); break;
            case "blue": background.setBackgroundColor(getResources().getColor(R.color.md_blue_400)); break;
            case "purple": background.setBackgroundColor(getResources().getColor(R.color.md_purple_400)); break;
            case "orange": background.setBackgroundColor(getResources().getColor(R.color.md_orange_400)); break;
            case "yellow": background.setBackgroundColor(getResources().getColor(R.color.md_yellow_400)); break;
            case "brown": background.setBackgroundColor(getResources().getColor(R.color.md_brown_400)); break;
            case "pink": background.setBackgroundColor(getResources().getColor(R.color.md_pink_400)); break;
            default: break;
        }

        selectionOptions.setOnClickListener(null);

        cancelSelectionButton.setOnClickListener(v -> {
            chatsAdapter.closeSelectionList();
        });

        selectionDeleteButton.setOnClickListener(v -> {
            List<Message> deleteList = chatsAdapter.getSelectionList();

            for (Message textMessage : deleteList) {
                if (!textMessage.getSender().equals(Constants.currentUserName)) {
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

            for (Message textMessage : copyList) {

                if (textMessage instanceof ImageMessage) continue;

                TextMessage message = (TextMessage) textMessage;

                long messageDateMilli = textMessage.getExactTime().toDate().getTime();
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
            else Toast.makeText(getContext(), copyList.size() + " Messages copied", Toast.LENGTH_SHORT).show();

            chatsAdapter.closeSelectionList();
        });

        selectionFavoriteButton.setOnClickListener(v -> {
            final CollectionReference favMessageRef = db.collection("users").document(Constants.currentUserName).collection("favorites");
            List<String> currentFavorites = new ArrayList<>();
            List<Message> sortedFavorites = new ArrayList<>();
            List<Message> favoriteList = chatsAdapter.getSelectionList();

            favMessageRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                currentFavorites.add(document.getString("id"));
                                Log.d(TAG, "item id: " + document.getString("id"));
                            }

                            for (Message message : favoriteList) {
                                Log.d(TAG, "textMessage id: " + message.getId());
                                if (!currentFavorites.contains(message.getId())) {
                                    if (message instanceof TextMessage) {
                                        TextMessage textMessage = (TextMessage) message;
                                        String sender = textMessage.getSender();
                                        String receiver = (sender.equals(Constants.currentUserName)) ? user.getUsername() : Constants.currentUserName;
                                        sortedFavorites.add(new FavoriteTextMessage(textMessage.getId(), sender, receiver, textMessage.getContent(), null, textMessage.getChatRef(), textMessage.getMessageType(), textMessage.getExactTime(), null));
                                    } else {
                                        ImageMessage imageMessage = (ImageMessage) message;
                                        String sender = imageMessage.getSender();
                                        String receiver = (sender.equals(Constants.currentUserName)) ? user.getUsername() : Constants.currentUserName;
                                        sortedFavorites.add(new FavoriteImageMessage(imageMessage.getId(), sender, receiver, imageMessage.getUrl(), null, imageMessage.getChatRef(), imageMessage.getMessageType(), imageMessage.getExactTime(), null));
                                    }
                                }
                            }

                            if (!sortedFavorites.isEmpty()) {
                                AtomicBoolean error = new AtomicBoolean(false);
                                for (Message sortedTextMessage : sortedFavorites) {
                                    favMessageRef.add(sortedTextMessage)
                                            .addOnFailureListener(e -> {
                                                Log.w(TAG, "Error adding document", e);
                                                Toast.makeText(getContext(), "There was an error", Toast.LENGTH_SHORT).show();
                                                error.set(true);
                                            });
                                }

                                if (!error.get()) {
                                    if (sortedFavorites.size() == 1) Toast.makeText(getContext(), "TextMessage added to favorites!", Toast.LENGTH_SHORT).show();
                                    else Toast.makeText(getContext(), sortedFavorites.size() + " messages added to favorites!", Toast.LENGTH_SHORT).show();
                                }

                                chatsAdapter.closeSelectionList();
                            } else {
                                Toast.makeText(getContext(), "Message already added to favorites!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
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
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, 0);
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

                    // Retrieves all the fields and puts it to a new TextMessage object
                    String id = document.getId();
                    String sender = document.getString("sender");
                    String content = document.getString("content");
                    String imageURL = document.getString("url");
                    String chatRef = user.getChatReference();
                    String messageType = document.getString("messageType");
                    Timestamp exactTime = document.getTimestamp("exactTime");

                    // Converts the timestamp into local time and into a readable hour:minute format
                    // .toDate() method automatically converts a timestamp into local time
                    long timeMilli = exactTime.toDate().getTime();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    String time = timeFormat.format(timeMilli);

//                    if (messageType.equals("text"))

                    Message message = (messageType.equals("text")) ? new TextMessage(id, sender, content, time, chatRef, messageType, exactTime)
                            : new ImageMessage(id, sender, chatRef, messageType, exactTime, imageURL, time);

//                    TextMessage textMessage = new TextMessage(id, sender, content, time, chatRef, messageType, exactTime);

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

        // Creates a new TextMessage object, fills it with specified information and sends it to the database
        // TextMessage newMessage = new TextMessage(Constants.currentUser, message, formattedTime, messageType, timestamp);

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender", Constants.currentUserName);
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

            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);
                int imageWidth = options.outWidth;
                int imageHeight = options.outHeight;

                Log.d("Bitmap Size", "Width: " + imageWidth + ", Height: " + imageHeight);

                if (imageWidth > MAX_WIDTH || imageHeight > MAX_HEIGHT) {
                    Toast.makeText(getContext(), "Image size is too large", Toast.LENGTH_SHORT).show();
                } else {
                    SendImageDialog sendImageDialog = SendImageDialog.newInstance(imageUri, user);
                    sendImageDialog.show(requireActivity().getSupportFragmentManager(), "sendImageDialog");
                }
            } catch (Exception e) {
                Log.d(TAG, "error: " + e);
            }
        }
    }

    private String loadFile() {
        FileInputStream fis = null;

        try {
            fis = requireContext().openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            return sb.toString();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "default";
    }

}
