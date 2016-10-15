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

    private MovieDBHelper mMovieDBHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    final static int MOVIE = 100;
    final static int MOVIE_WITH_ID = 101;

    final static int TOP_RATED = 200;

    final static int MOST_POPULAR = 300;

    final static int TRAILER = 400;
    final static int TRAILER_WITH_ID = 401;

    final static int REVIEW = 500;
    final static int REVIEW_WITH_ID = 501;

    final static int FAVORITE = 600;

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        //for movies
        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/#" , MOVIE_WITH_ID);
        //for top rated
        matcher.addURI(authority, MovieContract.PATH_TOP_RATED, TOP_RATED);

        //for most popular
        matcher.addURI(authority, MovieContract.PATH_MOST_POPULAR, MOST_POPULAR);

        //for trailer
        matcher.addURI(authority, MovieContract.PATH_TRAILERS, TRAILER);
        matcher.addURI(authority, MovieContract.PATH_TRAILERS, TRAILER_WITH_ID);

        //for review
        matcher.addURI(authority, MovieContract.PATH_REVIEWS, REVIEW);
        matcher.addURI(authority, MovieContract.PATH_REVIEWS, REVIEW_WITH_ID);

        //for favorite
        matcher.addURI(authority, MovieContract.PATH_FAVORITES, FAVORITE);


        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDBHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case MOVIE_WITH_ID: {
                //TODO
                retCursor = null;
                break;
            }
            case TRAILER_WITH_ID: {
                //TODO
                retCursor = null;
                break;
            }
            case REVIEW_WITH_ID: {
                //TODO
                retCursor = null;
                break;
            }
            case MOVIE: {
                retCursor = mMovieDBHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TOP_RATED: {
                retCursor = mMovieDBHelper.getReadableDatabase().query(
                        MovieContract.TopRatedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOST_POPULAR: {
                retCursor = mMovieDBHelper.getReadableDatabase().query(
                        MovieContract.MostPopularEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TRAILER: {
                retCursor = mMovieDBHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEW: {
                retCursor = mMovieDBHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FAVORITE: {
                retCursor = mMovieDBHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_DIR_TYPE;

            case TRAILER_WITH_ID:
                return MovieContract.TrailerEntry.CONTENT_DIR_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_DIR_TYPE;

            case TOP_RATED:
                return MovieContract.TopRatedEntry.CONTENT_DIR_TYPE;

            case MOST_POPULAR:
                return MovieContract.MostPopularEntry.CONTENT_DIR_TYPE;

            case REVIEW_WITH_ID:
                return MovieContract.ReviewEntry.CONTENT_DIR_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_DIR_TYPE;

            case FAVORITE:
                return MovieContract.FavoriteEntry.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

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
