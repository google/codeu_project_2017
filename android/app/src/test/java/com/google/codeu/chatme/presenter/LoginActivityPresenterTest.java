package com.google.codeu.chatme.presenter;

import com.google.codeu.chatme.R;
import com.google.codeu.chatme.view.login.LoginView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.never;
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

    @Test
    public void showEmailFieldErrorIfEmptyOnSignIn() {
        presenter.signIn("", "password");
        verify(view).setEmailFieldError(R.string.err_et_email);
        verify(view, never()).setPasswordFieldError(R.string.err_et_password);
    }

    @Test
    public void showPasswordFieldErrorIfEmptyOnSignIn() {
        presenter.signIn("abc@xyz.com", "");
        verify(view).setPasswordFieldError(R.string.err_et_password);
    }

    @Test
    public void showEmailFieldErrorIfEmptyOnSignUp() {
        presenter.signUp("", "password");
        verify(view).setEmailFieldError(R.string.err_et_email);
        verify(view, never()).setPasswordFieldError(R.string.err_et_password);
    }

    @Test
    public void showPasswordFieldErrorIfEmptyOnSignUp() {
        presenter.signUp("abc@xyz.com", "");
        verify(view).setPasswordFieldError(R.string.err_et_password);
    }

    @Test
    public void validateInput() {
        boolean result = presenter.validateInput("", "");
        boolean expected = false;
        Assert.assertEquals(expected, result);

        result = presenter.validateInput("abc@xyz.com", "");
        Assert.assertEquals(expected, result);

        result = presenter.validateInput("", "password");
        Assert.assertEquals(expected, result);

        result = presenter.validateInput("abc@xyz.com", "password");
        expected = true;
        Assert.assertEquals(expected, result);
    }
}