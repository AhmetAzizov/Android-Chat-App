package com.ahmetazizov.androidchatapp;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.ahmetazizov.androidchatapp.models.Contact;
import com.ahmetazizov.androidchatapp.models.Message;
import com.ahmetazizov.androidchatapp.models.Request;

import java.util.ArrayList;

public class Constants {

    public static String currentUserName;

    public static String currentUserUid;

    public static Contact currentUser;

    public static ArrayList<Contact> users = new ArrayList<>();

    public static ArrayList<Contact> contacts = new ArrayList<>();

    public static ArrayList<Message> favorites = new ArrayList<>();

    public static ArrayList<Request> requests = new ArrayList<>();

    public static boolean appClosed = true;

    public static Uri registerImageUri;



    // This method is used for getting the file extension of specified Uri
    public static String getFileExtension(Uri uri, Context context) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
