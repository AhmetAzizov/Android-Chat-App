package com.ahmetazizov.androidchatapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String email;
    private String imageURL;
    private String chatReference;

    public User () {
        // Default empty constructor
    }

    public User(String username, String email, String imageURL) {
        this.username = username;
        this.email = email;
        this.imageURL = imageURL;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getChatReference() {
        return chatReference;
    }

    public void setChatReference(String chatReference) {
        this.chatReference = chatReference;
    }
}
