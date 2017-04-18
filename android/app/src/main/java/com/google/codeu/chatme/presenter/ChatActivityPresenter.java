package com.google.codeu.chatme.presenter;

import android.util.Log;

import com.google.codeu.chatme.model.Conversation;
import com.google.codeu.chatme.utility.FirebaseUtil;
import com.google.codeu.chatme.utility.network.RetrofitBuilder;
import com.google.codeu.chatme.view.adapter.ChatListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Following MVP design pattern, this class encapsulates the functionality to
 * store and retrieve data related to current user's conversations from Firebase
 * database
 *
 * @see ChatActivityInteractor for documentation of interface methods
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

    public void loadConversations() {
        Query conversationsQuery = mRootRef.child("conversations").orderByChild("timeCreated");
        conversationsQuery.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Conversation> conversations = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Conversation conv = data.getValue(Conversation.class);
                    if (conv.getParticipants().contains(FirebaseUtil.getCurrentUserUid())) {
                        conv.setId(data.getKey());
                        conversations.add(conv);
                    }
                }

                Collections.reverse(conversations);
                Log.i(TAG, "loadConversations:retrieved " + conversations.size());

                // updates list of conversations (and the corresponding views) in adapter
                view.setChatList(conversations);

                setConversationNames(conversations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "loadConversations:failure " + databaseError.getMessage());
            }
        });
    }

    /**
     * @param conversations list of conversations
     * @return list of conversation participants ids for current user
     */
    private List<String> getParticipantsFromConversations(ArrayList<Conversation> conversations) {
        Set participants = new HashSet();
        for (Conversation conv : conversations) {
            participants.addAll(conv.getParticipants());
        }

        // removes self from set of participant Ids (saving API calls, one call at a time)
        participants.remove(FirebaseUtil.getCurrentUserUid());

        return new ArrayList<>(participants);
    }

    /**
     * Retrieves a hash map which maps user ids to their display names. Uses Retrofit
     * to make an API call to Firebase Functions (backend) by "posting" a list of user
     * ids. Then, updates view by resetting {@link ChatListAdapter#participantsIdsToNames}
     * <p>
     * Note: This function would need to altered if and when "groups" feature is introduced
     *
     * @param conversations list of conversations
     */
    private void setConversationNames(ArrayList<Conversation> conversations) {
        List<String> participants = getParticipantsFromConversations(conversations);
        if (participants.size() == 0) {
            Log.d(TAG, "setConversationNames: 0 conversations");
            return;
        }

        RetrofitBuilder.getService().getNamesFromIds(participants)
                .enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(Call<HashMap<String, String>> call,
                                           Response<HashMap<String, String>> response) {
                        Log.i(TAG, "setConversationNames:retrieved "
                                + String.valueOf(response.body().size()));

                        // updates conversation names
                        view.setIdsToNamesMap(response.body());
                    }

                    @Override
                    public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                        Log.e(TAG, "setConversationNames:failure " + t.getMessage());
                    }
                });
    }
}
