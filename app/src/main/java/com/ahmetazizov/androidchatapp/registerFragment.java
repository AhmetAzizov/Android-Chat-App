package com.ahmetazizov.androidchatapp;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link registerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class registerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public registerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment registerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static registerFragment newInstance(String param1, String param2) {
        registerFragment fragment = new registerFragment();
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
        return inflater.inflate(R.layout.fragment_register, container, false);
    }









    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public final static String TAG = "registerFragment";
    ActivityResultLauncher<String> mGetContentLauncher;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button selectImageButton, uploadImageButton, registerButton;
    TextInputLayout enterUsernameLayout, enterEmailLayout, enterPasswordLayout;
    TextInputEditText enterUsername, enterEmail, enterPassword;
    ShapeableImageView image;
    ProgressBar progressBar;
    private Uri imageUri;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageRef;
    CollectionReference dbUsersRef;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectImageButton = view.findViewById(R.id.selectImageButton);
        uploadImageButton = view.findViewById(R.id.uploadImageButton);
        registerButton = view.findViewById(R.id.registerButton);
        progressBar = view.findViewById(R.id.progressBar);
        image = view.findViewById(R.id.image);

        enterUsernameLayout = view.findViewById(R.id.enterUserNameLayout);
        enterEmailLayout = view.findViewById(R.id.enterEmailLayout);
        enterPasswordLayout = view.findViewById(R.id.enterPasswordLayout);

        enterUsername = view.findViewById(R.id.enterUsername);
        enterEmail = view.findViewById(R.id.enterEmail);
        enterPassword = view.findViewById(R.id.enterPassword);



        dbUsersRef = db.collection("users");
        storageRef = FirebaseStorage.getInstance().getReference("imageUploads");

        setupGetContentLauncher();

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Method for choosing the image from device
                openFileChooser();
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Method for uploading the image to firebase

//                Picasso.get()
//                        .load("https://firebasestorage.googleapis.com/v0/b/android-chat-application-155b5.appspot.com/o/imageUploads%2F1680045792837.jpg?alt=media&token=fa02e7ce-ff9e-415c-a636-33288511fe0f")
//                        .resize(1500, 1500)
//                        .centerInside()
//                        .into(image);

                Glide.with(getContext())
                        .load("data:image/jpeg;base64,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")
                        .override(1500, 1500)
                        .into(image);

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = enterUsername.getText().toString();
                String email = enterEmail.getText().toString();
                String password = enterPassword.getText().toString();


                if (checkErrors(username, email, password, imageUri)) {
                    // Checks if there is any error in the input
                    uploadFile(username, email, password);

                } else {
                    Log.d(TAG, "error");
                }

            }
        });
    }













    private boolean checkErrors(String username, String email, String password, Uri uri) {
        if (username.trim().isEmpty() || username == null)  {
            enterUsernameLayout.setError("User Name is Empty!");
            return false;
        } else {
            enterUsernameLayout.setError(null);
        }


        if (email.trim().isEmpty() || email == null) {
            enterEmailLayout.setError("Email is Empty!");
            return false;
        } else {
            enterEmailLayout.setError(null);
        }

        if (password.trim().isEmpty() || password == null) {
            enterPasswordLayout.setError("Password is Empty!");
            return false;
        } else {
            enterPasswordLayout.setError(null);
        }

        if (uri == null || uri.toString().isEmpty() || uri.toString() == "null") {
            return false;
        }

        return true;
    }








    // This method is used for getting the file extension of specified Uri
    private String getFileExtension(Uri uri) {
        ContentResolver cR = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(String username, String email, String password) {
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // This code delays the progress bar resetting by 1 second
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 1000);




                            // Here we get the download url from the specific database reference
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    // We create a new user object
                                    User user = new User(username, email, uri.toString());

                                    registerUser(email, password, user);

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(getContext(), "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }




    private void addUser(User user) {

        dbUsersRef.document(user.getUsername())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document created successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error creating user: " + e);
                        Toast.makeText(getContext(), "Error Creating user",Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void registerUser(String email, String password, User user) {


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(getContext(), "Successfully created user: " + user.getUsername(), Toast.LENGTH_SHORT).show();

                            addUser(user);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, e.getMessage());
                        Toast.makeText(getContext(), e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
        }



    private void setupGetContentLauncher() {


        mGetContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            imageUri = result;

                            Picasso.get().load(imageUri).resize(1500, 1500).centerInside().into(image);
                        }
                    }
                });
    }

    private void openFileChooser() {
        mGetContentLauncher.launch("image/*");
    }
}