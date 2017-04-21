package com.google.codeu.chatme.view.tabs;

import com.google.codeu.chatme.model.User;
import com.google.codeu.chatme.view.login.LoginActivity;

/**
 * Following MVP design pattern, this interface provides functions
 * which are implemented in {@link ProfileFragment}
 */
public interface ProfileView {

    /**
     * Launches {@link LoginActivity} usually on successful sign out
     */
    void openLoginActivity();

    /**
     * Creates a long toast message on the {@link ProfileFragment} frame
     *
     * @param messageId id of message to be toasted
     */
    void makeToast(int messageId);

    /**
     * Creates a long toast message on the {@link ProfileFragment} frame
     *
     * @param message message to be toasted
     */
    void makeToast(String message);

    void setUserProfile(User userData);
}
