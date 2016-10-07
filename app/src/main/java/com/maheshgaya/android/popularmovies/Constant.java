package com.maheshgaya.android.popularmovies;


/**
 * Copyright (c) Mahesh Gaya
 * Author: Mahesh Gaya
 * Date: 9/19/16.
 */

/**
 * This class provides all the constant that will be used
 * TMDB constants are used for getting the data from TheMovieDB
 * */
public class Constant {
    public final static String EXTRA_MOVIE_PARCELABLE = "com.maheshgaya.android.popularmovies.EXTRA_MOVIE_PARCELABLE";
    //public final static String INSTANCE_STATE_MOVIES = "movies";
    public final static String INSTANCE_STATE_MOVIES_ARRAY = "moviesArray";
    public final static String INSTANCE_MOVIE_FRAGMENT = "mainFragment";

    //names of JSON tags to be extracted
    public final static String TMDB_RESULTS = "results";
    public final static String TMDB_ID = "id";
    public final static String TMDB_TITLE = "original_title";
    public final static String TMDB_THUMBNAIL_URL = "poster_path";
    public final static String TMDB_PLOT = "overview";
    public final static String TMDB_RATINGS = "vote_average";
    public final static String TMDB_RELEASE_DATE = "release_date";

}
