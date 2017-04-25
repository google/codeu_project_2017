package com.google.codeu.chatme.presenter;

import android.net.Uri;

/**
 * This interface provides functions which primarily relate to login
 * and sign up functionality of this Firebase-powered chat application
 */
public interface ProfileInteractor {

    /**
     * Attempts to update an account with given account credentials
     *
     * @param fullName user Full Name
     * @param username user username
     * @param password user password
     */
    public void updateUser(String fullName, String username, String password);

    /**
     * Logs out current user
     */
    public void signOut();

    /**
     * Delete current user's account
     */
    public void deleteAccount();

    /**
     * Gets current users profile
     */
    public void getUserProfile();

    /**
     * Uploads profile picture to Firebase Storage and then updates Database
     * with the picture download url
     *
     * @param data {@link Uri} containing new profile picture
     */
    void uploadProfilePictureToStorage(Uri data);
}
