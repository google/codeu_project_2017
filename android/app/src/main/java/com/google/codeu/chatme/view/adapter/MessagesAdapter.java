package com.google.codeu.chatme.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.codeu.chatme.R;
import com.google.codeu.chatme.model.Message;
import com.google.codeu.chatme.presenter.MessagesPresenter;

import java.util.ArrayList;
import java.util.List;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder>
        implements MessagesAdapterView {

    /**
     * List of messages
     */
    private List<Message> messages = new ArrayList<>();

    private MessagesPresenter presenter;

    public MessagesAdapter() {
        this.presenter = new MessagesPresenter(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.tvMessage.setText(message.getContent());
    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }

    public void loadConversations(String conversationId) {
        presenter.loadMessages(conversationId);
    }

    public void sendMessage(Message newMessage) {
        presenter.sendMessage(newMessage);
    }

    @Override
    public void setMessagesOnView(ArrayList<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    /**
     * A {@link android.support.v7.widget.RecyclerView.ViewHolder} class to encapsulate
     * various views of a message item
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
        }
    }
}
