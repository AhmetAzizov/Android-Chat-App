package com.ahmetazizov.androidchatapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.Serializable;
import java.util.ArrayList;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.MyViewHolder> {

    public final static String TAG = "ChatsRecycler";
    static Context context;
    ArrayList<User> chats;

    public ContactsRecyclerViewAdapter(Context context, ArrayList<User> chats){
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public ContactsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chats_recyclerview_row, parent, false);
        return new ContactsRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsRecyclerViewAdapter.MyViewHolder holder, int position) {

        holder.chatName.setText(chats.get(position).getUsername());


//        Glide.with(context)
//                .load(chats.get(position).getImageURL())
//                .override(300, 300)
//                .centerCrop()
//                .into(holder.chatImage);

        Glide.with(context)
                .load(chats.get(position).getImageURL())
                .override(300, 300)
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Handle image loading failure
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Handle successful image loading

                        if (holder.getAdapterPosition() + 1 == getItemCount()){
                            loadingScreenGone();
                        }
                        return false;
                    }
                })
                .into(holder.chatImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToDataToFragment(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView chatName;
        ImageView chatImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            chatName = itemView.findViewById(R.id.txtChatName);
            chatImage = itemView.findViewById(R.id.chatImage);

        }
    }





    private void loadingScreenGone() {
        AlphaAnimation fadeAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeAnimation.setDuration(500); // set the duration of the animation in milliseconds

        fadeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended
                // Perform any actions you want to do after the animation ends
                ShowChats.cover.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated
            }
        });


        ShowChats.cover.startAnimation(fadeAnimation);
    }


    private void sendToDataToFragment(ContactsRecyclerViewAdapter.MyViewHolder holder) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Create a Bundle object and set the data you want to pass
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", (Serializable) chats.get(holder.getAdapterPosition()));

        // Create a new instance of the fragment and set the bundle
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(bundle);


        // Replace the current fragment with the new one
        fragmentTransaction.replace(R.id.frameLayout, chatFragment).commit();
    }
}
