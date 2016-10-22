package com.maheshgaya.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mahesh Gaya on 10/16/16.
 */

public class MovieContract{
    public static final String CONTENT_AUTHORITY = "com.maheshgaya.android.popularmovies.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Constants for paths
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_FAVORITE = "favorite";
    public static final String PATH_MOST_POPULAR = "most_popular";
    public static final String PATH_TOP_RATED = "top_rated";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_TRAILER = "trailer";

    /**
     * Movie Table
     * contains: movie_api_key, title, image_url, plot, ratings, release_date
     */
    public static final class MovieEntry implements BaseColumns{
        //defining the default paths
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        //Define tables
        //table name
        public static final String TABLE_NAME = "movie";
        //Columns
        public static final String COLUMN_MOVIE_API_ID = "movie_api_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATINGS = "ratings";
        public static final String COLUMN_RELEASE_DATE = "release_date";

        //building the paths
        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * Favorite Table
     * contains: movie_id (foreign key)
     */
    public static final class FavoriteEntry implements BaseColumns{
        //defining the default paths
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        //Define tables
        //table name
        public static final String TABLE_NAME = "favorite";
        //columns
        public static final String COLUMN_MOVIE_ID = "movie_id"; //foreign key

        //building the paths
        public static Uri buildFavoriteUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //content://com.maheshgaya.android.popularmovies.app/favorite/movie/[movie_id]
        public static Uri buildFavoriteMovieByMovieId(int movie_id){
            return CONTENT_URI.buildUpon().appendPath(MovieEntry.TABLE_NAME)
                    .appendPath(Integer.toString(movie_id)).build();
        }

        public static Uri buildFavoriteMovies(){
            return CONTENT_URI.buildUpon().appendPath(MovieEntry.TABLE_NAME + "s").build();
        }

        public static String getMovieIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

    }

    /**
     * Most Popular Table
     * contains: movie_id (foreign key)
     */
    public static final class MostPopularEntry implements BaseColumns{
        //defining the default path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOST_POPULAR).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOST_POPULAR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOST_POPULAR;

        //Define tables
        //table name
        public static final String TABLE_NAME = "most_popular";
        //columns
        public static final String COLUMN_MOVIE_ID = "movie_id"; //foreign key

        //building the paths
        public static Uri buildMostPopularUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //content://com.maheshgaya.android.popularmovies.app/most_popular/movies
        public static Uri buildMostPopularMovies(){
            return CONTENT_URI.buildUpon().appendPath(MovieEntry.TABLE_NAME + "s").build();
        }

        public static String getMostPopularIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    /**
     * Top Rated Table
     * contains: movie_id (foreign key)
     */
    public static final class TopRatedEntry implements BaseColumns{
        //defining the default path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;

        //Define tables
        //table name
        public static final String TABLE_NAME = "top_rated";
        //columns
        public static final String COLUMN_MOVIE_ID = "movie_id"; //foreign key

        //building the paths
        public static Uri buildTopRatedUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //content://com.maheshgaya.android.popularmovies.app/top_rated/movies
        public static Uri buildTopRatedMovies(){
            return CONTENT_URI.buildUpon().appendPath(MovieEntry.TABLE_NAME + "s").build();
        }

        public static String getTopRatedIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    /**
     * Trailer Table
     * contains: movie_id (foreign key), trailer_url (full youtube link)
     */
    public static final class TrailerEntry implements BaseColumns{
        //defining the default path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        //Define tables
        //table name
        public static final String TABLE_NAME = "trailer";
        //columns
        public static final String COLUMN_MOVIE_ID = "movie_id"; //foreign key
        public static final String COLUMN_TRAILER_URL = "trailer_url"; //youtube url

        //building the paths
        public static Uri buildTrailerUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //content://com.maheshgaya.android.popularmovies.app/trailer/movie/[movie_id]
        public static Uri buildTrailerMovie(int movie_id){
            return CONTENT_URI.buildUpon().appendPath(MovieEntry.TABLE_NAME)
                    .appendPath(Integer.toString(movie_id)).build();
        }

        public static String getMovieIdFromUri(Uri uri){
            return uri.getLastPathSegment();
        }
    }

    /**
     * Review Table
     * contains: movie_id (foreign key), review_url (full link)
     */
    public static final class ReviewEntry implements BaseColumns{
        //defining the default path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        //Define tables
        //table name
        public static final String TABLE_NAME = "review";
        //columns
        public static final String COLUMN_MOVIE_ID = "movie_id"; //foreign key
        public static final String COLUMN_REVIEW_URL = "review_url";

        //building the paths
        public static Uri buildReviewUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //content://com.maheshgaya.android.popularmovies.app/review/movie/[movie_id]
        public static Uri buildReviewMovie(int movie_id){
            return CONTENT_URI.buildUpon().appendPath(MovieEntry.TABLE_NAME)
                    .appendPath(Integer.toString(movie_id)).build();
        }

        public static String getMovieIdFromUri(Uri uri){
            return uri.getLastPathSegment();
        }
    }




}
