package com.google.codeu.chatme.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.codeu.chatme.R;
import com.google.codeu.chatme.view.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: implement basic splash screen in this activity
        startActivity(new Intent(this, LoginActivity.class));
    }
}
