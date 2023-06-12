package com.ahmetazizov.androidchatapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.graphics.BitmapFactory;

import android.os.Handler;
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

import com.ahmetazizov.androidchatapp.Constants;
import com.ahmetazizov.androidchatapp.MainActivity;
import com.ahmetazizov.androidchatapp.R;
import com.ahmetazizov.androidchatapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {
    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    private static final int MAX_WIDTH = 7000;
    private static final int MAX_HEIGHT = 7000;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public final static String TAG = "RegisterFragment";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button selectImageButton, registerButton;
    private ArrayList<String> users;
    TextView loginDirect;
    TextInputLayout enterUsernameLayout, enterEmailLayout, enterPasswordLayout;
    TextInputEditText enterUsername, enterEmail, enterPassword;
    ImageView image;
    CardView imageContainer;
    ProgressBar progressBar;
    Uri imageUri;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageRef;
    CollectionReference dbUsersRef;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectImageButton = view.findViewById(R.id.selectImageButton);
        registerButton = view.findViewById(R.id.registerButton);
        loginDirect = view.findViewById(R.id.loginDirect);
        progressBar = view.findViewById(R.id.progressBar);
        image = view.findViewById(R.id.image);
        imageContainer = view.findViewById(R.id.imageContainer);
        users = new ArrayList<>();

        if (savedInstanceState != null) {
            imageUri = savedInstanceState.getParcelable("uri");
            image.setImageURI(imageUri);
            Log.d(TAG, "something something");
        }

        if (Constants.registerImageUri != null) {
            image.setImageURI(Constants.registerImageUri);
        }

        enterUsernameLayout = view.findViewById(R.id.enterUserNameLayout);
        enterEmailLayout = view.findViewById(R.id.enterEmailLayout);
        enterPasswordLayout = view.findViewById(R.id.enterPasswordLayout);

        enterUsername = view.findViewById(R.id.enterUsername);
        enterEmail = view.findViewById(R.id.enterEmail);
        enterPassword = view.findViewById(R.id.enterPassword);


        dbUsersRef = db.collection("users");
        storageRef = FirebaseStorage.getInstance().getReference("userImages");

        usersListener();

//        setupGetContentLauncher();

        selectImageButton.setOnClickListener(v -> {
            // Method for choosing the image from device
            openFileChooser();
        });



        registerButton.setOnClickListener(v -> {
            String username = enterUsername.getText().toString().trim();
            String email = enterEmail.getText().toString().trim();
            String password = enterPassword.getText().toString().trim();


            if (checkErrors(username, email, password, imageUri)) {
                // Checks if there is any error in the input
                uploadFile(username, email, password);
            } else {
                Log.d(TAG, "error");
            }

        });

        loginDirect.setOnClickListener(v -> {
            FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.AuthFrameLayout, new LoginFragment(), "loginFragment").commit();

        });
    }


    private boolean checkErrors(String username, String email, String password, Uri uri) {
        if (username.isEmpty()) {
            enterUsernameLayout.setError("User Name is Empty!");
            enterUsernameLayout.requestFocus();
            return false;
        }
        else if(username.contains("-") || username.contains("%") || username.contains(" ")) {
            enterUsernameLayout.setError("Username can not contain space or special characters!");
            return false;
        } else if (username.length() > 15) {
            enterUsernameLayout.setError("Username length can't be more than 15!");
            return false;
        } else {
            enterUsernameLayout.setError(null);
        }

        if (email.isEmpty()) {
            enterEmailLayout.setError("Email is Empty!");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            enterEmailLayout.setError("Please provide valid email!");
            return false;
        } else {
            enterEmailLayout.setError(null);
        }


        if (password.isEmpty()) {
            enterPasswordLayout.setError("Password is Empty!");
            return false;
        }
        else if(password.length() < 6) {
            enterPasswordLayout.setError("Password should be at least 6 characters!");
            return false;
        } else {
            enterPasswordLayout.setError(null);
        }

        if (uri == null || uri.toString().isEmpty() || uri.toString().equals("null")) {
            Toast.makeText(getContext(), "Please select a profile image!", Toast.LENGTH_SHORT).show();
            return false;
        }

        for (String user : users) {
            if (user.equalsIgnoreCase(username)) {
                Toast.makeText(getContext(), "Username already taken, please choose another one", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }







//    // This method is used for getting the file extension of specified Uri
//    private String getFileExtension(Uri uri) {
//        ContentResolver cR = requireContext().getContentResolver();
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
//        return mime.getExtensionFromMimeType(cR.getType(uri));
//    }

    private void uploadFile(String username, String email, String password) {
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." + Constants.getFileExtension(imageUri, requireContext()));

            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {

                        // This code delays the progress bar resetting by 1 second
                        Handler handler = new Handler();
                        handler.postDelayed(() -> progressBar.setProgress(0), 1000);

                        // Here we get the download url from the specific database reference
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                            // We create a new user object
                            User user = new User(username, email, uri.toString());

                            registerUser(email, password, user);

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
    }




    private void addUser(User user) {

        dbUsersRef.document(user.getUsername())
                .set(user)
                .addOnSuccessListener(aVoid -> {

                    Map<String, Object> data = new HashMap<>();
                    data.put("isOnline", "true");

                    DocumentReference docRef = db.collection("users").document(user.getUsername());

                    docRef.update(data)
                            .addOnSuccessListener(aVoid1 -> {

                                Log.d(TAG, "Document created successfully!");
                                Toast.makeText(getContext(), "Successfully created user: " + user.getUsername(), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("username", user.getUsername());
                                startActivity(intent);

                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "Error writing document", e);
                                Toast.makeText(getContext(), "Error Creating user",Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating user: " + e);
                    Toast.makeText(getContext(), "Error Creating user",Toast.LENGTH_SHORT).show();
                });
    }



    private void registerUser(String email, String password, User user) {


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");


                        // When the complete the register the new user, we set their display name to the one chosen by the user
                        FirebaseUser authUser = mAuth.getCurrentUser();


                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(user.getUsername()).setPhotoUri(imageUri).build();


                        authUser.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {

                            // We call the addUser method to add the new added user's data to firestore
                            addUser(user);

                        }).addOnFailureListener(e -> {
                            Log.d(TAG, "Error: " + e);
                            return;
                        });
                    }
                }).addOnFailureListener(e -> {
                    Log.w(TAG, e.getMessage());
                    Toast.makeText(getContext(), e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
        }



        private void usersListener() {
            final CollectionReference usersRef = db.collection("users");

            usersRef.addSnapshotListener((value, e) -> {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                users.clear();


                for (QueryDocumentSnapshot document : value) {

                    users.add(document.getId());

                }
            });
        }



//    private void setupGetContentLauncher() {
//
//
//        mGetContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
//                new ActivityResultCallback<Uri>() {
//                    @Override
//                    public void onActivityResult(Uri result) {
//                        if (result != null) {
//
//                            imageUri = result;
//
////                            Picasso.get().load(imageUri).resize(500, 500).centerInside().into(image);
//
//
//                            try {
//                                Glide.with(getContext())
//                                        .load(imageUri)
//                                        .override(500, 500)
//                                        .centerInside()
//                                        .into(image);
//                            } catch (Exception e) {
//                                Log.d(TAG, "Exception: " + e.getMessage());
//                            }
//
//
////                            image.setImageURI(imageUri);
//
//
//                            Log.d(TAG, "URI: " + result);
//                        }
//                    }
//                });
//    }
//
//    private void openFileChooser() {
//        mGetContentLauncher.launch("image/*");
//    }









    private void openFileChooser(){
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//
////        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//          startActivityForResult(intent, PICK_IMAGE_REQUEST);

        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){

            imageUri = data.getData();

            Constants.registerImageUri = null;

            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);
                int imageWidth = options.outWidth;
                int imageHeight = options.outHeight;

                Log.d("Bitmap Size", "Width: " + imageWidth + ", Height: " + imageHeight);

                if (imageWidth > MAX_WIDTH || imageHeight > MAX_HEIGHT) {
                    Toast.makeText(getContext(), "Image size is too large", Toast.LENGTH_SHORT).show();
                } else {
                    image.setImageURI(imageUri);
                }
            } catch (Exception e) {
                Log.d(TAG, "error: " + e);
            }
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Constants.registerImageUri = imageUri;
        outState.putParcelable("uri", imageUri);
    }
}