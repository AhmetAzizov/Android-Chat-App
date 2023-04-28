package com.ahmetazizov.androidchatapp.models;

import java.util.Date;

public class Message {

//    public static final int LAYOUT_SENDER = 1;
//    public static final int LAYOUT_RECEIVER = 2;

    String id;
    String sender;
    String content;
    String time;
    String chatRef;
    String messageType;
    Date exactTime;

    public Message() {
        // Default no-argument constructor
    }

    public Message(String sender, String content, String time, String messageType,Date exactTime) {
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.messageType = messageType;
        this.exactTime = exactTime;
    }

    public Message(String id, String sender, String content, String time, String chatRef, String messageType, Date exactTime) {
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

    public Date getExactTime() {
        return exactTime;
    }

    public void setExactTime(Date exactTime) {
        this.exactTime = exactTime;
    }
}
