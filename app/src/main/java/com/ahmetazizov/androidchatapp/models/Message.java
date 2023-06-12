package com.ahmetazizov.androidchatapp.models;

import com.google.firebase.Timestamp;

public abstract class Message {

    String id;
    String sender;
    String chatRef;
    String messageType;
    Timestamp exactTime;

    public Message() {}

    public Message(String id, String sender, String chatRef, String messageType, Timestamp exactTime) {
        this.id = id;
        this.sender = sender;
        this.chatRef = chatRef;
        this.messageType = messageType;
        this.exactTime = exactTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getChatRef() {
        return chatRef;
    }

    public void setChatRef(String chatRef) {
        this.chatRef = chatRef;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Timestamp getExactTime() {
        return exactTime;
    }

    public void setExactTime(Timestamp exactTime) {
        this.exactTime = exactTime;
    }
}
