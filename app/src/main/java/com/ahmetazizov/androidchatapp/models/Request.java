package com.ahmetazizov.androidchatapp.models;

import java.security.Timestamp;

public class Request extends User{

    private Timestamp exactTime;

    public Request() {}

    public Request(String username, String email, String imageURL, Timestamp exactTime) {
        super(username, email, imageURL);
        this.exactTime = exactTime;
    }

    public Timestamp getExactTime() {
        return exactTime;
    }

    public void setExactTime(Timestamp exactTime) {
        this.exactTime = exactTime;
    }
}
