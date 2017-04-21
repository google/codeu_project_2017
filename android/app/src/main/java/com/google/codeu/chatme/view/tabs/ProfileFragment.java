package com.google.codeu.chatme.view.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import com.google.codeu.chatme.presenter.ProfilePresenter;
import com.google.codeu.chatme.R;
import com.google.codeu.chatme.view.login.LoginActivity;
import com.google.codeu.chatme.model.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements ProfileView{

    private OnFragmentInteractionListener mListener;

        // fragment edit texts and buttons
    private EditText etPassword;
    private EditText etUsername;
    private EditText etFullName;
    private Button btnSaveChanges;
    private Button btnLogOut;

    private ProfilePresenter presenter;
    private User user;

    /**
     * Required empty public constructor
     */
    public ProfileFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        etFullName = (EditText) view.findViewById(R.id.etFullName);
        etUsername = (EditText) view.findViewById(R.id.etUsername);
        etPassword = (EditText) view.findViewById(R.id.etPassword);

        btnSaveChanges = (Button) view.findViewById(R.id.btnSaveChanges);
        btnLogOut = (Button) view.findViewById(R.id.btnLogOut);
        presenter = new ProfilePresenter(this);
        presenter.postConstruct();

        presenter.getUserProfile();

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = etFullName.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                presenter.updateUser(fullName,username,password);
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.signOut();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
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

    @Override
    public void openLoginActivity() {
        Intent mIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(mIntent);
    }

    @Override
    public void showProgressDialog(int messsage) {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void makeToast(String message) {

    }
    // TODO: implement profile picture
    public void setUserProfile(User userData) {
        this.user = userData;
        etUsername.setText(user.getUsername());
        etFullName.setText(user.getFullName());
    }

    public void setPasswordFieldError(int err_et_password) {
        etPassword.setError(getString(err_et_password));
    }
    public void setUsernameFieldError(int err_et_username) {
        etUsername.setError(getString(err_et_username));
    }
    public void setFullNameFieldError(int err_et_fullname) {
        etFullName.setError(getString(err_et_fullname));
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
