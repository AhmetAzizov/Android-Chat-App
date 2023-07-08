package com.ahmetazizov.androidchatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmetazizov.androidchatapp.dialogs.SendImageDialog;
import com.ahmetazizov.androidchatapp.models.Contact;
import com.ahmetazizov.androidchatapp.models.FavoriteImageMessage;
import com.ahmetazizov.androidchatapp.models.FavoriteTextMessage;
import com.ahmetazizov.androidchatapp.models.ImageMessage;
import com.ahmetazizov.androidchatapp.models.Message;
import com.ahmetazizov.androidchatapp.models.TextMessage;
import com.ahmetazizov.androidchatapp.adapters.ChatsAdapter;
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

public class ChatActivity extends AppCompatActivity {


    private static final String FILE_NAME = "chatColor.txt";

    private static final int MAX_WIDTH = 7000;
    private static final int MAX_HEIGHT = 7000;

    private final static String TAG = "ChatActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    FirebaseFirestore db;
    ArrayList<Message> chats;
    ImageView contactImage, backButton, downArrowIcon;
    ImageView cancelSelectionButton, selectionCopyButton, selectionFavoriteButton, selectionDeleteButton;
    TextView contactName, infoLabel, selectionCount;
    Contact user;
    ChatsAdapter chatsAdapter;
    RecyclerView chatsRecyclerView;
    CardView sendButton, downArrow;
    ShapeableImageView pickImageButton;
    EditText messageInput;
    MaterialToolbar selectionOptions, profileInfo;
    boolean firstTime = true;
    boolean isOnline;

    ConstraintLayout background;

    public ChatsAdapter getAdapter() {
        return this.chatsAdapter;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Constants.appClosed = false;

        Log.d(TAG, "asdf onCreate: 2, appClosed: " + Constants.appClosed);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            user = (Contact) bundle.getSerializable("user");
        } else {
            Log.e(TAG, "bundle is null!");
        }

        Log.d(TAG, "CurrentUser: " + user.getEmail());

        db = FirebaseFirestore.getInstance();
        contactImage = findViewById(R.id.contactImage);
        contactName = findViewById(R.id.contactName);
        chatsRecyclerView = findViewById(R.id.chatsRecyclerView);
        chats = new ArrayList<>();
        sendButton = findViewById(R.id.sendButton);
        pickImageButton = findViewById(R.id.pickImageButton);
        messageInput = findViewById(R.id.messageInput);
        profileInfo = findViewById(R.id.profileInfo);
        infoLabel = findViewById(R.id.infoLabel);
        backButton = findViewById(R.id.backButton);
        downArrow = findViewById(R.id.downArrow);
        downArrowIcon = findViewById(R.id.downArrowIcon);
        selectionOptions = findViewById(R.id.selectionOptions);
        cancelSelectionButton = findViewById(R.id.cancelSelectionButton);
        selectionCount = findViewById(R.id.selectionCount);
        selectionCopyButton = findViewById(R.id.selectionCopyButton);
        selectionFavoriteButton = findViewById(R.id.selectionFavoriteButton);
        selectionDeleteButton = findViewById(R.id.selectionDeleteButton);
        background = findViewById(R.id.background);


        chatsAdapter = new ChatsAdapter(this, chats, chatsRecyclerView, selectionOptions, selectionCount);
        chatsRecyclerView.setAdapter(chatsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // This code makes the recyclerview scrollable
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatsRecyclerView.setLayoutManager(layoutManager);

        layoutManager.setReverseLayout(true);
        chatsRecyclerView.scrollToPosition(0);



        Handler handler = new Handler();
        handler.postDelayed(() -> getStatus(), 4500);

//        handler.postDelayed(() -> isOnline(), 1000);

        isOnline();


        Glide.with(this)
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
                    Toast.makeText(this, "You can only delete your own messages!", Toast.LENGTH_SHORT).show();
                    chatsAdapter.closeSelectionList();
                    return;
                }
            }

            Toast.makeText(this, "Successfully deleted", Toast.LENGTH_SHORT).show();

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
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            // Create a new ClipData object to hold the text to be copied
            ClipData clip = ClipData.newPlainText("copied messages", copyContent);

            // Copy the text to the clipboard
            clipboard.setPrimaryClip(clip);

            if (copyList.size() == 1) Toast.makeText(this, "Message copied", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, copyList.size() + " Messages copied", Toast.LENGTH_SHORT).show();

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
                                        Toast.makeText(this, "There was an error", Toast.LENGTH_SHORT).show();
                                        error.set(true);
                                    });
                        }

                        if (!error.get()) {
                            if (sortedFavorites.size() == 1) Toast.makeText(this, "TextMessage added to favorites!", Toast.LENGTH_SHORT).show();
                            else Toast.makeText(this, sortedFavorites.size() + " messages added to favorites!", Toast.LENGTH_SHORT).show();
                        }

                        chatsAdapter.closeSelectionList();
                    } else {
                        Toast.makeText(this, "Message already added to favorites!", Toast.LENGTH_SHORT).show();
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
                    downArrowIcon.setColorFilter(ContextCompat.getColor(ChatActivity.this, R.color.Gray), PorterDuff.Mode.SRC_IN);
                }
            }
        });


        downArrow.setOnClickListener(v -> {
            chatsRecyclerView.scrollToPosition(0);
            downArrowIcon.setColorFilter(ContextCompat.getColor(this, R.color.Gray), PorterDuff.Mode.SRC_IN);
        });



        backButton.setOnClickListener(v -> {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, 0);
//            fragmentTransaction.replace(R.id.frameLayout, new ShowChatsFragment(), "showChatsFragment").commit();

            super.onBackPressed();
        });



        profileInfo.setOnClickListener(v -> {

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

            if (!firstTime) {
                downArrowIcon.setColorFilter(ContextCompat.getColor(this, R.color.LimeGreen), PorterDuff.Mode.SRC_IN);
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
                    Toast.makeText(this, "There was an error sending your message", Toast.LENGTH_SHORT).show();
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

                if (status.equals("true")) {
                    isOnline = true;
                    infoLabel.setText("Online");
                } else {
                    isOnline = false;
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        if (!isOnline) infoLabel.setText(lastSeen(lastOnline));
                    }, 2000);
                }

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
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);
                int imageWidth = options.outWidth;
                int imageHeight = options.outHeight;

                Log.d("Bitmap Size", "Width: " + imageWidth + ", Height: " + imageHeight);

                if (imageWidth > MAX_WIDTH || imageHeight > MAX_HEIGHT) {
                    Toast.makeText(this, "Image size is too large", Toast.LENGTH_SHORT).show();
                } else {
                    SendImageDialog sendImageDialog = SendImageDialog.newInstance(imageUri, user);
                    sendImageDialog.show(getSupportFragmentManager(), "sendImageDialog");
                }
            } catch (Exception e) {
                Log.d(TAG, "error: " + e);
            }
        }
    }

    private String loadFile() {
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
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


    private void isOffline() {
        Timestamp timestamp = Timestamp.now();

        Map<String, Object> data = new HashMap<>();
        data.put("isOnline", "false");
        data.put("lastOnline", timestamp);

        DocumentReference docRef = db.collection("users").document(Constants.currentUserName);

        docRef.update(data)
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }


    private void isOnline() {
        Map<String, Object> data = new HashMap<>();
        data.put("isOnline", "true");

        DocumentReference docRef = db.collection("users").document(Constants.currentUserName);

        docRef.update(data)
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

    }


    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "asdf onPause: 2, appClosed: " + Constants.appClosed);

        isOffline();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "asdf onResume: 2, appClosed: " + Constants.appClosed);

        isOnline();
    }


    @Override
    public void onBackPressed() {
        List<Message> deleteList = chatsAdapter.getSelectionList();
        if (deleteList.size() == 0) {
            super.onBackPressed();
        } else {
            chatsAdapter.closeSelectionList();
        }
    }
}