package com.google.codeu.chatme.presenter;

import android.util.Log;

import com.google.codeu.chatme.model.Conversation;
import com.google.codeu.chatme.model.User;
import com.google.codeu.chatme.view.adapter.ConversationListAdapter;
import com.google.codeu.chatme.view.adapter.UserListAdapter;
import com.google.codeu.chatme.view.adapter.UserListAdapterView;
import com.google.codeu.chatme.view.create.CreateConversationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CreateConversationPresenter implements CreateConversationInteractor {

    private static final String TAG = ConversationsPresenter.class.getName();

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    /**
     * {@link UserListAdapter} reference to update list of conversations
     */
    private final UserListAdapter view;
    /**
     * Constructor to accept a reference to a recycler view adapter to bind
     * conversation data to views
     *
     * @param view {@link UserListAdapter} reference
     */
    public CreateConversationPresenter(UserListAdapter view) {
       this.view = view;
    }

    /**
     * Creates new conversation and adds it to DB before returning the Conversation
     * object
     * @param selectedUserID
     * @return newConvo
     */
    public Conversation createConversation(String selectedUserID) {
            // create conversation and set current user as owner
        String currentUID = mAuth.getCurrentUser().getUid();
        Conversation newConvo = new Conversation(currentUID);
        newConvo.addParticipant(selectedUserID);

            // obtain key for new conversation and add to "conversations" DB
        String convoID = mRootRef.child("conversations").push().getKey();
        mRootRef.child("conversations").child(convoID).setValue(newConvo);
        Log.i(TAG, "Conversation between user "+currentUID+" (owner) and "+selectedUserID+" successfully " +
                "created. " );
        newConvo.setId(convoID);

        return newConvo;

    }
}
