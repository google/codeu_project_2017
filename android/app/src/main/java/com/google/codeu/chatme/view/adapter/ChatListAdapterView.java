package com.google.codeu.chatme.view.adapter;

import com.google.codeu.chatme.model.Conversation;
import com.google.codeu.chatme.model.ConversationParticipantDetails;

import java.util.HashMap;
import java.util.List;

/**
 * An interface to handle presenter-delegated actions in order to
 * update list of conversations in {@link ChatListAdapter}
 */
public interface ChatListAdapterView {

    /**
     * Resets the list of conversations in {@link ChatListAdapter}
     *
     * @param conversations new list of conversations
     */
    void setChatList(List<Conversation> conversations);

    /**
     * Resets the map from participant ids to their details {@link ChatListAdapter}
     *
     * @param map map from participant ids to details
     */
    void setParticipantDetailsMap(HashMap<String, ConversationParticipantDetails> map);
}
