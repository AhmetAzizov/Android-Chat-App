package com.ahmetazizov.androidchatapp.recyclerview_adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.models.FavoriteImageMessage;
import com.ahmetazizov.androidchatapp.models.FavoriteTextMessage;
import com.ahmetazizov.androidchatapp.models.Message;
import com.ahmetazizov.androidchatapp.models.TextMessage;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter {

    private static final String TAG = "FavoritesAdapter";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    ArrayList<Message> list;

    public FavoritesAdapter(Context context, ArrayList<Message> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getItemViewType(int position) {
        if (!list.isEmpty()) {
            return (list.get(position).getMessageType().equals("text")) ? 1 : 2;
        } else {
            return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View textLayout = LayoutInflater.from(context).inflate(R.layout.favorites_text_row, parent, false);
                return new TextMessage(textLayout);
            case 2:
                View imageLayout = LayoutInflater.from(context).inflate(R.layout.favorites_image_row, parent, false);
                return new ImageMessage(imageLayout);
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        DocumentReference docRef = db.collection("users").document(list.get(position).getSender());
        int CASE;

        if (!list.isEmpty()) {
            CASE = (list.get(position).getMessageType().equals("text")) ? 1 : 2;
        } else {
            CASE = -1;
        }

        switch (CASE) {
            case 1:
                FavoriteTextMessage favoriteTextMessage = (FavoriteTextMessage) list.get(position);

                String sender = favoriteTextMessage.getSender();
                String messageContent = favoriteTextMessage.getContent();
                String time = favoriteTextMessage.getTime();

                ((TextMessage) holder).sender.setText(sender);
                ((TextMessage) holder).messageContent.setText(messageContent);
                ((TextMessage) holder).timeSent.setText(time);


                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            String imageURL = document.getString("imageURL");

                            Glide.with(context)
                                    .load(imageURL)
                                    .override(100, 100)
                                    .centerCrop()
                                    .into(((TextMessage) holder).senderImage);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
                break;

            case 2:
                FavoriteImageMessage favoriteImageMessage = (FavoriteImageMessage) list.get(position);

                String sender2 = favoriteImageMessage.getSender();
                String url = favoriteImageMessage.getUrl();
                String time2 = favoriteImageMessage.getTime();

                ((ImageMessage) holder).sender.setText(sender2);
                ((ImageMessage) holder).timeSent.setText(time2);

                Glide.with(context)
                        .load(url)
                        .override(500, 500)
                        .centerCrop()
                        .into((((ImageMessage) holder).imageContent));

                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            String imageURL = document.getString("imageURL");

                            Glide.with(context)
                                    .load(imageURL)
                                    .override(100, 100)
                                    .centerCrop()
                                    .into(((ImageMessage) holder).senderImage);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });

                break;

            default: break;
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TextMessage extends RecyclerView.ViewHolder {

        TextView sender, messageContent, timeSent;
        ImageView senderImage;

        public TextMessage(@NonNull View itemView) {
            super(itemView);

            sender = itemView.findViewById(R.id.sender);
            timeSent = itemView.findViewById(R.id.timeSent);
            messageContent = itemView.findViewById(R.id.messageContent);
            senderImage = itemView.findViewById(R.id.senderImage);
        }
    }

    public static class ImageMessage extends RecyclerView.ViewHolder {

        TextView sender, timeSent;
        ImageView senderImage, imageContent;

        public ImageMessage(@NonNull View itemView) {
            super(itemView);

            sender = itemView.findViewById(R.id.sender);
            timeSent = itemView.findViewById(R.id.timeSent);
            imageContent = itemView.findViewById(R.id.imageContent);
            senderImage = itemView.findViewById(R.id.senderImage);
        }
    }
}
