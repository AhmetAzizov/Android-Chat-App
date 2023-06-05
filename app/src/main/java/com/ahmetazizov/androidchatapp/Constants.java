package com.ahmetazizov.androidchatapp;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.ahmetazizov.androidchatapp.models.Message;
import com.ahmetazizov.androidchatapp.models.User;

import java.util.ArrayList;

public class Constants {

    public static String currentUserName;

    public static User currentUser;

    public static ArrayList<User> users = new ArrayList<>();

    public static ArrayList<User> contacts = new ArrayList<>();

    public static ArrayList<Message> favorites = new ArrayList<>();


    // This method is used for getting the file extension of specified Uri
    public static String getFileExtension(Uri uri, Context context) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
