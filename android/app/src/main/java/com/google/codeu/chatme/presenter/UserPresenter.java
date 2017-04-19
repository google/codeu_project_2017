package com.google.codeu.chatme.presenter;

import android.util.Log;

import com.google.codeu.chatme.model.User;
import com.google.codeu.chatme.view.adapter.UserListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Following MVP design pattern, this class encapsulates the functionality to
 * store and retrieve data related to the current users from Firebase
 * database
 *
 * @see UserInteractor for documentation of interface methods
 */
public class UserPresenter implements UserInteractor {

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    private final UserListAdapter view;

    private static final String TAG = UserPresenter.class.getName();


    /**
     * Constructor to accept a reference to a recycler view adapter to bind
     * user data to views
     *
     * @param view {@link UserListAdapter} reference
     */
    public UserPresenter(UserListAdapter view) {
        this.view = view;
    }

    public void loadUsers() {
        Query userQuery = mRootRef.child("users");

        userQuery.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> users = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    users.add(user);
                    Log.d(TAG, "loadUsers:onDataChange:userID:" + user.getId());
                }

                // TODO: call adapterView.loadUsers() to display users
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "loadUsers:failure " + databaseError.getMessage());
            }
        });
    }


}
