package com.ahmetazizov.androidchatapp.models;

import com.google.firebase.Timestamp;

public class FavoriteImageMessage extends Message {

    private String selfId;
    String url;
    String time;

    public FavoriteImageMessage() {}

    public FavoriteImageMessage(String id, String sender, String url, String time, String chatRef, String messageType, Timestamp exactTime, String selfId) {
        super(id, sender, chatRef, messageType, exactTime);
        this.url = url;
        this.time = time;
        this.selfId = selfId;
    }

    public String getSelfId() {
        return selfId;
    }

    public void setSelfId(String selfId) {
        this.selfId = selfId;
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
