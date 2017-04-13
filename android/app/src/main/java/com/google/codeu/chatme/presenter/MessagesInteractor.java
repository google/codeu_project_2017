package com.google.codeu.chatme.presenter;

import com.google.codeu.chatme.model.Message;

/**
 * In accordance with Interactor Design Pattern, this interface provides function(s)
 * used for messaging in a particular conversation
 */
public interface MessagesInteractor {

    /**
     * Retrieves all messages from the database for the specific conversation
     *
     * @param conversationId id of conversation to load messages of
     */
    void loadMessages(String conversationId);

    /**
     * Saves a new message to Firebase real-time database
     *
     * @param newMessage {@link Message} object to be added
     */
    void sendMessage(Message newMessage);
}