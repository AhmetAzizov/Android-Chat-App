package com.ahmetazizov.androidchatapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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


    TextInputLayout addContactUsernameLayout;
    TextInputEditText addContactUsername;
    Button addContactButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        addContactUsernameLayout = view.findViewById(R.id.addContactUsernameLayout);
        addContactUsername = view.findViewById(R.id.addContactUsername);
        addContactButton = view.findViewById(R.id.addContactButton);




        addContactButton.setOnClickListener(v -> {
            String username = addContactUsername.getText().toString().trim();
            boolean found = false;

            if (username.isEmpty()) {
                addContactUsernameLayout.setError("User Name is Empty!");
                addContactUsernameLayout.requestFocus();
                return;
            } else if (username.length() > 15) {
                addContactUsernameLayout.setError("Username length can't be more than 15!");
                return;
            } else {
                addContactUsernameLayout.setError(null);
            }

            for (User user : Constants.users) {
                if (user.getUsername().equals(Constants.currentUser)) {
                    addContactUsernameLayout.setError("username should not be same as the users!");
                    found = true;
                    break;
                }

                if (user.getUsername().equalsIgnoreCase(username.toLowerCase())) {
                    addContactUsernameLayout.setHelperText("correct!");
                    addContactUsernameLayout.setError(null);
                    found = true;
                    break;
                }
            }

            if (!found) {
                addContactUsernameLayout.setError("No such user found!");
                addContactUsernameLayout.requestFocus();
            }
        });
    }
}