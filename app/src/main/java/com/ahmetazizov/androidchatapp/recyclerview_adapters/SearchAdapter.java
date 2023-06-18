package com.ahmetazizov.androidchatapp.recyclerview_adapters;

import android.content.Context;
import android.content.Intent;
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

import com.ahmetazizov.androidchatapp.ChatActivity;
import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.fragments.ChatFragment;
import com.ahmetazizov.androidchatapp.models.AppUser;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {



    public final static String TAG = "ChatsRecycler";
    static Context context;
    ArrayList<AppUser> searchResult;

    public SearchAdapter(Context context, ArrayList<AppUser> searchResult){
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


        holder.itemView.setOnClickListener(v -> {


            Intent favoritesIntent = new Intent(v.getContext(), ChatActivity.class);
//            favoritesIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", searchResult.get(holder.getAdapterPosition()));
            favoritesIntent.putExtras(bundle);
            v.getContext().startActivity(favoritesIntent);


//            for (AppUser user : Constants.contacts) {
//                if (searchResult.get(holder.getAdapterPosition()).getUsername().equals(user.getUsername())) {
//                    sendToDataToFragment(holder, position);
//                    return;
//                }
//            }
//
//            String newChatRef = Constants.currentUserName + "-" + searchResult.get(position).getUsername();
//            CollectionReference colRef = db.collection("chats");
//
//            Timestamp timestamp = Timestamp.now();
//            Map<String, Object> data = new HashMap<>();
//            data.put("time", timestamp);
//
//            // Create an empty document inside "chats" collection
//            colRef.document(newChatRef)
//                    .set(data, SetOptions.merge())
//                    .addOnSuccessListener(unused -> {
//                        Toast.makeText(context, "Successfully added a new contact!", Toast.LENGTH_SHORT).show();
//
//                        // This will edit the new contacts chat reference so that it can be used in the next fragment
//                        searchResult.get(position).setChatReference(newChatRef);
//
//                        // This method will navigate to the chat fragment and send a user object containing the users information
//                        sendToDataToFragment(holder, position);
//                    }).addOnFailureListener(e -> Toast.makeText(context, "There was an error: " + e.getMessage(), Toast.LENGTH_LONG).show());

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


    private void sendToDataToFragment(SearchAdapter.MyViewHolder holder, int position) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Create a Bundle object and set the data you want to pass
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", searchResult.get(position));

        // Create a new instance of the fragment and set the bundle
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(bundle);

        // Replace the current fragment with the new one
        fragmentTransaction.replace(R.id.frameLayout, chatFragment, "chatFragment").commit();
    }


}
