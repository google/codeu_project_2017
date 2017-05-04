package com.google.codeu.chatme.view.tabs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.codeu.chatme.R;

/**
 * This activity controls the tab panel for switching between the following three fragments
 * - {@link ConversationsFragment}
 * - {@link ProfileFragment}
 * - {@link UsersFragment}
 */
public class TabsActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        ConversationsFragment.OnFragmentInteractionListener,
        UsersFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        initFragment(new ConversationsFragment());
    }

    /**
     * Handles event of selecting a {@link BottomNavigationView} item
     *
     * @param item {@link BottomNavigationView} menu item selected
     * @return true if the event was handles, false otherwise
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass;

        // determines appropriate fragment using item Id
        switch (item.getItemId()) {
            case R.id.navigation_chats:
                fragmentClass = ConversationsFragment.class;
                break;
            case R.id.navigation_profile:
                fragmentClass = ProfileFragment.class;
                break;
            case R.id.navigation_users:
                fragmentClass = UsersFragment.class;
                break;
            default:
                fragmentClass = ConversationsFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initFragment(fragment);
        return true;
    }

    /**
     * Initializes fragment by changing container view content
     *
     * @param newFragment new {@link Fragment} object
     */
    private void initFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_nav, newFragment);
        transaction.commit();
    }
}
