package com.ahmetazizov.androidchatapp.models;

import java.io.Serializable;

public class Contact extends User implements Serializable {

    private String chatReference;
    private String Uid;

    public Contact() {}

    public Contact(String username, String email, String imageURL) {
        super(username, email, imageURL);
    }


    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getChatReference() {
        return chatReference;
    }

    public void setChatReference(String chatReference) {
        this.chatReference = chatReference;
    }
}
