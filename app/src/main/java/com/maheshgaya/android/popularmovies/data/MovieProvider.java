package com.maheshgaya.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Mahesh Gaya on 10/6/16.
 */
public class MovieProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    final static int MOVIE = 100;
    final static int TOP_RATED = 200;
    final static int MOST_POPULAR = 300;
    final static int TRAILER = 400;
    final static int REVIEW = 500;
    final static int FAVORITE = 600;

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        //for movies
        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIE);

        //for top rated
        matcher.addURI(authority, MovieContract.PATH_TOP_RATED, TOP_RATED);

        //for most popular
        matcher.addURI(authority, MovieContract.PATH_MOST_POPULAR, MOST_POPULAR);

        //for trailer
        matcher.addURI(authority, MovieContract.PATH_TRAILERS, TRAILER);

        //for review
        matcher.addURI(authority, MovieContract.PATH_REVIEWS, REVIEW);

        //for favorite
        matcher.addURI(authority, MovieContract.PATH_FAVORITES, FAVORITE);


        return matcher;
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
