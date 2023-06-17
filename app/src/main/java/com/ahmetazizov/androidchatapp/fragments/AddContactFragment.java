package com.ahmetazizov.androidchatapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.models.AppUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private static final String TAG = "AddContactFragment";
    FirebaseFirestore db;
    TextInputLayout addContactUsernameLayout;
    TextInputEditText addContactUsername;
    Button addContactButton;
    ImageView backButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        addContactUsernameLayout = view.findViewById(R.id.addContactUsernameLayout);
        addContactUsername = view.findViewById(R.id.addContactUsername);
        addContactButton = view.findViewById(R.id.addContactButton);
        backButton = view.findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, 0);
            fragmentTransaction.replace(R.id.frameLayout, new ShowChatsFragment(), "showChatsFragment").commit();
        });

        addContactButton.setOnClickListener(v -> {
            String username = addContactUsername.getText().toString().trim();
            String checkedUsername = checkError(username);

            if (checkedUsername != null) {
                final CollectionReference colRef = db.collection("users").document(checkedUsername).collection("requests");

                Map<String, Object> data = new HashMap<>();
                AppUser currentUser = Constants.currentUser;
                Timestamp timestamp = Timestamp.now();

                data.put("username", currentUser.getUsername());
                data.put("email", currentUser.getEmail());
                data.put("imageURL", currentUser.getImageURL());
                data.put("requestTime", timestamp);


                colRef.document(Constants.currentUserName)
                        .set(data)
                        .addOnSuccessListener(success -> {
                            Toast.makeText(requireContext(), "Request sent successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(failure -> {
                            Toast.makeText(requireContext(), "There was an error sending request, please try again", Toast.LENGTH_SHORT).show();
                        });



//                Timestamp timestamp = Timestamp.now();
//                Map<String, Object> data = new HashMap<>();
//                data.put("time", timestamp);
//
//                // Create an empty document inside "chats" collection
//                colRef.document(newChatRef)
//                        .set(data, SetOptions.merge())
//                        .addOnSuccessListener(unused -> {
//                            Toast.makeText(getContext(), "Successfully added a new contact!", Toast.LENGTH_SHORT).show();
//                        }).addOnFailureListener(e -> Toast.makeText(getContext(), "There was an error: " + e.getMessage(), Toast.LENGTH_LONG).show());

            }
        });
    }


    private String checkError(String username) {

        if(username.contains("-") || username.contains("%") || username.contains(" ")) {
            addContactUsernameLayout.setError("Username can not contain space or special characters!");
            return null;
        } else if (username.equalsIgnoreCase(Constants.currentUserName)) {
            addContactUsernameLayout.setError("Username should not be same as the current user!");
            return null;
        } else if (username.isEmpty()) {
            addContactUsernameLayout.setError("AppUser Name is Empty!");
            addContactUsernameLayout.requestFocus();
            return null;
        } else if (username.length() > 15) {
            addContactUsernameLayout.setError("Username length can't be more than 15!");
            return null;
        } else {
            addContactUsernameLayout.setError(null);
        }

//        for (AppUser user : Constants.contacts) {
//            if (username.equalsIgnoreCase(user.getUsername())) {
//                addContactUsernameLayout.setError("AppUser already in your contacts!");
//                return null;
//            }
//        }

        for (AppUser user : Constants.users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                addContactUsernameLayout.setHelperText("correct!");
                addContactUsernameLayout.setError(null);
                return user.getUsername();
            }
        }

        addContactUsernameLayout.setError("No such user found!");
        addContactUsernameLayout.requestFocus();

        return null;
    }
}