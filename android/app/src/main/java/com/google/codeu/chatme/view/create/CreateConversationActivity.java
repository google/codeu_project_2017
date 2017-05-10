package com.google.codeu.chatme.view.create;

import android.os.Bundle;
import android.view.View;
import android.support.v7.app.AppCompatActivity;

import com.google.codeu.chatme.R;

public class CreateConversationActivity extends AppCompatActivity
        implements CreateConversationView, View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_create_conversation);
    }


    @Override
    public void onClick(View view) {

    }
}
