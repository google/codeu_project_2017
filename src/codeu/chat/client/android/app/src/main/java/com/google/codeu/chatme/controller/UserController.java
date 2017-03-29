package com.google.codeu.chatme.controller;

import com.google.codeu.chatme.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

/**
 * Created by Yash on 3/29/2017.
 */

public class UserController {

    public static void addUser(String id, String name) {
        User newUser = new User(name, System.currentTimeMillis());

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.child("users").child(id).setValue(newUser);
    }

}
