package com.google.codeu.chatme.presenter;

import com.google.firebase.auth.FirebaseAuth;

/**
 * This interface provides functions which primarily relate to login
 * and sign up functionality of this Firebase-powered chat application
 */
public interface LoginActivityInteractor {

    /**
     * Attempts to create an account with given account credentials
     *
     * @param email    user email
     * @param password user password
     */
    public void signUp(String email, String password);

    /**
     * Attempts to log user in with given credentials
     *
     * @param email    user email
     * @param password user password
     */
    public void signIn(String email, String password);

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
