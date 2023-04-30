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
import com.ahmetazizov.androidchatapp.models.FavoriteMessage;
import com.ahmetazizov.androidchatapp.models.Message;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter {

    private static final String TAG = "FavoritesAdapter";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    ArrayList<FavoriteMessage> list;

    public FavoritesAdapter(Context context, ArrayList<FavoriteMessage> list) {
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
                View imageLayout = LayoutInflater.from(context).inflate(R.layout.favorites_text_row, parent, false);
                return new ImageMessage(imageLayout);
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int CASE;

        if (!list.isEmpty()) {
            CASE = (list.get(position).getMessageType().equals("text")) ? 1 : 2;
        } else {
            CASE = -1;
        }

        switch (CASE) {
            case 1:
                DocumentReference docRef = db.collection("users").document(list.get(position).getSender());

                String sender = list.get(position).getSender();
                String messageContent = list.get(position).getContent();

                ((TextMessage) holder).sender.setText(sender);
                ((TextMessage) holder).messageContent.setText(messageContent);


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

            default: break;
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TextMessage extends RecyclerView.ViewHolder {

        TextView sender, messageContent;
        ImageView senderImage;

        public TextMessage(@NonNull View itemView) {
            super(itemView);

            sender = itemView.findViewById(R.id.sender);
            messageContent = itemView.findViewById(R.id.messageContent);
            senderImage = itemView.findViewById(R.id.senderImage);
        }
    }

    public static class ImageMessage extends RecyclerView.ViewHolder {

        public ImageMessage(@NonNull View itemView) {
            super(itemView);
        }
    }
}
