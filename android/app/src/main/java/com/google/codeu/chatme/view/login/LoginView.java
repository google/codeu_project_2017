package com.google.codeu.chatme.view.login;

import com.google.codeu.chatme.view.tabs.TabsActivity;

/**
 * Following MVP design pattern, this interface provides functions
 * which are implemented in {@link LoginActivity}
 */
public interface LoginView {

    /**
     * Launches {@link TabsActivity}
     * usually on successful sign up or sign in
     */
    void openTabsActivity();

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

    /**
     * Displays an error for {@link LoginActivity#etEmail} field
     *
     * @param err_et_email resource id of email field error message
     */
    void setEmailFieldError(int err_et_email);

    /**
     * Displays an error for {@link LoginActivity#etPassword} field
     *
     * @param err_et_password resource id of password field error message
     */
    void setPasswordFieldError(int err_et_password);
}
