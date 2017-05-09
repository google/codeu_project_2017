package com.google.codeu.chatme.presenter;

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
}
