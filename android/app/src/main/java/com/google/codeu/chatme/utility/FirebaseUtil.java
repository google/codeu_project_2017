package com.google.codeu.chatme.utility;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A utility class to hold Firebase related constants and functions for easy access
 */
public class FirebaseUtil {

    /**
     * @return user id of the currently logged in user
     */
    public static String getCurrentUserUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * @return currently logged in user
     */
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
