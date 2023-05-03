package com.ahmetazizov.androidchatapp;

import com.ahmetazizov.androidchatapp.models.FavoriteMessage;
import com.ahmetazizov.androidchatapp.models.Message;
import com.ahmetazizov.androidchatapp.models.User;

import java.util.ArrayList;

public class Constants {

    public static String currentUser;

    public static ArrayList<User> users = new ArrayList<>();

    public static ArrayList<User> contacts = new ArrayList<>();

    public static ArrayList<FavoriteMessage> favorites = new ArrayList<>();
}
