package com.ahmetazizov.androidchatapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.fragments.ChatFragment;
import com.ahmetazizov.androidchatapp.models.User;
import com.bumptech.glide.Glide;

import java.io.Serializable;

public class ProfileDialog extends DialogFragment {

    private final String TAG = "profileDialog";

    User user;
    ImageView profileImage;
    ImageView chatButton;
    TextView username;

    public static ProfileDialog newInstance(User user) {

        ProfileDialog dialogFragment = new ProfileDialog();

        Bundle args = new Bundle();
        args.putSerializable("user", user);

        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.profile_dialog, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//        getDialog().setCancelable(false);
//        getDialog().setCanceledOnTouchOutside(false);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }

        profileImage = view.findViewById(R.id.profileImg);
        chatButton = view.findViewById(R.id.chatButton);
        username = view.findViewById(R.id.username);

        Glide.with(getContext())
                .load(user.getImageURL())
                .override(500, 500)
                .centerCrop()
                .into(profileImage);


        username.setText(user.getUsername());



        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
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


    @Override
    public void onResume() {
        super.onResume();
//        To set the dialog to full screen
//        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
