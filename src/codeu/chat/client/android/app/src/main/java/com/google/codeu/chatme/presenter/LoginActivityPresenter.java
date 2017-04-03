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
 */
public class LoginActivityPresenter implements LoginActivityInteractor {

    private static final String TAG = LoginActivityPresenter.class.getName();

    private final LoginView view;

    private static final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    /**
     * Sets up the presenter with a reference to the {@link LoginActivity}.
     * Additionally, adds {@link com.google.firebase.auth.FirebaseAuth.AuthStateListener}
     * to {@link FirebaseAuth} instance to detect changed in user authentication status
     *
     * @param view a reference to {@link LoginActivity}
     */
    public LoginActivityPresenter(final LoginView view) {
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
                            addUser(currentUser.getUid(), currentUser.getDisplayName());
                            view.openChatActivity();
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

    /**
     * Attempts to log user in with given credentials
     *
     * @param email    user email
     * @param password user password
     */
    public void signIn(String email, String password) {
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
