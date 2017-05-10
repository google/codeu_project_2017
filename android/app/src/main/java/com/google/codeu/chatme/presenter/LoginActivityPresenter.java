package com.google.codeu.chatme.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.codeu.chatme.R;
import com.google.codeu.chatme.model.User;
import com.google.codeu.chatme.view.login.LoginActivity;
import com.google.codeu.chatme.view.login.LoginView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Following MVP design pattern, this class encapsulates the functionality to
 * perform user sign up and sign in using Firebase
 *
 * @see LoginActivityInteractor for documentation on interface methods
 */
public class LoginActivityPresenter implements LoginActivityInteractor {

    private static final String TAG = LoginActivityPresenter.class.getName();

    private final LoginView view;

    private DatabaseReference mRootRef;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    /**
     * Sets up the presenter with a reference to the {@link LoginActivity}.
     * Additionally, adds {@link com.google.firebase.auth.FirebaseAuth.AuthStateListener}
     * to {@link FirebaseAuth} instance to detect changed in user authentication status
     * Refer to {@link LoginActivityPresenter#postConstruct()}
     *
     * @param view a reference to {@link LoginActivity}
     */
    public LoginActivityPresenter(final LoginView view) {
        this.view = view;
    }

    @javax.annotation.PostConstruct
    public void postConstruct() {
        this.mRootRef = FirebaseDatabase.getInstance().getReference();

        this.mAuth = FirebaseAuth.getInstance();

        this.mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    view.openTabsActivity();
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void signUp(String email, String password) {
        boolean isValid = validateInput(email, password);
        if (!isValid) {
            return;
        }
        // Create username from email address
        int index = email.indexOf('@');
        final String username = email.substring(0, index);

        view.showProgressDialog(R.string.progress_sign_up);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signUp:onComplete:" + task.isSuccessful());
                        view.hideProgressDialog();

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signUp:failure", task.getException());
                            view.makeToast(task.getException().getMessage());
                        } else {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            Log.i(TAG, "signUp:success:" + currentUser.getUid());

                            // saves new user to real-time database
                            addUser(currentUser.getUid(), username);
                        }
                    }
                });
    }

    /**
     * Saves a new user to Firebase real-time database
     *
     * @param id   id of {@link User}
     * @param name display name of {@link User}
     */
    private void addUser(final String id, String name) {
        User newUser = new User(name);

        mRootRef.child("users").child(id).setValue(newUser, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.w(TAG, "addUser:failure " + databaseError.getMessage());
                } else {
                    Log.i(TAG, "addUser:success " + id);
                }
            }
        });
    }

    public void signIn(String email, String password) {
        boolean isValid = validateInput(email, password);
        if (!isValid) {
            return;
        }

        view.showProgressDialog(R.string.progress_sign_in);

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
                        }
                    }
                });
    }

    /**
     * Validates user email and password for login form
     *
     * @param email    email the user entered
     * @param password password the user enterd
     * @return true if the inputs are valid
     */
    public boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            view.setEmailFieldError(R.string.err_et_email);
            return false;
        }
        if (password.isEmpty()) {
            view.setPasswordFieldError(R.string.err_et_password);
            return false;
        }
        return true;
    }

    @Override
    public void setAuthStateListener() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void removeAuthStateListener() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
