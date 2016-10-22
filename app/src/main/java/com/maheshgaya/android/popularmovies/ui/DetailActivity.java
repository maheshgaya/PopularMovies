package com.maheshgaya.android.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.maheshgaya.android.popularmovies.R;


/**
 * Copyright (c) Mahesh Gaya
 * Author: Mahesh Gaya
 * Date: 9/18/16.
 */

/**
 * This class deals with showing the details for each individual movie.
 * If user tap on the poster, this class will be executed.
 * It will show information about the movie
 * */

public class DetailActivity extends AppCompatActivity{
    private static final String  TAG = DetailActivity.class.getSimpleName();

    /**
     * onCreate Method
     * @param savedInstanceState
     * Functionality: initialize the fragment layout for DetailFragment
     * */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

    /**
     * onCreateOptionsMenu
     * @param menu
     * inflate the menu for this activity
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }


    /**
     * onOptionsItemSelected
     * @param item
     * check if action_settings is tapped
     *      opens settings activity if it is the case
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this.getApplicationContext(), SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
