package com.maheshgaya.android.popularmovies.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Mahesh Gaya on 10/15/16.
 */


public class MovieProvider extends ContentProvider {
    private static final String TAG = MovieProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    //code for UriMatcher
    private static final int MOVIE = 100;
    private static final int MOVIE_WITH_ID = 101;

    private static final int FAVORITE = 200;
    private static final int FAVORITE_WITH_MOVIE_ID = 201;

    private static final int MOST_POPULAR = 300;
    private static final int TOP_RATED = 400;

    private static final int REVIEW = 500;
    private static final int REVIEW_WITH_MOVIE_ID = 501; //Dir type

    private static final int TRAILER = 600;
    private static final int TRAILER_WITH_MOVIE_ID = 601; //Dir type

    private static UriMatcher buildUriMatcher(){
        //build uri matcher
        //Initialize uri matcher
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        //each uri
        matcher.addURI(authority, MovieContract.MovieEntry.TABLE_NAME, MOVIE);
        matcher.addURI(authority, MovieContract.MovieEntry.TABLE_NAME + "/#", MOVIE_WITH_ID);

        matcher.addURI(authority, MovieContract.FavoriteEntry.TABLE_NAME, FAVORITE);
        matcher.addURI(authority, MovieContract.FavoriteEntry.TABLE_NAME + "/#", FAVORITE_WITH_MOVIE_ID);

        matcher.addURI(authority, MovieContract.MostPopularEntry.TABLE_NAME, MOST_POPULAR);
        matcher.addURI(authority, MovieContract.TopRatedEntry.TABLE_NAME, TOP_RATED);

        matcher.addURI(authority, MovieContract.ReviewEntry.TABLE_NAME, REVIEW);
        matcher.addURI(authority, MovieContract.ReviewEntry.TABLE_NAME + "/#", REVIEW_WITH_MOVIE_ID);

        matcher.addURI(authority, MovieContract.TrailerEntry.TABLE_NAME, TRAILER);
        matcher.addURI(authority, MovieContract.TrailerEntry.TABLE_NAME + "/#", TRAILER_WITH_MOVIE_ID);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case MOVIE:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return retCursor;
            }
            case MOVIE_WITH_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                return retCursor;
            }
            case FAVORITE:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return retCursor;
            }
            case FAVORITE_WITH_MOVIE_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                return retCursor;
            }
            case MOST_POPULAR:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MostPopularEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return retCursor;
            }
            case TOP_RATED:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TopRatedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return retCursor;
            }
            case REVIEW:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return retCursor;

            }
            case REVIEW_WITH_MOVIE_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                return retCursor;
            }
            case TRAILER:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return retCursor;
            }
            case TRAILER_WITH_MOVIE_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                return retCursor;
            }
            default:{
                // Bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case MOVIE:{
                return MovieContract.MovieEntry.CONTENT_TYPE;
            }
            case MOVIE_WITH_ID:{
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            }
            case FAVORITE:{
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            }
            case FAVORITE_WITH_MOVIE_ID:{
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            }
            case MOST_POPULAR:{
                return MovieContract.MostPopularEntry.CONTENT_TYPE;
            }
            case TOP_RATED:{
                return MovieContract.TopRatedEntry.CONTENT_TYPE;
            }
            case REVIEW:{
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            }
            case REVIEW_WITH_MOVIE_ID:{
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            }
            case TRAILER:{
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            }
            case TRAILER_WITH_MOVIE_ID:{
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            }
            default:{
                //Bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }
}
