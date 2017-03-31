package com.google.codeu.chatme.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.codeu.chatme.LoginActivity;
import com.google.codeu.chatme.R;
import com.google.codeu.chatme.controller.Controller;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Following MVP design pattern, this class encapsulates the functionality to
 * perform user sign up and sign in using Firebase
 */
public class LoginActivityPresenter implements LoginActivityInteractor {

    private static final String TAG = LoginActivityPresenter.class.getName();

    private final LoginActivity view;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    /**
     * Sets up the presenter with a reference to the {@link LoginActivity}.
     * Additionally, adds {@link com.google.firebase.auth.FirebaseAuth.AuthStateListener}
     * to {@link FirebaseAuth} instance to detect changed in user authentication status
     *
     * @param view a reference to {@link LoginActivity}
     */
    public LoginActivityPresenter(final LoginActivity view) {
        this.view = view;

        this.mAuth = FirebaseAuth.getInstance();
        this.mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    view.openChatActivity();
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    /**
     * Attempts to create an account with given account credentials
     *
     * @param email    user email
     * @param password user password
     */
    @Override
    public void signUp(String email, String password) {
        view.showProgressDialog(view.getString(R.string.progress_sign_up));

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signUp:onComplete:" + task.isSuccessful());
                        view.hideProgressDialog();

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signUpWithEmail:failure", task.getException());
                            view.makeToast(task.getException().getMessage());
                        } else {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            Log.i(TAG, "signUp:success" + currentUser.getUid());

                            // saves new user to real-time database
                            Controller.addUser(currentUser.getUid(), currentUser.getDisplayName());
                            view.openChatActivity();
                        }
                    }
                });
    }

    /**
     * Attempts to log user in with given credentials
     *
     * @param email    user email
     * @param password user password
     */
    public void signIn(String email, String password) {
        view.showProgressDialog(view.getString(R.string.progress_sign_in));

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        view.hideProgressDialog();

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            view.makeToast(task.getException().getMessage());
                        } else {
                            Log.i(TAG, "signInwithEmail:success:"
                                    + mAuth.getCurrentUser().getUid());
                            view.hideProgressDialog();
                        }
                    }
                });
    }

    /**
     * Adds {@link com.google.firebase.auth.FirebaseAuth.AuthStateListener} to
     * {@link FirebaseAuth} which is the entry point to Firebase SDK
     */
    @Override
    public void setAuthStateListener() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Removes {@link com.google.firebase.auth.FirebaseAuth.AuthStateListener} from
     * {@link FirebaseAuth} if it exists
     */
    @Override
    public void removeAuthStateListener() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
