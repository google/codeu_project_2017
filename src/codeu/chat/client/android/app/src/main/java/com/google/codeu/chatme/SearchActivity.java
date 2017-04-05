package com.google.codeu.chatme;

import android.os.Bundle;
import android.app.Activity;

public class SearchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
