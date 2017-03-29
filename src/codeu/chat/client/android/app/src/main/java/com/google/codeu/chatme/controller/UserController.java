package com.google.codeu.chatme.controller;

import android.util.Log;

import com.google.codeu.chatme.model.User;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

/**
 * Created by Yash on 3/29/2017.
 */

public class UserController {

    private static final String TAG = UserController.class.getName();

    /**
     * Saves a new user to Firebase real-time database
     *
     * @param id   id of {@link User}
     * @param name display name of {@link User}
     */
    public static void addUser(final String id, String name) {
        User newUser = new User(name, System.currentTimeMillis());

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.child("users").child(id).setValue(newUser, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.w(TAG, "addUser:failure " + databaseError.getMessage());
                } else {
                    Log.i(TAG, "addUser:success " + id);
                }
            }
        });
    }

}
