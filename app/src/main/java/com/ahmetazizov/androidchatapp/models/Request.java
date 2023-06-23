package com.ahmetazizov.androidchatapp.models;

import com.google.firebase.Timestamp;

public class Request extends User{

    private Timestamp requestTime;

    public Request() {}

    public Request(String username, String email, String imageURL, com.google.firebase.Timestamp exactTime) {
        super(username, email, imageURL);
        this.requestTime = exactTime;
    }

    public Timestamp getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Timestamp requestTime) {
        this.requestTime = requestTime;
    }
}
