package com.ahmetazizov.androidchatapp.recyclerview_adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.MainActivity;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.fragments.ChatFragment;
import com.ahmetazizov.androidchatapp.fragments.ShowChatsFragment;
import com.ahmetazizov.androidchatapp.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {



    public final static String TAG = "ChatsRecycler";
    static Context context;
    ArrayList<User> searchResult;

    public SearchAdapter(Context context, ArrayList<User> searchResult){
        this.context = context;
        this.searchResult = searchResult;
    }

    @NonNull
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.search_recyclerview_row, parent, false);
        return new SearchAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.MyViewHolder holder, int position) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        holder.searchContact.setText(searchResult.get(position).getUsername());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                for (User user : Constants.contacts) {
                    if (searchResult.get(holder.getAdapterPosition()).getUsername().equals(user.getUsername())) {
                        sendToDataToFragment(holder);
                        return;
                    }
                }

                String newChatRef = Constants.currentUser + "-" + searchResult.get(holder.getAdapterPosition()).getUsername();
                CollectionReference colRef = db.collection("chats");

                Timestamp timestamp = Timestamp.now();
                Map<String, Object> data = new HashMap<>();
                data.put("time", timestamp);

                // Create an empty document inside "chats" collection
                colRef.document(newChatRef)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Successfully added a new contact!", Toast.LENGTH_SHORT).show();

                                // This will edit the new contacts chat reference so that it can be used in the next fragment
                                searchResult.get(holder.getAdapterPosition()).setChatReference(newChatRef);

                                // This method will navigate to the chat fragment and send a user object containing the users information
                                sendToDataToFragment(holder);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@androidx.annotation.NonNull Exception e) {
                                Toast.makeText(context, "There was an error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResult.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView searchContact;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            searchContact = itemView.findViewById(R.id.searchContact);
        }
    }


    private void sendToDataToFragment(SearchAdapter.MyViewHolder holder) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Create a Bundle object and set the data you want to pass
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", searchResult.get(holder.getAdapterPosition()));

        // Create a new instance of the fragment and set the bundle
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(bundle);


        // Replace the current fragment with the new one
        fragmentTransaction.replace(R.id.frameLayout, chatFragment, "chatFragment").commit();
    }


}