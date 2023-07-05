package com.ahmetazizov.androidchatapp.dialogs;

import android.content.Intent;
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

import com.ahmetazizov.androidchatapp.ChatActivity;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.fragments.ChatFragment;
import com.ahmetazizov.androidchatapp.models.AppUser;
import com.bumptech.glide.Glide;

import java.io.Serializable;

public class ProfileDialog extends DialogFragment {

    private final String TAG = "profileDialog";
    AppUser user;
    ImageView profileImage;
    ImageView chatButton;
    TextView username;

    public static ProfileDialog newInstance(AppUser user) {

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
            user = (AppUser) getArguments().getSerializable("user");
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

        chatButton.setOnClickListener(v -> {
            Intent favoritesIntent = new Intent(v.getContext(), ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", (Serializable) user);
            favoritesIntent.putExtras(bundle);
            v.getContext().startActivity(favoritesIntent);

            dismiss();
        });
    }


    @Override
    public void onResume() {
        super.onResume();
//        To set the dialog to full screen
//        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
