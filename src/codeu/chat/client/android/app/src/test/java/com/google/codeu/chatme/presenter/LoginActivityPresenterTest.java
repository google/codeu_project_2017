package com.google.codeu.chatme.presenter;

import com.google.codeu.chatme.R;
import com.google.codeu.chatme.view.login.LoginView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LoginActivityPresenterTest {

    @Mock
    private FirebaseDatabase firebaseDatabase;

    @Mock
    private FirebaseAuth firebaseAuth;

    @Mock
    private LoginView view;

    @InjectMocks
    private LoginActivityPresenter presenter;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void showEmailFieldErrorIfEmpty() {
        presenter.signIn("", "password");
        verify(view).setEmailFieldError(R.string.err_et_email);
    }

    @Test
    public void showPasswordFieldErrorIfEmpty() {
        presenter.signIn("abc@xyz.com", "");
        verify(view).setPasswordFieldError(R.string.err_et_password);
    }
}