package com.ahmetazizov.androidchatapp.adapters;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmetazizov.androidchatapp.ChatActivity;
import com.ahmetazizov.androidchatapp.dialogs.ProfileDialog;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.fragments.ChatFragment;
import com.ahmetazizov.androidchatapp.models.Contact;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.MyViewHolder> {

    public final static String TAG = "ChatsRecycler";
    Context context;
    ArrayList<Contact> chats;
    CardView cover;
    Activity activity;


    public ContactsRecyclerViewAdapter(Context context, ArrayList<Contact> chats, CardView cover, Activity activity){
        this.context = context;
        this.chats = chats;
        this.cover = cover;
        this.activity = activity;
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

//                        if (holder.getAdapterPosition() + 1 == getItemCount()){
//                            loadingScreenGone();
//                        }
                        return false;
                    }
                })
                .into(holder.chatImage);

        holder.itemView.setOnClickListener(v -> {
//            sendToDataToFragment(holder);

            Intent favoritesIntent = new Intent(v.getContext(), ChatActivity.class);
//            favoritesIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", chats.get(holder.getAdapterPosition()));
            favoritesIntent.putExtras(bundle);
            v.getContext().startActivity(favoritesIntent);
        });


        holder.chatImage.setOnClickListener(v -> {
            ProfileDialog customDialog = ProfileDialog.newInstance(chats.get(holder.getAdapterPosition()));
            customDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "customDialog");
        });

        holder.chatImage.setTransitionName("chat_image" + position);
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
        cover.animate().alpha(0.0f).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                cover.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
    }


    private void sendToDataToFragment(ContactsRecyclerViewAdapter.MyViewHolder holder) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(0, R.anim.slide_out_from_left);

        // Create a Bundle object and set the data you want to pass
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", chats.get(holder.getAdapterPosition()));

        // Create a new instance of the fragment and set the bundle
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(bundle);


        // Replace the current fragment with the new one
        fragmentTransaction.replace(R.id.frameLayout, chatFragment, "chatFragment").commit();
    }
}
