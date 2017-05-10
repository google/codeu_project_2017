package com.google.codeu.chatme.view.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.codeu.chatme.R;
import com.google.codeu.chatme.presenter.LoginActivityPresenter;
import com.google.codeu.chatme.view.tabs.TabsActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * @see LoginView for documentation of interface methods
 */
public class LoginActivity extends AppCompatActivity implements LoginView, View.OnClickListener {

    private static final String TAG = LoginActivity.class.getName();

    /**
     * Progress loader
     */
    private ProgressDialog mProgressDialog;

    private LoginActivityPresenter presenter;

    private Button btnLogin;
    private EditText etPassword;
    private EditText etEmail;
    private Button btnCreateAcnt;

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
        presenter.postConstruct();

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnCreateAcnt = (Button) findViewById(R.id.btnCreateAcnt);
        btnLogin.setOnClickListener(this);
        btnCreateAcnt.setOnClickListener(this);
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
    public void openTabsActivity() {
        Intent mIntent = new Intent(LoginActivity.this, TabsActivity.class);
        startActivity(mIntent);
        finish();
    }

    public void showProgressDialog(int messsage) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(messsage));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setEmailFieldError(int err_et_email) {
        etEmail.setError(getString(err_et_email));
    }

    @Override
    public void setPasswordFieldError(int err_et_password) {
        etPassword.setError(getString(err_et_password));
    }

    /**
     * Handles click events on views implementing {@link android.view.View.OnClickListener}
     *
     * @param view clicked view
     */
    @Override
    public void onClick(View view) {

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        switch (view.getId()) {

            // login button clicked
            case R.id.btnLogin:
                presenter.signIn(email, password);
                break;

            // create account button clicked
            case R.id.btnCreateAcnt:
                presenter.signUp(email, password);
                break;
        }
    }
}
