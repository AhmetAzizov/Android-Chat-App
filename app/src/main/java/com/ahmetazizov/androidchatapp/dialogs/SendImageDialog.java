package com.ahmetazizov.androidchatapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.fragments.ChatFragment;
import com.ahmetazizov.androidchatapp.models.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SendImageDialog  extends DialogFragment {

    private final String TAG = "sendImageDialog";


    public static SendImageDialog newInstance(Uri imageUri, User user) {

        SendImageDialog sendImageDialog = new SendImageDialog();

        Bundle args = new Bundle();
        args.putParcelable("imageUri", imageUri);
        args.putSerializable("user", user);

        sendImageDialog.setArguments(args);
        return sendImageDialog;
    }

    StorageReference storageRef;
    FirebaseFirestore db;
    Uri imageUri;
    User user;
    CardView cancelButton, sendButton;
    ImageView imageContainer;
    ProgressBar progressBar;

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
            user = (User) getArguments().getSerializable("user");
        }

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("chatImages");
        cancelButton = view.findViewById(R.id.cancelButton);
        sendButton = view.findViewById(R.id.sendButton);
        imageContainer = view.findViewById(R.id.imageContainer);
        progressBar = view.findViewById(R.id.progressBar);


        Glide.with(getContext())
                .load(imageUri)
                .override(500, 500)
                .centerCrop()
                .into(imageContainer);


        cancelButton.setOnClickListener(v -> {
            dismiss();
        });

        sendButton.setOnClickListener(v -> {
            if (imageUri != null) {
                StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." + Constants.getFileExtension(imageUri, requireContext()));

                fileRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {

                            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {



                                Timestamp timestamp = Timestamp.now();

                                Map<String, Object> messageData = new HashMap<>();
                                messageData.put("sender", Constants.currentUser);
                                messageData.put("messageType", "image");
                                messageData.put("url", uri);
                                messageData.put("exactTime", timestamp);


                                final CollectionReference chatRef = db.collection("chats").document(user.getChatReference()).collection("messages");

                                chatRef.add(messageData)
                                        .addOnCompleteListener(task -> {
                                            Handler handler = new Handler();
                                            handler.postDelayed(() -> {
                                                progressBar.setProgress(0);
                                                dismiss();
                                            }, 1000);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.w(TAG, "Error adding document", e);
                                            Toast.makeText(getContext(), "There was an error sending your message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });

                                final DocumentReference docRef = db.collection("chats").document(user.getChatReference());

                                Timestamp docTimestamp = Timestamp.now();

                                Map<String, Object> data = new HashMap<>();
                                data.put("time", docTimestamp);

                                docRef.set(data)
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                                        .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                            });
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                        .addOnProgressListener(snapshot -> {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        });
            } else {
                Toast.makeText(getContext(), "No File Selected", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
