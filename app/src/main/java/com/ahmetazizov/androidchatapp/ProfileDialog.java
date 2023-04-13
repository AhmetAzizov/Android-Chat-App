package com.ahmetazizov.androidchatapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ahmetazizov.androidchatapp.fragments.ChatFragment;
import com.ahmetazizov.androidchatapp.models.User;
import com.bumptech.glide.Glide;

import java.io.Serializable;

public class ProfileDialog extends Dialog {

    ImageView profileImage;
    ImageView chatButton;
    TextView username;


    public ProfileDialog(@NonNull Context context, User user) {
        super(context);
        setContentView(R.layout.profile_dialog);

        // Set the window background to transparent
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



        profileImage = findViewById(R.id.profileImg);
        chatButton = findViewById(R.id.chatButton);
        username = findViewById(R.id.username);


        Glide.with(context)
                .load(user.getImageURL())
                .override(300, 300)
                .centerCrop()
                .into(profileImage);


        username.setText(user.getUsername());

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Create a Bundle object and set the data you want to pass
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", (Serializable) user);

                // Create a new instance of the fragment and set the bundle
                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(bundle);

                // Replace the current fragment with the new one
                fragmentTransaction.replace(R.id.frameLayout, chatFragment).commit();

                dismiss();
            }
        });
    }

}
