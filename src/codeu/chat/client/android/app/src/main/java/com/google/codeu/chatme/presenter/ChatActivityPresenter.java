package com.google.codeu.chatme.presenter;

import android.util.Log;

import com.google.codeu.chatme.model.Conversation;
import com.google.codeu.chatme.view.adapter.ChatListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Following MVP design pattern, this class encapsulates the functionality to
 * store and retrieve data related to current user's conversations from Firebase
 * database
 */
public class ChatActivityPresenter implements ChatActivityInteractor {

    private static final String TAG = ChatActivityPresenter.class.getName();

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    /**
     * {@link ChatListAdapter} reference to update list of conversations
     */
    private final ChatListAdapter view;

    /**
     * Constructor to accept a reference to a recycler view adapter to bind
     * conversation data to views
     *
     * @param view {@link ChatListAdapter} reference
     */
    public ChatActivityPresenter(ChatListAdapter view) {
        this.view = view;
    }

    /**
     * Loads conversations of the current user from Firebase
     */
    public void loadConversations() {
        mRootRef.child("conversations").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Conversation> conversations = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    conversations.add(data.getValue(Conversation.class));
                    Log.d(TAG, data.getValue(Conversation.class).getOwner());
                }

                // updates list of conversations (and the corresponding views) in adapter
                view.setChatList(conversations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "loadConversations:failure " + databaseError.getMessage());
            }
        });
    }
}
