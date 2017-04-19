package com.google.codeu.chatme.view.adapter;

import com.google.codeu.chatme.model.Conversation;
import com.google.codeu.chatme.model.User;

import java.util.List;

/**
 * An interface to handle presenter-delegated actions in order to
 * update list of users in {@link UserListAdapter}
 */
public interface UserListAdapterView {
    /**
     * Resets the list of users in {@link UserListAdapter}
     *
     * @param users new list of conversations
     */
    public void setChatList(List<User> users);
}
