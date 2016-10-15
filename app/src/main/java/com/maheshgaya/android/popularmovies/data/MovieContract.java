package com.maheshgaya.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mahesh Gaya on 10/6/16.
 */
public class MovieContract {
    //URI Config
    public static final String CONTENT_AUTHORITY = "com.maheshgaya.android.popularmovies.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //URI Parameters Config
    //example: content://com.maheshgaya.android.popularmovies.app/movies
    //example: content://com.maheshgaya.android.popularmovies.app/movies/_ID
    public static final String PATH_MOVIES = "movies";
    //example: content://com.maheshgaya.android.popularmovies.app/most_popular
    //returns all most popular movies
    public static final String PATH_MOST_POPULAR = "most_popular";
    //example: content://com.maheshgaya.android.popularmovies.app/top_rated
    //returns all top rated movies
    public static final String PATH_TOP_RATED = "top_rated";
    //example: content://com.maheshgaya.android.popularmovies.app/trailers/movie_id
    //returns all trailers for a movie queried by movie_id
    public static final String PATH_TRAILERS = "trailers";
    //example: content://com.maheshgaya.android.popularmovies.app/favorites/movie_id
    //returns a specific movie that is in the favorite table
    public static final String PATH_FAVORITES = "favorites";
    //example: content://com.maheshgaya.android.popularmovies.app/reviews/movie_id
    //returns all movies that has the movie_id
    public static final String PATH_REVIEWS = "reviews";

    //TODO: Remove this unnecessary statement. Will be used inside Movie class
    private static final String IMAGE_PATH_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    private MovieContract(){
        //prevent other classes from accidentally defining this class
    }


    public static final class MovieEntry implements BaseColumns{
        //create content URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES).build();
        //create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        /*DEFINE TABLE*/
        //TABLE NAME
        public static final String TABLE_NAME = "movie";
        //COLUMNS
        public static final String _ID = "movie_id"; //unique movie id from API (non-autoincrement)
        public static final String COLUMN_TITLE = "title"; //title of the movie
        public static final String COLUMN_IMAGE_PATH = "image"; //thumbnail path of the movie
        public static final String COLUMN_PLOT = "plot"; //plot of the movie
        public static final String COLUMN_RATINGS = "ratings"; //rating over 10 of the movie
        public static final String COLUMN_RELEASE_DATE = "release_date"; //release date of the movie



        //for building URIs on insertion
        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /*
    * API CALL URL: https://api.themoviedb.org/3/movie/{movie_id}/videos?api_key=<<api_key>>&language=en-US
    * */
    public static final class TrailerEntry implements BaseColumns{
        //Create content URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TRAILERS).build();
        //create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;
        //create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;

        /*DEFINE TABLE*/
        //TABLE NAME
        public static final String TABLE_NAME = "trailer";

        //COLUMNS
        public static final String _ID = "trailer_id"; //unique table id (autoincrement)
        public static final String COLUMN_MOVIE_ID = "movie_id"; //FOREIGN KEY (Can have multiple)
        //url: https://www.youtube.com/watch?v=9vN6DHB6bJc
        //The key is 9vN6DHB6bJc
        public static final String COLUMN_TRAILER_KEY = "youtube_key";


        //for building URIs on insertion
        public static Uri buildTrailerUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /*
    * API CALL URL: https://api.themoviedb.org/3/movie/{movie_id}/reviews?api_key=<<api_key>>&language=en-US
    * */
    public static final class ReviewEntry implements BaseColumns{
        //Create content URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TRAILERS).build();
        //create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        //create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        /*DEFINE TABLE*/
        //TABLE NAME
        public static final String TABLE_NAME = "review";
        //COLUMNS
        public static final String _ID = "review_id"; //unique table id (autoincrement)
        public static final String COLUMN_MOVIE_ID = "movie_id"; //FOREIGN KEY (can have multiple)
        public static final String COLUMN_REVIEW_URL = "url"; //url for the review


        //for building URIs on insertion
        public static Uri buildReviewUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * This class defines entries for the favorite functionality
     * If a user favorite a movie, it will be stored in this table
     * Columns: _id, movie_id (Foreign key)
     * NOTE: if a movie is not favorite, it will not be in this table
     * (the record will be deleted if unfavorite)
     */
    public static final class FavoriteEntry implements BaseColumns{
        //Create content URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES).build();
        //create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        //create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        /*DEFINE TABLE*/
        //TABLE NAME
        public static final String TABLE_NAME = "favorite";
        //COLUMNS
        public static final String _ID = "favorite_id"; //autoincrement
        public static final String COLUMN_MOVIE_ID = "movie_id"; //FOREIGN KEY


        //for building URIs on insertion
        public static Uri buildFavoriteUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

    /**
     * Defines the movies queried by top rated url
     * This will check if the movie exists in movie table,
     * if not then it will create a new one then add it here
     */

    public static final class TopRatedEntry implements BaseColumns{

        //Create content URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TOP_RATED).build();

        //Create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;
        //Create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY  + "/" + PATH_TOP_RATED;

        /* DEFINE TABLE*/
        //TABLE NAME
        public static final String TABLE_NAME = "top_rated";
        //COLUMNS
        public static final String _ID = "top_rated_id";
        public static final String COLUMN_MOVIE_ID = "movie_id"; //FOREIGN KEY

        //for building URIs on insertion
        public static  Uri buildTopRatedUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * Defines the movies queried by most popular url
     * This will check if the movie exists in movie table,
     * if not then it will create a new one then add it here
     */

    public static final class MostPopularEntry implements BaseColumns{

        //Create content URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOST_POPULAR).build();

        //Create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOST_POPULAR;
        //Create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY  + "/" + PATH_MOST_POPULAR;

        /* DEFINE TABLE*/
        //TABLE NAME
        public static final String TABLE_NAME = "most_popular";
        //COLUMNS
        public static final String _ID = "most_popular_id";
        public static final String COLUMN_MOVIE_ID = "movie_id"; //FOREIGN KEY

        //for building URIs on insertion
        public static  Uri buildMostPopularUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
