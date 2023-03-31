package com.ahmetazizov.androidchatapp;

public class Message {

    public static final int LAYOUT_SENDER = 1;
    public static final int LAYOUT_RECEIVER = 2;

    String sender;
    String content;
    String time;
    String exactTime;

    public Message() {
        // Default no-argument constructor
    }

    public Message(String sender, String content, String time, String exactTime) {
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

    public String getExactTime() {
        return exactTime;
    }

    public void setExactTime(String exactTime) {
        this.exactTime = exactTime;
    }
}
