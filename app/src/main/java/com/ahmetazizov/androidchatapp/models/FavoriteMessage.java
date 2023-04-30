package com.ahmetazizov.androidchatapp.models;

import com.ahmetazizov.androidchatapp.recyclerview_adapters.FavoritesAdapter;
import com.google.firebase.Timestamp;

public class FavoriteMessage extends Message{

    private String selfId;

    public FavoriteMessage() {}

    public FavoriteMessage(String id, String sender, String content, String time, String chatRef, String messageType, Timestamp exactTime, String selfId) {
        super(id, sender, content, time, chatRef, messageType, exactTime);
        this.selfId = selfId;
    }

    public String getSelfId() {
        return selfId;
    }

    public void setSelfId(String selfId) {
        this.selfId = selfId;
    }
}
