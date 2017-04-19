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
import com.google.codeu.chatme.presenter.UserPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link android.support.v7.widget.RecyclerView.Adapter} to bind the list of users
 * to the recyclerview in {@link com.google.codeu.chatme.view.tabs.UsersFragment}
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {


    /**
     * List of users
     */
    private List<User> users = new ArrayList<>();
    private UserPresenter presenter;

    public UserListAdapter() {
        this.presenter = new UserPresenter(this);
    }


    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        return new ViewHolder(v);
    }

    public void loadUsers() {
        this.presenter.loadUsers();
    }


    @Override
    public void onBindViewHolder(UserListAdapter.ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.tvSender.setText(user.getName());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openMessagesActivity(view.getContext(), conversation.getId());
//            }
//        });
    }

    @Override
    public int getItemCount() {return users.size();}


    /**
     * A {@link android.support.v7.widget.RecyclerView.ViewHolder} class to encapsulate
     * various views of a user list item
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSender;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSender = (TextView) itemView.findViewById(R.id.tvSender);
        }
    }
}



