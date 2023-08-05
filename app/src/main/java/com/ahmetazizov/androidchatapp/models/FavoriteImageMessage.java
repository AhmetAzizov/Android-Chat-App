package com.ahmetazizov.androidchatapp.models;

import com.google.firebase.Timestamp;

public class FavoriteImageMessage extends Message {

    private String selfId;
    private String receiver;
    private String url;
    private String time;

    public FavoriteImageMessage() {}

    public FavoriteImageMessage(String id, String sender, String receiver, String url, String time, String chatRef, String messageType, Timestamp exactTime, String selfId) {
        super(id, sender, chatRef, messageType, exactTime);
        this.receiver = receiver;
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

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
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
