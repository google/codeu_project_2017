package com.google.codeu.chatme.presenter;

import com.google.codeu.chatme.model.User;

/**
 * In accordance with Interactor Design Pattern, this interface provides function(s)
 * which can be used to access Firebase database for data related to conversations of
 * the current user
 */
public interface ConversationsInteractor {

    /**
     * Loads conversations of the current user from Firebase database
     */
    public void loadConversations();

    /**
     * Creates new 2-user conversation between current logged in user and a second
     * user of their choosing
     *
     * @param selectedUser
     */
    public void createConversation(User selectedUser);
}
