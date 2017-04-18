package com.google.codeu.chatme.view.adapter;

import com.google.codeu.chatme.model.Conversation;

import java.util.List;
import java.util.Map;

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
     * Resets the map from participant Ids to their display names in {@link ChatListAdapter}
     *
     * @param map map from participant Ids to names
     */
    void setIdsToNamesMap(Map<String, String> map);
}
