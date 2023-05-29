package com.ahmetazizov.androidchatapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ahmetazizov.androidchatapp.R;
import com.bumptech.glide.Glide;

public class ResizePhotoDialog extends DialogFragment {

    private static final String TAG = "resizePhotoDialog";


    public static ResizePhotoDialog newInstance(String imageUrl) {

        ResizePhotoDialog resizePhotoDialog = new ResizePhotoDialog();

        Bundle args = new Bundle();
        args.putString("imageUrl", imageUrl);

        resizePhotoDialog.setArguments(args);
        return resizePhotoDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.resize_photo_dialog, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return view;
    }


    ImageView imageContainer;
    String imageUrl;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            imageUrl = getArguments().getString("imageUrl");
        }

        imageContainer = view.findViewById(R.id.imageContainer);


        Glide.with(getContext())
                .load(imageUrl)
                .override(500, 500)
                .centerCrop()
                .into(imageContainer);

    }
}
