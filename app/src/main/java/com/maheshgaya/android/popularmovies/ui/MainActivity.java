package com.maheshgaya.android.popularmovies.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.maheshgaya.android.popularmovies.R;
import com.maheshgaya.android.popularmovies.syncdata.Utility;

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
    private final String MOVIE_FRAGMENT_TAG = "MTAG";
    private final String TAG = MainActivity.class.getSimpleName();
    private String mSortPref;
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
        mSortPref = Utility.getSortPreference(this); //initialize sort Preference
        FragmentManager fragmentManager = new MainActivity().getSupportFragmentManager();
        mFragment = fragmentManager.findFragmentByTag(MOVIE_FRAGMENT_TAG);

        if (savedInstanceState == null) {
            mFragment = new MovieFragment();
             getSupportFragmentManager()
                     .beginTransaction()
                    .add(R.id.container_main, mFragment, MOVIE_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortPref = Utility.getSortPreference(this);
        //Log.d(TAG, "onResume: " + sortPref);
        if (sortPref != null && !sortPref.equals(mSortPref)){
            MovieFragment mf = (MovieFragment)getSupportFragmentManager().findFragmentByTag(MOVIE_FRAGMENT_TAG);
            mf.onSortPrefChanged();
            mSortPref = sortPref;
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
        } else if (id == R.id.action_refresh){
            MovieFragment mf = (MovieFragment)getSupportFragmentManager().findFragmentByTag(MOVIE_FRAGMENT_TAG);
            mf.updateMovie();
        }

        return super.onOptionsItemSelected(item);
    }



}
