package com.google.codeu.chatme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.codeu.chatme.controller.Controller;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    /**
     * Progress loader
     */
    private ProgressDialog mProgressDialog;

    /**
     * Sets up {@link com.google.firebase.auth.FirebaseAuth.AuthStateListener} to
     * respond to a change in user's sign-in state
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    openChatActivity();
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    /**
     * Adds {@link com.google.firebase.auth.FirebaseAuth.AuthStateListener} to
     * {@link FirebaseAuth} which is the entry point to Firebase SDK
     */
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Removes {@link com.google.firebase.auth.FirebaseAuth.AuthStateListener} from
     * {@link FirebaseAuth} if it exists
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Attempts to create an account with given account credentials
     *
     * @param email    user email
     * @param password user password
     */
    private void signUp(String email, String password) {
        showProgressDialog(getString(R.string.progress_sign_up));

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signUpWithEmail:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signUpWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            Log.i(TAG, "signUpWithEmail:success" + currentUser.getUid());

                            // saves new user to real-time database
                            Controller.addUser(currentUser.getUid(), currentUser.getDisplayName());
                            openChatActivity();
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
    private void signIn(String email, String password) {
        showProgressDialog(getString(R.string.progress_sign_in));

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Log.i(TAG, "signInwithEmail:success:"
                                    + mAuth.getCurrentUser().getUid());
                            hideProgressDialog();
                        }
                    }
                });
    }

    /**
     * Launches {@link ChatActivity}, usually on successful sign up or sign in
     */
    private void openChatActivity() {
        Intent mIntent = new Intent(LoginActivity.this, ChatActivity.class);
        startActivity(mIntent);
    }

    /**
     * Shows progress loader with the given message
     *
     * @param messsage message to display
     */
    public void showProgressDialog(String messsage) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(messsage);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    /**
     * Hides progress loader
     */
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
