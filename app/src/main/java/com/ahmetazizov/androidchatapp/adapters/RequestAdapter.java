package com.ahmetazizov.androidchatapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.models.Contact;
import com.ahmetazizov.androidchatapp.models.Request;
import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {

    public final static String TAG = "RequestAdapter";
    Context context;
    ArrayList<Request> requestList;
    FirebaseFirestore db;


    public RequestAdapter(Context context, ArrayList<Request> requestList) {
        this.context = context;
        this.requestList = requestList;
        db = FirebaseFirestore.getInstance();
    }



    @NonNull
    @Override
    public RequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.request_row, parent, false);
        return new RequestAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String requestSender = requestList.get(position).getUsername();

        Glide.with(context)
                .load(requestList.get(position).getImageURL())
                .override(300, 300)
                .centerCrop()
                .into(holder.profileImage);

        holder.username.setText(requestSender);

        Timestamp exactTime = requestList.get(position).getRequestTime();

        long timeMilli = exactTime.toDate().getTime();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String time = timeFormat.format(timeMilli);

        holder.timeSent.setText(time);


        holder.approveButton.setOnClickListener(v -> {

                final CollectionReference colRef = db.collection("chats");
                final DocumentReference deleteRef = db.collection("users").document(Constants.currentUserName).collection("requests").document(requestSender);

                for (Contact user : Constants.contacts) {
                    if (user.getUsername().equalsIgnoreCase(requestSender)) {
                        deleteRef.delete();
                        notifyDataSetChanged();
                        return;
                    }
                }

                String newChatRef = requestSender + '-' + Constants.currentUserName;

                Timestamp timestamp = Timestamp.now();
                Map<String, Object> data = new HashMap<>();
                data.put("time", timestamp);

                deleteRef.delete()
                        .addOnFailureListener(e -> Log.e(TAG, "Error"));

                // Create an empty document inside "chats" collection
                colRef.document(newChatRef)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(context, "Successfully added a new contact!", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> Toast.makeText(context, "There was an error: " + e.getMessage(), Toast.LENGTH_LONG).show());

                notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage, approveButton, denyButton;
        TextView username, timeSent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            approveButton = itemView.findViewById(R.id.approveButton);
            denyButton = itemView.findViewById(R.id.denyButton);
            username = itemView.findViewById(R.id.username);
            timeSent = itemView.findViewById(R.id.timeSent);
        }
    }
}
