package com.ahmetazizov.androidchatapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.MainActivity;
import com.ahmetazizov.androidchatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }



    public final static String TAG = "LoginFragment";
    TextInputLayout enterEmailLayout, enterPasswordLayout;
    TextInputEditText enterEmail, enterPassword;
    Button loginInButton;
    TextView registerDirect;
    private FirebaseAuth mAuth;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        enterEmailLayout = view.findViewById(R.id.enterEmailLayout);
        enterPasswordLayout = view.findViewById(R.id.enterPasswordLayout);
        enterEmail = view.findViewById(R.id.enterEmail);
        enterPassword = view.findViewById(R.id.enterPassword);
        loginInButton = view.findViewById(R.id.loginInButton);
        registerDirect = view.findViewById(R.id.registerDirect);
        mAuth = FirebaseAuth.getInstance();




        loginInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = enterEmail.getText().toString().trim();
                String password = enterPassword.getText().toString().trim();

                if (errorCheck(email, password)) {
                    signIn(email, password);
                }
            }
        });

        registerDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                fragmentTransaction.replace(R.id.AuthFrameLayout, new RegisterFragment(), "registerFragment").commit();

                ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
                viewPager.setCurrentItem(0);
            }
        });
    }



    private boolean errorCheck(String email, String password){
        if (email.isEmpty() || email == null) {
            enterEmailLayout.setError("Email is Empty!");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            enterEmailLayout.setError("Please provide valid email!");
            return false;
        } else {
            enterEmailLayout.setError(null);
        }


        if (password.isEmpty() || password == null) {
            enterPasswordLayout.setError("Password is Empty!");
            return false;
        }
        else if(password.length() < 6) {
            enterPasswordLayout.setError("Password should be at least 6 characters!");
            return false;
        } else {
            enterPasswordLayout.setError(null);
        }


        return true;
    }




    private void signIn (String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Constants.registerImageUri = null;

                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}