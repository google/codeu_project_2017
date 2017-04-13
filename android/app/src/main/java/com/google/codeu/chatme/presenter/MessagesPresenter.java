package com.google.codeu.chatme.presenter;

import android.util.Log;

import com.google.codeu.chatme.model.Message;
import com.google.codeu.chatme.view.message.MessagesView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessagesPresenter implements MessagesInteractor {

    private static final String TAG = MessagesPresenter.class.getName();

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    private final MessagesView 2view;

    public MessagesPresenter(MessagesView view) {
        this.view = view;
    }

    public void loadMessages(String conversationId) {
        Query conversationsQuery = mRootRef.child("messages")
                .orderByChild("conversation").equalTo(conversationId);

        conversationsQuery.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Message> messages = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    message.setId(data.getKey());
                    messages.add(message);
                    Log.d(TAG, "loadMessages:onDataChange:messageId:" + message.getId());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "loadMessages:failure " + databaseError.getMessage());
            }
        });
    }
}