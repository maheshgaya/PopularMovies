package com.maheshgaya.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Copyright (c) Mahesh Gaya
 * Author: Mahesh Gaya
 * Date: 9/17/16.
 */

/**
 * MovieFragment
 * Deals with getting data from TheMovieDB in JSON
 * and updating the gridview for showing the posters/thumbnails
 */
public class MovieFragment extends Fragment {

    private FetchMovieTask fetchMovieTask;

    private static final String TAG = MovieFragment.class.getSimpleName(); //logging purposes
    private MovieAdapter mMovieAdapter; //for managing the gridview
    private GridView mGridView;
    private Movie[] mMovies = {}; //array of movies

    /**
     * MovieFragment Constructor
     * initialize the Fragment
     */
    public MovieFragment(){
        //empty constructor
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Constant.INSTANCE_STATE_MOVIES_ARRAY)) {
                Parcelable[] savedArray = savedInstanceState.getParcelableArray(Constant.INSTANCE_STATE_MOVIES_ARRAY);
                try{
                    mMovies = new Movie[savedArray.length];
                    System.arraycopy(savedArray, 0, mMovies, 0, savedArray.length );
                }catch (java.lang.NullPointerException e){
                    Log.e(TAG, "onCreate: ",e );
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(Constant.INSTANCE_STATE_MOVIES_ARRAY, mMovies);
        super.onSaveInstanceState(outState);
    }

    /**
     * onOptionsItemSelected
     * @param item
     * @return item selected
     * does nothing useful currently
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /**
     * updateMovie
     * get data from asynctask and update the gridview
     */
    public void updateMovie(){
        //will get movies according to sort preference
        //default is Most Popular (popular)
        fetchMovieTask = new FetchMovieTask(getActivity(), mMovieAdapter, mGridView);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortPref = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        fetchMovieTask.execute(sortPref);


    }


    /**
     * onStart
     * upon start fragment get data from TheMovieDB and update the gridview
     */
    @Override
    public void onStart() {
        super.onStart();
        // sort order has changed or the movies list is empty
        if(mMovies.length == 0){
            // fetch the movie list
            updateMovie();
        } else {
            // do nothing
        }

    }

    /**
     * onCreateView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return rootView
     * Initialize the views
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Log.d(TAG, "onCreateView: method executed");
        mMovieAdapter = new MovieAdapter(getActivity(), mMovies);
        //Log.d(TAG, "onCreateView: " + mMovieAdapter);

        // Get a reference to the GridView, and attach this adapter to it.
        mGridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        mGridView.setAdapter(mMovieAdapter);


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Movie movie = mMovieAdapter.getItem(position);
                //get more details about the particular movie
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Constant.EXTRA_MOVIE_PARCELABLE, movie);
                startActivity(intent);
            }
        });


        return rootView;
    }

}
