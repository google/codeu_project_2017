package com.google.codeu.chatme;

import android.os.Bundle;
import android.app.Activity;

public class CreateChatActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
