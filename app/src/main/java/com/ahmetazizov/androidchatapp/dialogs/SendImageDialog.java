package com.ahmetazizov.androidchatapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.fragments.ChatFragment;
import com.ahmetazizov.androidchatapp.models.User;
import com.bumptech.glide.Glide;

import java.io.Serializable;

public class SendImageDialog  extends DialogFragment {

    private final String TAG = "sendImageDialog";


    public static SendImageDialog newInstance(Uri imageUri) {

        SendImageDialog sendImageDialog = new SendImageDialog();

        Bundle args = new Bundle();
        args.putParcelable("imageUri", imageUri);

        sendImageDialog.setArguments(args);
        return sendImageDialog;
    }


    Uri imageUri;
    CardView cancelButton, sendButton;
    ImageView imageContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_image_dialog, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            imageUri = getArguments().getParcelable("imageUri");
        }

        cancelButton = view.findViewById(R.id.cancelButton);
        sendButton = view.findViewById(R.id.sendButton);
        imageContainer = view.findViewById(R.id.imageContainer);


        Glide.with(getContext())
                .load(imageUri)
                .override(500, 500)
                .centerCrop()
                .into(imageContainer);


        cancelButton.setOnClickListener(v -> {
            dismiss();
        });
    }
}
