package com.ahmetazizov.androidchatapp.models;

import com.google.firebase.Timestamp;

public class FavoriteTextMessage extends Message {

    private String selfId;
    String receiver;
    String content;
    String time;

    public FavoriteTextMessage() {}

    public FavoriteTextMessage(String id, String sender, String receiver, String content, String time, String chatRef, String messageType, Timestamp exactTime, String selfId) {
        super(id, sender, chatRef, messageType, exactTime);
        this.receiver = receiver;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
