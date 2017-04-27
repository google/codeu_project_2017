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
import com.google.codeu.chatme.model.ConversationParticipantDetails;
import com.google.codeu.chatme.presenter.ChatActivityPresenter;
import com.google.codeu.chatme.utility.FirebaseUtil;
import com.google.codeu.chatme.view.message.MessagesActivity;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
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
    private final Context context;

    /**
     * List of conversations to display in the list
     */
    private List<Conversation> conversations = new ArrayList<>();

    private ChatActivityPresenter presenter;

    /**
     * A map from different conversation participants to their details such as full names and
     * profile picture download urls
     */
    private HashMap<String, ConversationParticipantDetails> participantDetailsMap = new HashMap<>();

    public ChatListAdapter(Context context) {
        this.presenter = new ChatActivityPresenter(this);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Conversation conversation = conversations.get(position);
        String participantId = getRecipientId(conversation.getParticipants());

        ConversationParticipantDetails pDetails = participantDetailsMap.get(participantId);
        if (pDetails != null) {
            holder.tvSender.setText(pDetails.getFullName());
            holder.setHolderPicture(pDetails.getPhotoUrl());
        } else {
            holder.setHolderPicture(null);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMessagesActivity(view.getContext(), conversation.getId());
            }
        });
    }

    /**
     * Note: This function would need to altered if and when "groups" feature is introduced
     *
     * @param participants list of participant Ids for a particular conversation
     * @return recipient id (in current user's scope)
     */
    private String getRecipientId(List<String> participants) {
        if (participants.size() > 1) {
            for (String id : participants) {
                if (!FirebaseUtil.getCurrentUserUid().equals(id)) {
                    return id;
                }
            }
        }
        return FirebaseUtil.getCurrentUserUid();    // should not be returned ideally
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

    @Override
    public void setChatList(List<Conversation> conversations) {
        this.conversations = conversations;
        notifyDataSetChanged();
    }

    @Override
    public void setParticipantDetailsMap(HashMap<String, ConversationParticipantDetails>
                                                 participantDetailsMap) {
        this.participantDetailsMap = participantDetailsMap;
        notifyDataSetChanged();
    }

    /**
     * A {@link android.support.v7.widget.RecyclerView.ViewHolder} class to encapsulate
     * various views of a conversation list item
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSender;
        private CircularImageView civPic;

        public ViewHolder(View itemView) {
            super(itemView);

            tvSender = (TextView) itemView.findViewById(R.id.tvSender);
            civPic = (CircularImageView) itemView.findViewById(R.id.civPic);
        }

        private void setHolderPicture(String picUrl) {
            if (picUrl != null && !picUrl.isEmpty()) {
                Picasso.with(context)
                        .load(picUrl)
                        .placeholder(R.drawable.placeholder_person)
                        .error(R.drawable.placeholder_person)
                        .fit()
                        .into(this.civPic);
            } else {
                Picasso.with(context)
                        .load(R.drawable.placeholder_person)
                        .placeholder(R.drawable.placeholder_person)
                        .into(this.civPic);
            }
        }
    }
}
