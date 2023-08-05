package com.ahmetazizov.androidchatapp.models;

import java.io.Serializable;

public class Contact extends User implements Serializable {

    private String chatReference;
    private String uid;

    public Contact() {}

    public Contact(String username, String email, String imageURL) {
        super(username, email, imageURL);
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getChatReference() {
        return chatReference;
    }

    public void setChatReference(String chatReference) {
        this.chatReference = chatReference;
    }
}
