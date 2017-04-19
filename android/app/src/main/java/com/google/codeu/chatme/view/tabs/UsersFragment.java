package com.google.codeu.chatme.view.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.codeu.chatme.R;
import com.google.codeu.chatme.view.adapter.ChatListAdapter;
import com.google.codeu.chatme.view.adapter.UserListAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UsersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    /**
     * {@link RecyclerView} to hold the list of Users registered in the database
     */
    private RecyclerView rvUserList;
    private UserListAdapter userListAdapter;

    /**
     * Required empty public constructor
     */
    public UsersFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UsersFragment.
     */
    public static UsersFragment newInstance() {
        UsersFragment fragment = new UsersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    ///////////////

    /**
     * Sets up user interface by loading the list of conversations for the current
     * user in the recyclerview
     *
     * @param view inflated {@link ChatsFragment} layout view
     */
    private void initializeUI(View view) {
        rvUserList = (RecyclerView) view.findViewById(R.id.userList);
        //rvUserList.setLayoutManager(new LinearLayoutManager(getActivity()));

        userListAdapter = new UserListAdapter();
        rvUserList.setAdapter(userListAdapter);

        userListAdapter.loadUsers();
    }
    /////////////////////


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }
}
