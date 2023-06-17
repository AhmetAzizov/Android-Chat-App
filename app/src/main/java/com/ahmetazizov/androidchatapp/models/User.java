package com.ahmetazizov.androidchatapp.models;

import java.io.Serializable;

public abstract class User implements Serializable {
    private String username;
    private String email;
    private String imageURL;

    public User () {}

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
}
