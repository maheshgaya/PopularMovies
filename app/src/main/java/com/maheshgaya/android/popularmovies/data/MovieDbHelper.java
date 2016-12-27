package com.maheshgaya.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mahesh Gaya on 10/16/16.
 */

public class MovieDbHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "movie.db";
    public static final int DATABASE_VERSION = 6;

    public MovieDbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*
        * CREATE TABLE movie (
        * movie_id INTEGER NOT NULL PRIMARY KEY,
        * movie_api_id INTEGER UNIQUE,
        * title TEXT NOT NULL,
        * image TEXT NOT NULL,
        * plot TEXT NOT NULL,
        * ratings TEXT NOT NULL,
        * release_date TEXT NOT NULL
        * );
        * */
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + "(" +
                        MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_API_ID + " INTEGER UNIQUE, " +
                        MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_RATINGS + " REAL NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL " +
                        ");";

        /*
        * CREATE TABLE favorite(
        * favorite_id INTEGER PRIMARY KEY AUTOINCREMENT,
        * movie_id INTEGER NOT NULL,
        * FOREIGN KEY (movie_id) REFERENCES movie(movie_id),
        * UNIQUE (movie_id) ON CONFLICT REPLACE
        * );
        * */

        final String SQL_CREATE_FAVORITE_TABLE =
                "CREATE TABLE " + MovieContract.FavoriteEntry.TABLE_NAME + "(" +
                        MovieContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        "FOREIGN KEY (" + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + ") " +
                        "REFERENCES " + MovieContract.MovieEntry.TABLE_NAME + "(" + MovieContract.MovieEntry._ID + ")," +
                        "UNIQUE (" + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE" +
                        ");";


        /*
        * CREATE TABLE most_popular (
        * most_popular_id INTEGER PRIMARY KEY AUTOINCREMENT,
        * movie_id INTEGER NOT NULL,
        * current_movie INTEGER NOT NULL,
        * FOREIGN KEY (movie_id) REFERENCES movie(movie_id)
        * );
         */
        final String SQL_CREATE_MOST_POPULAR_TABLE =
                "CREATE TABLE " + MovieContract.MostPopularEntry.TABLE_NAME + "(" +
                        MovieContract.MostPopularEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.MostPopularEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        MovieContract.MostPopularEntry.COLUMN_CURRENT_MOVIE + " INTEGER NOT NULL," +
                        "FOREIGN KEY (" + MovieContract.MostPopularEntry.COLUMN_MOVIE_ID + ") " +
                        "REFERENCES " + MovieContract.MovieEntry.TABLE_NAME + "(" + MovieContract.MovieEntry._ID + ")" +
                        ");";

        /*
        * CREATE TABLE top_rated (
        * top_rated_id INTEGER PRIMARY KEY AUTOINCREMENT,
        * movie_id INTEGER NOT NULL,
        * current_movie INTEGER NOT NULL,
        * FOREIGN KEY (movie_id) REFERENCES movie(movie_id)
        * );
         */
        final String SQL_CREATE_TOP_RATED_TABLE =
                "CREATE TABLE " + MovieContract.TopRatedEntry.TABLE_NAME + "(" +
                        MovieContract.TopRatedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.TopRatedEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        MovieContract.TopRatedEntry.COLUMN_CURRENT_MOVIE + " INTEGER NOT NULL, " +
                        "FOREIGN KEY (" + MovieContract.TopRatedEntry.COLUMN_MOVIE_ID + ") " +
                        "REFERENCES " + MovieContract.MovieEntry.TABLE_NAME + "(" + MovieContract.MovieEntry._ID + ")" +
                        ");";

        /*
        * CREATE TABLE trailer (
        * trailer_id INTEGER PRIMARY KEY AUTOINCREMENT,
        * movie_id INTEGER NOT NULL,
        * trailer_url TEXT NOT NULL,
        * FOREIGN KEY (movie_id) REFERENCES movie(movie_id)
        * );
        * */
        final String SQL_CREATE_TRAILER_TABLE =
                "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME +"("  +
                        MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        MovieContract.TrailerEntry.COLUMN_TRAILER_URL + " TEXT NOT NULL, " +
                        "FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + ") " +
                        "REFERENCES " + MovieContract.MovieEntry.TABLE_NAME + "(" + MovieContract.MovieEntry._ID + ")" +
                        ");";

        /*
        * CREATE TABLE review (
        * review_id INTEGER PRIMARY KEY AUTOINCREMENT,
        * movie_id INTEGER NOT NULL,
        * review_url TEXT NOT NULL,
        * FOREIGN KEY (movie_id) REFERENCES movie(movie_id)
        * );
        * */
        final String SQL_CREATE_REVIEW_TABLE =
                "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME +"("  +
                        MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        MovieContract.ReviewEntry.COLUMN_REVIEW_URL + " TEXT NOT NULL, " +
                        "FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + ") " +
                        "REFERENCES " + MovieContract.MovieEntry.TABLE_NAME + "(" + MovieContract.MovieEntry._ID + ")" +
                        ");";


        //Create the tables in SQLite
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOST_POPULAR_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TOP_RATED_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TopRatedEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MostPopularEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
