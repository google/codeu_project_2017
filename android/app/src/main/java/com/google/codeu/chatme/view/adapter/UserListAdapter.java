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
import com.google.codeu.chatme.model.User;
import com.google.codeu.chatme.presenter.ConversationsPresenter;
import com.google.codeu.chatme.presenter.CreateConversationPresenter;
import com.google.codeu.chatme.presenter.UserPresenter;
import com.google.codeu.chatme.view.message.MessagesActivity;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link android.support.v7.widget.RecyclerView.Adapter} to bind the list of users
 * to the recyclerview in {@link com.google.codeu.chatme.view.tabs.UsersFragment}
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder>
        implements UserListAdapterView {

    private List<User> users = new ArrayList<>();
    private CreateConversationPresenter createConvoPresenter;

    private UserPresenter presenter;
    public static final String CONV_ID_EXTRA = "CONV_ID_EXTRA";
    private final Context context;

    public UserListAdapter(Context context) {
        this.presenter = new UserPresenter(this);
        this.createConvoPresenter = new CreateConversationPresenter(this);
        this.context = context;
    }

    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new ViewHolder(v);
    }

    public void loadUsers() {
        this.presenter.loadUsers();
    }

    @Override
    public void onBindViewHolder(UserListAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.setUserID(user.getId());

        holder.setHolderPicture(user.getPhotoUrl());
        if (user.getFullName() != null) {
            holder.tvName.setText(user.getFullName());
        } else {
            holder.tvName.setText(user.getUsername());
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public void setUserList(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    /**
     * A {@link android.support.v7.widget.RecyclerView.ViewHolder} class to encapsulate
     * various views of a user list item
     */
    public class ViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        private TextView tvName;
        private CircularImageView civUserPic;
        private String userID;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            civUserPic = (CircularImageView) itemView.findViewById(R.id.civUserPic);
        }

        private void setUserID(String uid) {
            this.userID = uid;
        }

        private void setHolderPicture(String picUrl) {
            if (picUrl != null && !picUrl.isEmpty()) {
                Picasso.with(context)
                        .load(picUrl)
                        .placeholder(R.drawable.placeholder_person)
                        .error(R.drawable.placeholder_person)
                        .fit()
                        .into(civUserPic);
            } else {
                Picasso.with(context)
                        .load(R.drawable.placeholder_person)
                        .placeholder(R.drawable.placeholder_person)
                        .into(this.civUserPic);
            }
        }

        @Override
        public void onClick(View view) {

            Conversation convo  = createConvoPresenter.createConversation(userID);
            openMessagesActivity(view.getContext(), convo.getId());
        }
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
}