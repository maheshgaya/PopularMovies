package com.maheshgaya.android.popularmovies.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.maheshgaya.android.popularmovies.Movie;

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

    private static final SQLiteQueryBuilder sFavoriteMovieQueryBuilder;
    private static final SQLiteQueryBuilder sReviewMovieQueryBuilder;
    private static final SQLiteQueryBuilder sTrailerMovieQueryBuilder;
    private static final SQLiteQueryBuilder sMostPopularMovieQueryBuilder;
    private static final SQLiteQueryBuilder sTopRatedMovieQueryBuilder;

    /**
     * Query
     * Favorite INNER JOIN
     */
    static {
        sFavoriteMovieQueryBuilder = new SQLiteQueryBuilder();
        //Inner join
        /**
         * movie INNER JOIN favorite ON movie._id = favorite.movie_id
         */
        sFavoriteMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.FavoriteEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID +
                        " = " + MovieContract.FavoriteEntry.TABLE_NAME +
                        "." + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID
        );
    }
    //favorite.movie_id = ?
    private static final String sFavoriteMovieIdSelection =
            MovieContract.FavoriteEntry.TABLE_NAME
                    + "." + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ? ";

    private Cursor getFavoriteByMovieId(Uri uri, String[] projection, String sortOrder){
        String movieId = MovieContract.FavoriteEntry.getMovieIdFromUri(uri);
        String selection = sFavoriteMovieIdSelection;
        String[] selectionArgs = new String[]{movieId};
        return sFavoriteMovieQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    /**
     * Query
     * Review INNER JOIN
     */
    static {
        sReviewMovieQueryBuilder = new SQLiteQueryBuilder();
        //Inner join
        /**
         * movie INNER JOIN review ON movie._id = review.movie_id
         */
        sReviewMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.ReviewEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID +
                        " = " + MovieContract.ReviewEntry.TABLE_NAME +
                        "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID
        );
    }
    //review.movie_id = ?
    private static final String sReviewMovieIdSelection =
            MovieContract.ReviewEntry.TABLE_NAME
                    + "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ";

    private Cursor getReviewByMovieId(Uri uri, String[] projection, String sortOrder){
        String movieId = MovieContract.ReviewEntry.getMovieIdFromUri(uri);
        String selection = sReviewMovieIdSelection;
        String[] selectionArgs = new String[]{movieId};
        return sReviewMovieQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    /**
     * Query
     * Trailer INNER JOIN
     */
    static {
        sTrailerMovieQueryBuilder = new SQLiteQueryBuilder();
        //Inner join
        /**
         * movie INNER JOIN trailer ON movie._id = trailer.movie_id
         */
        sTrailerMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.TrailerEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID +
                        " = " + MovieContract.TrailerEntry.TABLE_NAME +
                        "." + MovieContract.TrailerEntry.COLUMN_MOVIE_ID
        );

    }
    //trailer.movie_id = ?
    private static final String sTrailerMovieIdSelection =
            MovieContract.TrailerEntry.TABLE_NAME
                    + "." + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ";

    private Cursor getTrailerByMovieId(Uri uri, String[] projection, String sortOrder){
        String movieId = MovieContract.TrailerEntry.getMovieIdFromUri(uri);
        String selection = sTrailerMovieIdSelection;
        String[] selectionArgs = new String[]{movieId};
        return sTrailerMovieQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    /**
     * Query
     * Most Popular INNER JOIN
     */
    static {
        sMostPopularMovieQueryBuilder = new SQLiteQueryBuilder();
        //Inner join
        /**
         * movie INNER JOIN most_popular ON movie._id = most_popular.movie_id
         */
        sMostPopularMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MostPopularEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID +
                        " = " + MovieContract.MostPopularEntry.TABLE_NAME +
                        "." + MovieContract.MostPopularEntry.COLUMN_MOVIE_ID
        );
    }

    private Cursor getMostPopularMovies(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        return sMostPopularMovieQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /**
     * Query
     * Top Rated INNER JOIN
     */
    static {
        sTopRatedMovieQueryBuilder = new SQLiteQueryBuilder();
        //Inner join
        /**
         * movie INNER JOIN top_rated ON movie._id = top_rated.movie_id
         */

        sTopRatedMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.TopRatedEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID +
                        " = " + MovieContract.TopRatedEntry.TABLE_NAME +
                        "." + MovieContract.TopRatedEntry.COLUMN_MOVIE_ID
        );
    }

    private Cursor getTopRatedMovies(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        return sTopRatedMovieQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    private static UriMatcher buildUriMatcher(){
        //build uri matcher
        //Initialize uri matcher
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        //each uri
        //content://authority/movie
        matcher.addURI(authority, MovieContract.MovieEntry.TABLE_NAME, MOVIE);
        //content://authority/movie/#
        matcher.addURI(authority, MovieContract.MovieEntry.TABLE_NAME + "/#", MOVIE_WITH_ID);

        matcher.addURI(authority, MovieContract.FavoriteEntry.TABLE_NAME, FAVORITE);
        //content://authority/favorite/movie/#
        matcher.addURI(authority, MovieContract.FavoriteEntry.TABLE_NAME + "/" + MovieContract.MovieEntry.TABLE_NAME + "/#", FAVORITE_WITH_MOVIE_ID);

        //content://authority/most_popular
        matcher.addURI(authority, MovieContract.MostPopularEntry.TABLE_NAME, MOST_POPULAR);
        //content://authority/top_rated
        matcher.addURI(authority, MovieContract.TopRatedEntry.TABLE_NAME, TOP_RATED);

        //content://authority/review
        matcher.addURI(authority, MovieContract.ReviewEntry.TABLE_NAME, REVIEW);
        //content://authority/review/movie/#
        matcher.addURI(authority, MovieContract.ReviewEntry.TABLE_NAME + "/" + MovieContract.MovieEntry.TABLE_NAME + "/#", REVIEW_WITH_MOVIE_ID);

        //content://authority/trailer
        matcher.addURI(authority, MovieContract.TrailerEntry.TABLE_NAME, TRAILER);
        //content://authority/trailer/movie/#
        matcher.addURI(authority, MovieContract.TrailerEntry.TABLE_NAME + "/" + MovieContract.MovieEntry.TABLE_NAME + "/#", TRAILER_WITH_MOVIE_ID);
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
                break;
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
                break;
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
                break;
            }
            case FAVORITE_WITH_MOVIE_ID:{
                retCursor = getFavoriteByMovieId(uri, projection, sortOrder);
                break;
            }
            case MOST_POPULAR:{
                retCursor = getMostPopularMovies(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case TOP_RATED:{
                retCursor = getTopRatedMovies(uri, projection, selection, selectionArgs, sortOrder);
                break;
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
                break;

            }
            case REVIEW_WITH_MOVIE_ID:{
                retCursor = getReviewByMovieId(uri, projection, sortOrder);
                break;
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
                break;
            }
            case TRAILER_WITH_MOVIE_ID:{
                retCursor = getTrailerByMovieId(uri, projection, sortOrder);
                break;
            }
            default:{
                // Bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

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
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match){
            case MOVIE:{
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (_id > 0){
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case MOST_POPULAR:{
                long _id = db.insert(MovieContract.MostPopularEntry.TABLE_NAME, null, contentValues);
                if (_id > 0){
                    returnUri = MovieContract.MostPopularEntry.buildMostPopularUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case TOP_RATED:{
                long _id = db.insert(MovieContract.TopRatedEntry.TABLE_NAME, null, contentValues);
                if (_id > 0){
                    returnUri = MovieContract.TopRatedEntry.buildTopRatedUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case FAVORITE:{
                long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, contentValues);
                if (_id > 0){
                    returnUri = MovieContract.FavoriteEntry.buildFavoriteUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case REVIEW:{
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, contentValues);
                if (_id > 0){
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case TRAILER:{
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, contentValues);
                if (_id > 0){
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match){
            case MOVIE:{
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            }
            case MOST_POPULAR:{
                rowsDeleted = db.delete(
                        MovieContract.MostPopularEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            }
            case TOP_RATED:{
                rowsDeleted = db.delete(
                        MovieContract.TopRatedEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            }
            case FAVORITE:{
                rowsDeleted = db.delete(
                        MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            }
            case REVIEW:{
                rowsDeleted = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            }
            case TRAILER:{
                rowsDeleted = db.delete(
                        MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match){
            case MOVIE:{
                rowsUpdated = db.update(
                        MovieContract.MovieEntry.TABLE_NAME, contentValues, selection, selectionArgs
                );
                break;
            }
            case MOST_POPULAR:{
                rowsUpdated = db.update(
                        MovieContract.MostPopularEntry.TABLE_NAME, contentValues, selection, selectionArgs
                );
                break;
            }
            case TOP_RATED:{
                rowsUpdated = db.update(
                        MovieContract.TopRatedEntry.TABLE_NAME, contentValues, selection, selectionArgs
                );
                break;
            }
            case FAVORITE:{
                rowsUpdated = db.update(
                        MovieContract.FavoriteEntry.TABLE_NAME, contentValues, selection, selectionArgs
                );
                break;
            }
            case REVIEW:{
                rowsUpdated = db.update(
                        MovieContract.ReviewEntry.TABLE_NAME, contentValues, selection, selectionArgs
                );
                break;
            }
            case TRAILER:{
                rowsUpdated = db.update(
                        MovieContract.TrailerEntry.TABLE_NAME, contentValues, selection, selectionArgs
                );
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match){
            //Only for movies
            case MOVIE:{
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values){
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                        db.setTransactionSuccessful();
                    }
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case MOST_POPULAR:{
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values){
                        long _id = db.insert(MovieContract.MostPopularEntry.TABLE_NAME, null, value);
                        if (_id != -1){
                            returnCount++;
                        }
                        db.setTransactionSuccessful();
                    }
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case TOP_RATED:{
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values){
                        long _id = db.insert(MovieContract.TopRatedEntry.TABLE_NAME, null, value);
                        if (_id != -1){
                            returnCount++;
                        }
                        db.setTransactionSuccessful();
                    }
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }

    }
}
