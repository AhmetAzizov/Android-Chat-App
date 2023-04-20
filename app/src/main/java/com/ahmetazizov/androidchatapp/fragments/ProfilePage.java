package com.ahmetazizov.androidchatapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.models.User;
import com.bumptech.glide.Glide;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfilePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilePage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfilePage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfilePage.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfilePage newInstance(String param1, String param2) {
        ProfilePage fragment = new ProfilePage();
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
        return inflater.inflate(R.layout.fragment_profile_page, container, false);
    }









    public final static String TAG = "ProfilePage";
    boolean imageResized = false;
    User user;
    ImageView profileImage;
    TextView username, userEmail;
    CardView profileImageContainer;
    CardView returnButton;
    View topView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("user");

        profileImage = view.findViewById(R.id.profileImage);
        profileImageContainer = view.findViewById(R.id.profileImageContainer);
        username = view.findViewById(R.id.username);
        userEmail = view.findViewById(R.id.userEmail);
        returnButton = view.findViewById(R.id.returnButton);
        topView = view.findViewById(R.id.topView);


        Glide.with(getContext())
                .load(user.getImageURL())
                .override(1000, 1000)
                .centerCrop()
                .into(profileImage);


        username.setText(user.getUsername());
        userEmail.setText(user.getEmail());


        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.setCustomAnimations(0, R.anim.enter_from_top);

                // Create a Bundle object and set the data you want to pass
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", (Serializable) user);

                // Create a new instance of the fragment and set the bundle
                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(bundle);


                // Replace the current fragment with the new one
                fragmentTransaction.replace(R.id.frameLayout, chatFragment).commit();
            }
        });




        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        });
    }


    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}