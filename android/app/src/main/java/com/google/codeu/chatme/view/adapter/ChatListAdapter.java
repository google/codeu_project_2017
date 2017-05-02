package com.google.codeu.chatme.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.codeu.chatme.R;
import com.google.codeu.chatme.model.Conversation;
import com.google.codeu.chatme.presenter.ChatActivityPresenter;
import com.google.codeu.chatme.view.MessagesActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link android.support.v7.widget.RecyclerView.Adapter} to bind the list of conversations
 * data to the recyclerview in {@link com.google.codeu.chatme.view.tabs.ChatsFragment}
 *
 * @see ChatListAdapterView for documentation on interface methods
 */
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder>
        implements ChatListAdapterView {

    public static final String CONV_ID_EXTRA = "CONV_ID_EXTRA";

    /**
     * List of conversations
     */
    private List<Conversation> conversations = new ArrayList<>();

    private ChatActivityPresenter presenter;

    public ChatListAdapter() {
        this.presenter = new ChatActivityPresenter(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Conversation conversation = conversations.get(position);
        holder.tvSender.setText(conversation.getOwner());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMessagesActivity(view.getContext(), conversation.getId());
            }
        });
    }

    /**
     * Launches {@link MessagesActivity} for the specific conversation
     *
     * @param context        context to create a startActivity intent
     * @param conversationId id of conversation to display messages of
     */
    private void openMessagesActivity(Context context, String conversationId) {
        Intent mIntent = new Intent(context, MessagesActivity.class);
        mIntent.putExtra(CONV_ID_EXTRA, conversationId);
        context.startActivity(mIntent);
    }

    /**
     * @return number of conversations
     */
    @Override
    public int getItemCount() {
        return conversations.size();
    }

    /**
     * Loads conversations of the current user from Firebase database
     */
    public void loadConversations() {
        this.presenter.loadConversations();
    }

    public void setChatList(List<Conversation> conversations) {
        this.conversations = conversations;
        notifyDataSetChanged();
    }

    /**
     * A {@link android.support.v7.widget.RecyclerView.ViewHolder} class to encapsulate
     * various views of a conversation list item
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSender;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSender = (TextView) itemView.findViewById(R.id.tvSender);
        }
    }
}
