package com.google.codeu.chatme.view.tabs;

import com.google.codeu.chatme.view.login.LoginActivity;

/**
 * Following MVP design pattern, this interface provides functions
 * which are implemented in {@link ProfileFragment}
 */
public interface ProfileView {

    /**
     * Launches {@link LoginActivity}
     * usually on successful sign up or sign in
     */
    void openLoginActivity();

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
     * Creates a long toast message on the {@link ProfileFragment} frame
     *
     * @param message message to be toasted
     */
    void makeToast(String message);

    /**
     * Displays an error for {@link ProfileFragment#etFullName} field
     *
     * @param err_et_fullname resource id of email field error message
     */
    void setFullNameFieldError(int err_et_fullname);
    /**
     * Displays an error for {@link ProfileFragment#etUsername} field
     *
     * @param err_et_username resource id of email field error message
     */
    void setUsernameFieldError(int err_et_username);

    /**
     * Displays an error for {@link ProfileFragment#etPassword} field
     *
     * @param err_et_password resource id of password field error message
     */
    void setPasswordFieldError(int err_et_password);
}
