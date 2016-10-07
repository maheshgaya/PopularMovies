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
    public static final String PATH_MOVIES = "movie";

    //BASE URL for image path
    private static final String IMAGE_PATH_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    public static final class MovieEntry implements BaseColumns{
        //TABLE NAME
        public static final String TABLE_MOVIES = "movie";
        //COLUMNS
        public static final String MOVIE_ID = "movie_id"; //unique movie id from API
        public static final String COLUMN_TITLE = "title"; //title of the movie
        public static final String COLUMN_IMAGE_PATH = "image"; //thumbnail path of the movie
        public static final String COLUMN_PLOT = "plot"; //
        public static final String COLUMN_RATINGS = "ratings";
        public static final String COLUMN_RELEASE_DATE = "release_date";

        //create content URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_MOVIES).build();
        //create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIES;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + TABLE_MOVIES;

        //for building URIs on insertion
        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class TrailerEntry implements BaseColumns{
        //TABLE NAME
        public static final String TABLE_TRAILER = "trailer";

        //COLUMNS
        public static final String _ID = "_id"; //unique table id (autoincrement)
        public static final String COLUMN_MOVIE_ID = "movie_id"; //FOREIGN KEY (Can have multiple)
        //url: https://www.youtube.com/watch?v=9vN6DHB6bJc
        //The key is 9vN6DHB6bJc
        public static final String COLUMN_TRAILER_KEY = "youtube_key";
    }

    /**
     * This class defines entries for the favorite functionality
     * If a user favorite a movie, it will be stored in this table
     * Columns: _id, movie_id (Foreign key), favorite(boolean (true ["1"] if user favorite a movie)
     * NOTE: if a movie is not favorite, it will not be in this table (the record will be deleted)
     */
    public static final class FavoriteEntry implements BaseColumns{
        //TABLE NAME
        public static final String TABLE_FAVORITE = "favorite";

    }
}
