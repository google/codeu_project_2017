package com.google.codeu.chatme.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.codeu.chatme.R;
import com.google.codeu.chatme.view.adapter.ChatListAdapter;

public class MessagesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Intent intent = getIntent();
        String conversationId = intent.getStringExtra(ChatListAdapter.CONV_ID_EXTRA);

        // TODO: retrieve conversation data
    }

}
