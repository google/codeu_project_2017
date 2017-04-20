package com.google.codeu.chatme.controller;

import android.util.Log;

import com.google.codeu.chatme.model.Conversation;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Functions defined in this class are being moved to presenter classes to follow MVP
 * design pattern
 *
 * @Deprecated This class will be removed as and when its functions find a place in our app
 */
@Deprecated
public class Controller {

    private static final String TAG = Controller.class.getName();

    private static DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    /**
     * Saves a new conversation to Firebase real-time database
     *
     * @param newConversation {@link Conversation} object to be added
     */
    public static void addConversation(Conversation newConversation) {

        // generates unique id for conversation to be saved
        final String newConversationId = mRootRef.child("conversations").push().getKey();

        mRootRef.child("conversations").child(newConversationId).setValue(newConversation,
                new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.w(TAG, "addConversation:failure " + databaseError.getMessage());
                        } else {
                            Log.i(TAG, "addConversation:success " + newConversationId);
                        }
                    }
                });
    }
}
