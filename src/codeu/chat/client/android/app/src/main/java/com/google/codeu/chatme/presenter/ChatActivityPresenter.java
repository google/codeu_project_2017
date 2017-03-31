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
 * Created by Yash on 3/30/2017.
 */

public class ChatActivityPresenter implements ChatActivityInteractor {

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    private final ChatListAdapter view;

    public ChatActivityPresenter(ChatListAdapter view) {
        this.view = view;
    }

    public void loadUserChats() {
        mRootRef.child("conversations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Conversation> conversations = new ArrayList<>();
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    conversations.add(data.getValue(Conversation.class));
                    Log.d("LOG_TAG", data.getValue(Conversation.class).getOwner());
                }
                view.setChatList(conversations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
