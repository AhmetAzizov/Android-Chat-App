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
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    TextView loginDirect;
    TextInputLayout enterUsernameLayout, enterEmailLayout, enterPasswordLayout;
    TextInputEditText enterUsername, enterEmail, enterPassword;
    ImageView image;
    ProgressBar progressBar;
    private Uri imageUri;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageRef;
    CollectionReference dbUsersRef;

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            selectedUri -> {
                // handle the selected image URI here
                image.setImageURI(selectedUri);
            });

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectImageButton = view.findViewById(R.id.selectImageButton);
        uploadImageButton = view.findViewById(R.id.uploadImageButton);
        registerButton = view.findViewById(R.id.registerButton);
        loginDirect = view.findViewById(R.id.loginDirect);
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
//                galleryLauncher.launch("image/*");

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
                        .load("https://firebasestorage.googleapis.com/v0/b/android-chat-application-155b5.appspot.com/o/imageUploads%2F1680045792837.jpg?alt=media&token=fa02e7ce-ff9e-415c-a636-33288511fe0f")
                        .override(500, 500)
                        .into(image);

                Log.d(TAG, "URI: " + imageUri);

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = enterUsername.getText().toString().trim();
                String email = enterEmail.getText().toString().trim();
                String password = enterPassword.getText().toString().trim();


                if (checkErrors(username, email, password, imageUri)) {
                    // Checks if there is any error in the input
                    uploadFile(username, email, password);
                } else {
                    Log.d(TAG, "error");
                }

            }
        });

//        loginDirect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }


    private boolean checkErrors(String username, String email, String password, Uri uri) {
        if (username.isEmpty() || username == null) {
            enterUsernameLayout.setError("User Name is Empty!");
            enterUsernameLayout.requestFocus();
            return false;
        }
        else if(username.contains("-") || username.contains("%") || username.contains(" ")) {
            enterUsernameLayout.setError("Username can not contain space or special characters!");
        } else {
            enterUsernameLayout.setError(null);
        }




        if (email.isEmpty() || email == null) {
            enterEmailLayout.setError("Email is Empty!");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            enterEmailLayout.setError("Please provide valid email!");
        } else {
            enterEmailLayout.setError(null);
        }


        if (password.isEmpty() || password == null) {
            enterPasswordLayout.setError("Password is Empty!");
            return false;
        }
        else if(password.length() < 6) {
            enterPasswordLayout.setError("Password should be at least 6 characters!");
        } else {
            enterPasswordLayout.setError(null);
        }

        if (uri == null || uri.toString().isEmpty() || uri.toString() == "null") {
            Toast.makeText(getContext(), "Please select a profile image!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Successfully created user: " + user.getUsername(), Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.putExtra("username", user.getUsername());
                        startActivity(intent);
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


                            // When the complete the register the new user, we set their display name to the one chosen by the user
                            FirebaseUser authUser = mAuth.getCurrentUser();


                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(user.getUsername()).setPhotoUri(imageUri).build();


                            authUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@androidx.annotation.NonNull Task<Void> task) {

                                    // We call the addUser method to add the new added user's data to firestore
                                    addUser(user);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@androidx.annotation.NonNull Exception e) {
                                    Log.d(TAG, "Error: " + e);
                                    return;
                                }
                            });
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

//                            Picasso.get().load(imageUri).resize(500, 500).centerInside().into(image);


                            try {
                                Glide.with(getContext())
                                        .load(imageUri)
                                        .override(500, 500)
                                        .centerInside()
                                        .into(image);
                            } catch (Exception e) {
                                Log.d(TAG, "Exception: " + e.getMessage());
                            }


//                            image.setImageURI(imageUri);


                            Log.d(TAG, "URI: " + result);
                        }
                    }
                });
    }

    private void openFileChooser() {
        mGetContentLauncher.launch("image/*");
    }









//    private void openFileChooser(){
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent,PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
//                && data != null && data.getData() != null){
//
//            imageUri = data.getData();
//            image.setImageURI(imageUri);
//
//            Log.d(TAG, "uri: " + imageUri);
//
//
////            Glide.with(getContext())
////                                    .load(imageUri)
////                                    .override(500, 500)
////                                    .centerInside()
////                                    .into(image);
//
////                    Picasso.get()
////                        .load(imageUri)
////                        .resize(500, 500)
////                        .centerInside()
////                        .into(image);
//
//        }
//    }


    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy: ");
    }
}