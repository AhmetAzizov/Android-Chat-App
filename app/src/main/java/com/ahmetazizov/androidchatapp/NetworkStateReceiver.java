package com.ahmetazizov.androidchatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;



public class NetworkStateReceiver extends BroadcastReceiver {

    public static final String TAG = "NetworkStateReceiver";



    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Check network connectivity status
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        // Do something with the network connectivity status
        if (isConnected) {
            // Connected to the internet
            Map<String, Object> data = new HashMap<>();
            data.put("isOnline", "true");

            DocumentReference docRef = db.collection("users").document(Constants.currentUserName);

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
        } else {
            // Disconnected from the internet
            Log.d(TAG, "user disconnected");
        }
    }
}
