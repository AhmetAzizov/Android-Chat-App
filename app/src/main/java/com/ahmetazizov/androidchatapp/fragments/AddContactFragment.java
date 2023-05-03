package com.ahmetazizov.androidchatapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class AddContactFragment extends Fragment {

    public AddContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_contact, container, false);
    }

    FirebaseFirestore db;
    TextInputLayout addContactUsernameLayout;
    TextInputEditText addContactUsername;
    Button addContactButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        addContactUsernameLayout = view.findViewById(R.id.addContactUsernameLayout);
        addContactUsername = view.findViewById(R.id.addContactUsername);
        addContactButton = view.findViewById(R.id.addContactButton);




        addContactButton.setOnClickListener(v -> {
            String username = addContactUsername.getText().toString().trim();
            String checkedUsername = checkError(username);

            if (checkedUsername != null) {
                String newChatRef = Constants.currentUser + "-" + checkedUsername;
                CollectionReference colRef = db.collection("chats");

                Timestamp timestamp = Timestamp.now();
                Map<String, Object> data = new HashMap<>();
                data.put("time", timestamp);

                // Create an empty document inside "chats" collection
                colRef.document(newChatRef)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getContext(), "Successfully added a new contact!", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> Toast.makeText(getContext(), "There was an error: " + e.getMessage(), Toast.LENGTH_LONG).show());

            }
        });
    }


    private String checkError(String username) {
        boolean found = false;

        if(username.contains("-") || username.contains("%") || username.contains(" ")) {
            addContactUsernameLayout.setError("Username can not contain space or special characters!");
            return null;
        } else if (username.equalsIgnoreCase(Constants.currentUser)) {
            addContactUsernameLayout.setError("Username should not be same as the current user!");
            return null;
        } else if (username.isEmpty()) {
            addContactUsernameLayout.setError("User Name is Empty!");
            addContactUsernameLayout.requestFocus();
            return null;
        } else if (username.length() > 15) {
            addContactUsernameLayout.setError("Username length can't be more than 15!");
            return null;
        } else {
            addContactUsernameLayout.setError(null);
        }

        for (User user : Constants.contacts) {
            if (username.equalsIgnoreCase(user.getUsername())) {
                addContactUsernameLayout.setError("User already in your contacts!");
                return null;
            }
        }

        for (User user : Constants.users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                addContactUsernameLayout.setHelperText("correct!");
                addContactUsernameLayout.setError(null);
                found = true;
                return user.getUsername();
            }
        }

        addContactUsernameLayout.setError("No such user found!");
        addContactUsernameLayout.requestFocus();

        return null;
    }
}