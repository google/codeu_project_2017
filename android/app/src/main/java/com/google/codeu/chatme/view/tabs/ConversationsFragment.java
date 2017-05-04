package com.google.codeu.chatme.view.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.codeu.chatme.R;
import com.google.codeu.chatme.view.adapter.ConversationListAdapter;

public class ConversationsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private RecyclerView rvChatList;
    private ConversationListAdapter conversationListAdapter;

    /**
     * Required empty public constructor
     */
    public ConversationsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConversationsFragment.
     */
    public static ConversationsFragment newInstance() {
        ConversationsFragment fragment = new ConversationsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);
        initializeUI(view);
        return view;
    }

    /**
     * Sets up user interface by loading the list of conversations for the current
     * user in the recyclerview
     *
     * @param view inflated {@link ConversationsFragment} layout view
     */
    private void initializeUI(View view) {
        rvChatList = (RecyclerView) view.findViewById(R.id.rvChatList);
        rvChatList.setLayoutManager(new LinearLayoutManager(getActivity()));

        conversationListAdapter = new ConversationListAdapter(getContext());
        rvChatList.setAdapter(conversationListAdapter);

        conversationListAdapter.loadConversations();
    }


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
