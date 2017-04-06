package com.google.codeu.chatme.view.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.codeu.chatme.R;
import com.google.codeu.chatme.presenter.LoginActivityPresenter;
import com.google.codeu.chatme.view.tabs.TabsActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private static final String TAG = LoginActivity.class.getName();

    /**
     * Progress loader
     */
    private ProgressDialog mProgressDialog;

    private LoginActivityPresenter presenter;

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

        presenter = new LoginActivityPresenter(this);
    }

    /**
     * Delegates presenter to add {@link com.google.firebase.auth.FirebaseAuth.AuthStateListener} to
     * {@link FirebaseAuth} which is the entry point to Firebase SDK
     */
    @Override
    protected void onStart() {
        super.onStart();
        presenter.setAuthStateListener();
    }

    /**
     * Delegates presenter to remove {@link com.google.firebase.auth.FirebaseAuth.AuthStateListener}
     * from {@link FirebaseAuth} if it exists
     */
    @Override
    protected void onStop() {
        super.onStop();
        presenter.removeAuthStateListener();
    }

    /**
     * Launches {@link TabsActivity}, usually on successful sign up or sign in
     */
    public void openChatActivity() {
        Intent mIntent = new Intent(LoginActivity.this, TabsActivity.class);
        startActivity(mIntent);
    }

    /**
     * Shows progress loader with the given message
     *
     * @param messsage resource Id of string message to display
     */
    public void showProgressDialog(int messsage) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(messsage));
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

    /**
     * Creates a long toast message on the {@link LoginActivity} frame
     *
     * @param message message to be toasted
     */
    public void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
