package com.maheshgaya.android.popularmovies.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.maheshgaya.android.popularmovies.Constant;
import com.maheshgaya.android.popularmovies.data.MovieContract;
import com.maheshgaya.android.popularmovies.model.Movie;
import com.maheshgaya.android.popularmovies.R;
import com.maheshgaya.android.popularmovies.syncdata.FetchMovieTask;
import com.maheshgaya.android.popularmovies.syncdata.Utility;

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
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int MOVIE_LOADER = 0;
    private FetchMovieTask fetchMovieTask;

    private static final String TAG = MovieFragment.class.getSimpleName(); //logging purposes
    private MovieAdapter mMovieAdapter; //for managing the gridview
    private GridView mGridView;

    private static final  String[] MOVIE_COLUMNS_PROJECTION = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_API_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_IMAGE_URL,
            MovieContract.MovieEntry.COLUMN_PLOT,
            MovieContract.MovieEntry.COLUMN_RATINGS,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE
    };

    static final int COLUMN_MOVIE_ID = 0;
    static final int COLUMN_MOVIE_API_ID = 1;
    static final int COLUMN_MOVIE_TITLE = 2;
    static final int COLUMN_MOVIE_IMAGE_URL = 3;
    static final int COLUMN_MOVIE_PLOT = 4;
    static final int COLUMN_MOVIE_RATINGS = 5;
    static final int COLUMN_MOVIE_RELEASE_DATE = 6;






    /**
     * MovieFragment Constructor
     * initialize the Fragment
     */
    public MovieFragment(){
        //empty constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putParcelableArray(Constant.INSTANCE_STATE_MOVIES_ARRAY, mMovies);
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
        fetchMovieTask = new FetchMovieTask(getActivity());
        String sortPref = Utility.getSortPreference(getActivity());
        fetchMovieTask.execute(sortPref);


    }


    /**
     * onStart
     * upon start fragment get data from TheMovieDB and update the gridview
     */
    @Override
    public void onStart() {
        super.onStart();
        updateMovie();

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


        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);


        // Get a reference to the GridView, and attach this adapter to it.
        mGridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        mGridView.setAdapter(mMovieAdapter);




        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieUri;
        String sortOrder;
        if (Utility.isMostPopular(getActivity())){
            //query most popular
            sortOrder = MovieContract.MostPopularEntry._ID + " ASC";
            movieUri = MovieContract.MostPopularEntry.buildMostPopularMovies();

        } else {
            //query top rated
            sortOrder = MovieContract.TopRatedEntry.COLUMN_MOVIE_ID + " ASC";
            movieUri = MovieContract.TopRatedEntry.buildTopRatedMovies();
        }
        return new CursorLoader(getActivity(),
                movieUri,
                MOVIE_COLUMNS_PROJECTION,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mMovieAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }
}
