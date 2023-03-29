package com.ahmetazizov.androidchatapp;

public class User {
    private String username;
    private String email;
    private String imageURL;

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
}
