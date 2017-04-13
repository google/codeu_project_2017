package com.google.codeu.chatme.view.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.codeu.chatme.R;
import com.google.codeu.chatme.presenter.MessagesPresenter;
import com.google.codeu.chatme.view.adapter.ChatListAdapter;

public class MessagesActivity extends Activity implements MessagesView {

    private MessagesPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Intent intent = getIntent();
        String conversationId = intent.getStringExtra(ChatListAdapter.CONV_ID_EXTRA);

        presenter = new MessagesPresenter(this);

        // TODO: retrieve conversation data
        presenter.loadMessages(conversationId);
    }

}
