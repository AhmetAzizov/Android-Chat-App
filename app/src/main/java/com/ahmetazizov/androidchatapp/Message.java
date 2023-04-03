package com.ahmetazizov.androidchatapp;

import java.util.Date;

public class Message {

    public static final int LAYOUT_SENDER = 1;
    public static final int LAYOUT_RECEIVER = 2;

    String sender;
    String content;
    String time;
    Date exactTime;

    public Message() {
        // Default no-argument constructor
    }

    public Message(String sender, String content, String time, Date exactTime) {
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.exactTime = exactTime;
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

    public Date getExactTime() {
        return exactTime;
    }

    public void setExactTime(Date exactTime) {
        this.exactTime = exactTime;
    }
}
