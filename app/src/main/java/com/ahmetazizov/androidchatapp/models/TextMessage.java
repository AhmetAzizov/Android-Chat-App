package com.ahmetazizov.androidchatapp.models;

import com.google.firebase.Timestamp;

public class TextMessage extends Message{

    String content;
    String time;

    public TextMessage() {
        // Default no-argument constructor
    }

//    public TextMessage(String sender, String content, String time, String messageType, Timestamp exactTime) {
//        this.sender = sender;
//        this.content = content;
//        this.time = time;
//        this.messageType = messageType;
//        this.exactTime = exactTime;
//    }

    public TextMessage(String id, String sender, String content, String time, String chatRef, String messageType, Timestamp exactTime) {
        super(id, sender, chatRef, messageType, exactTime);
        this.time = time;
        this.content = content;
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
