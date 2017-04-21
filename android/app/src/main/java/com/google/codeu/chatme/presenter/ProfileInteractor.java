package com.google.codeu.chatme.presenter;

import com.google.firebase.auth.FirebaseAuth;

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
     * Logs out curent user
     */
    public void signOut();

    /**
     * Gets current users profile
     */
    public void getUserProfile();

    /**
     * Adds {@link com.google.firebase.auth.FirebaseAuth.AuthStateListener} to
     * {@link FirebaseAuth} which is the entry point to Firebase SDK
     */
    public void setAuthStateListener();

    /**
     * Removes {@link com.google.firebase.auth.FirebaseAuth.AuthStateListener} from
     * {@link FirebaseAuth} if it exists
     */
    public void removeAuthStateListener();

}
