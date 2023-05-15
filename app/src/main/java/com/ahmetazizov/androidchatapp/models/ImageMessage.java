package com.ahmetazizov.androidchatapp.models;

import com.google.firebase.Timestamp;

public class ImageMessage extends Message{

    String url;
    String time;

    public ImageMessage() {} // Empty constructor

    public ImageMessage(String id, String sender, String chatRef, String messageType, Timestamp exactTime, String url, String time) {
        super(id, sender, chatRef, messageType, exactTime);
        this.url = url;
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
