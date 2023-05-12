package com.ahmetazizov.androidchatapp.models;

import com.google.firebase.Timestamp;

public class TextMessage{

//    public static final int LAYOUT_SENDER = 1;
//    public static final int LAYOUT_RECEIVER = 2;

    String id;
    String sender;
    String content;
    String time;
    String chatRef;
    String messageType;
    Timestamp exactTime;

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
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.chatRef = chatRef;
        this.messageType = messageType;
        this.exactTime = exactTime;
    }

    public String getId() { return id; }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getChatRef() {
        return chatRef;
    }

    public void setChatRef(String chatRef) {
        this.chatRef = chatRef;
    }

    public Timestamp getExactTime() {
        return exactTime;
    }

    public void setExactTime(Timestamp exactTime) {
        this.exactTime = exactTime;
    }
}
