package com.google.codeu.chatme.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.codeu.chatme.ChatActivity;
import com.google.codeu.chatme.R;
import com.google.codeu.chatme.model.Conversation;
import com.google.codeu.chatme.presenter.ChatActivityPresenter;

import java.util.ArrayList;

/**
 * Created by Yash on 3/30/2017.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder>
        implements ChatListAdapterView {

    private ArrayList<Conversation> conversations = new ArrayList<>();

    private ChatActivityPresenter presenter;

    public ChatListAdapter(ChatActivity view) {
        this.presenter = new ChatActivityPresenter(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Conversation current = conversations.get(position);
        holder.tvSender.setText(current.getOwner());
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public void loadUserChats() {
        this.presenter.loadUserChats();
    }

    public void setChatList(ArrayList<Conversation> conversations) {
        this.conversations = conversations;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSender;

        public ViewHolder(View itemView) {
            super(itemView);

            tvSender = (TextView) itemView.findViewById(R.id.tvSender);
        }
    }
}
