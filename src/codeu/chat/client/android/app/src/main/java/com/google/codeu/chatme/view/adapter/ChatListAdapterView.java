package com.google.codeu.chatme.view.adapter;

import com.google.codeu.chatme.model.Conversation;

import java.util.ArrayList;

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
    public void setChatList(ArrayList<Conversation> conversations);

}
