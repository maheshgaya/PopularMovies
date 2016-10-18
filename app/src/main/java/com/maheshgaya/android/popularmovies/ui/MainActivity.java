package com.maheshgaya.android.popularmovies.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.maheshgaya.android.popularmovies.R;

/**
 * Copyright (c) Mahesh Gaya
 * Author: Mahesh Gaya
 * Date: 9/17/16.
 */

/**
 * MainActivity
 * Deals with showing the posters in grid view format (2 columns)
 * */
public class MainActivity extends AppCompatActivity{
    private final String TAG_MOVIE_FRAGMENT = "movie_fragment";
    private final String TAG = MainActivity.class.getSimpleName();
    Fragment mFragment;

    /**
     * onCreate
     * @param savedInstanceState
     * initialize activity_main.xml for view
     * initialize container_main for containing the posters
     * Reference for saving states for fragment and activity:
     * http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = new MainActivity().getSupportFragmentManager();
        mFragment = (MovieFragment)fragmentManager.findFragmentByTag(TAG_MOVIE_FRAGMENT);

        if (mFragment == null) {
            //Log.d(TAG, "onCreate: savedInstanceState is null");
            mFragment = new MovieFragment();
             getSupportFragmentManager()
                     .beginTransaction()
                    .add(R.id.container_main, mFragment)
                    .commit();
        }
    }


    /**
     * onCreateOptionsMenu
     * @param menu
     * @return true
     * inflates the menu for MainActivity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * onOptionsItemSelected
     * @param item
     * @return the item selected
     * if action_settings pressed
     *      then open settings activity
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.pref_general.
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
