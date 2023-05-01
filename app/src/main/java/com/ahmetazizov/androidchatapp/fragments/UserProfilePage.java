package com.ahmetazizov.androidchatapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmetazizov.androidchatapp.AuthenticationActivity;
import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.models.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfilePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfilePage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserProfilePage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfilePage.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfilePage newInstance(String param1, String param2) {
        UserProfilePage fragment = new UserProfilePage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile_page, container, false);
    }















    public final static String TAG = "UserProfilePage";
    FirebaseFirestore db;
    ImageView profileImage;
    CardView profileImageContainer, returnButton;
    TextView username, userEmail;
    Button resetPassword, logOut;
    View topView;
    boolean imageResized = false;
    User currentUserData;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        profileImage = view.findViewById(R.id.profileImage);
        profileImageContainer = view.findViewById(R.id.profileImageContainer);
        username = view.findViewById(R.id.username);
        userEmail = view.findViewById(R.id.userEmail);
        resetPassword = view.findViewById(R.id.resetPassword);
        logOut = view.findViewById(R.id.logOut);
        returnButton = view.findViewById(R.id.returnButton);
        topView = view.findViewById(R.id.topView);

        DocumentReference currentUser = db.collection("users").document(Constants.currentUser);

        currentUser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // document data exists, extract data and fill into object

                    currentUserData = document.toObject(User.class);

                    Glide.with(getContext())
                            .load(currentUserData.getImageURL())
                            .override(1000, 1000)
                            .centerCrop()
                            .into(profileImage);

                    username.setText(currentUserData.getUsername());
                    userEmail.setText(currentUserData.getEmail());

                } else {
                    Log.d(TAG, "Error fetching user data");
                    Toast.makeText(getContext(), "Error fetching user data", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d(TAG, "Error fetching user data");
                Toast.makeText(getContext(), "Error fetching user data", Toast.LENGTH_LONG).show();
            }
        });







        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, new ShowChatsFragment(), "showChatsFragment").commit();
            }
        });




        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resizeImage();
            }
        });


        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(currentUserData.getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Password reset link has been sent to your email address", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Timestamp timestamp = Timestamp.now();

                Map<String, Object> data = new HashMap<>();
                data.put("isOnline", "false");
                data.put("lastOnline", timestamp);

                DocumentReference docRef = db.collection("users").document(Constants.currentUser);

                docRef.update(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getContext(), AuthenticationActivity.class);
                startActivity(intent);
            }
        });
    }




    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void resizeImage(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        ViewGroup.MarginLayoutParams imageLayoutParams = (ViewGroup.MarginLayoutParams) profileImageContainer.getLayoutParams();
        ViewGroup.MarginLayoutParams viewLayoutParams = (ViewGroup.MarginLayoutParams) topView.getLayoutParams();

        if (!imageResized) {

            viewLayoutParams.setMargins(0, dpToPx(50), 0, 0);
            imageLayoutParams.setMargins(20, 50, 20, 50);
            profileImageContainer.setRadius(50);

            // Set the width of the image view to match the screen width
            profileImage.getLayoutParams().width = screenWidth - 40;

            // Set the height of the image view to match the same value as the width
            profileImage.getLayoutParams().height = screenWidth - 40;


            // Request a layout to update the changes
            topView.setLayoutParams(viewLayoutParams);
            profileImageContainer.setLayoutParams(imageLayoutParams);
            profileImage.requestLayout();

            imageResized = true;
        } else {
            viewLayoutParams.setMargins(0, 0, 0, 0);
            imageLayoutParams.setMargins(0, dpToPx(50), 0, 0);
            profileImageContainer.setRadius(dpToPx(300));

            // Set the width of the image view to match the screen width
            profileImage.getLayoutParams().width = dpToPx(250);

            // Set the height of the image view to match the same value as the width
            profileImage.getLayoutParams().height = dpToPx(250);


            // Request a layout to update the changes
            topView.setLayoutParams(viewLayoutParams);
            profileImageContainer.setLayoutParams(imageLayoutParams);
            profileImage.requestLayout();

            imageResized = false;
        }
    }
}