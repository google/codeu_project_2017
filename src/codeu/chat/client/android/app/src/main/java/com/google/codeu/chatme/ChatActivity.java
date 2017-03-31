package com.google.codeu.chatme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.codeu.chatme.view.adapter.ChatListAdapter;

public class ChatActivity extends AppCompatActivity {

    /**
     * {@link RecyclerView} to hold the list of conversations for the current user
     */
    private RecyclerView rvChatList;

    /**
     * {@link android.support.v7.widget.RecyclerView.Adapter} to bind conversations
     * data to their corresponding views
     */
    private ChatListAdapter chatListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initializeUI();
    }

    /**
     * Sets up user interface by loading the list of conversations for the current
     * user in the recyclerview
     */
    private void initializeUI() {
        rvChatList = (RecyclerView) findViewById(R.id.rvChatList);
        rvChatList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        chatListAdapter = new ChatListAdapter();
        rvChatList.setAdapter(chatListAdapter);

        chatListAdapter.loadConversations();
    }
}
