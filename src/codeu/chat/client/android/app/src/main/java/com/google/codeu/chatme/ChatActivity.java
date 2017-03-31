package com.google.codeu.chatme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.codeu.chatme.presenter.ChatActivityPresenter;
import com.google.codeu.chatme.view.adapter.ChatListAdapter;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvChatList;
    private ChatListAdapter chatListAdapter;

    private ChatActivityPresenter chatActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rvChatList = (RecyclerView) findViewById(R.id.rvChatList);
        rvChatList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        chatListAdapter = new ChatListAdapter(this);
        rvChatList.setAdapter(chatListAdapter);

        chatListAdapter.loadUserChats();
    }
}
