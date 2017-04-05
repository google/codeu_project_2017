package com.google.codeu.chatme.view.login;

/**
 * Following MVP design pattern, this interface provides functions
 * which are implemented in {@link LoginActivity}
 */
public interface LoginView {

    /**
     * Launches {@link com.google.codeu.chatme.view.ChatActivity}
     * usually on successful sign up or sign in
     */
    void openChatActivity();

    /**
     * Shows progress loader with the given message
     *
     * @param messsage resource Id of string message to display
     */
    void showProgressDialog(int messsage);

    /**
     * Hides progress loader
     */
    void hideProgressDialog();

    /**
     * Creates a long toast message on the {@link LoginActivity} frame
     *
     * @param message message to be toasted
     */
    void makeToast(String message);

}
