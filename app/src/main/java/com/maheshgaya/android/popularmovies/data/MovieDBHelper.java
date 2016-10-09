package com.maheshgaya.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mahesh Gaya on 10/6/16.
 */
public class MovieDBHelper extends SQLiteOpenHelper {
    public static final String TAG = MovieDBHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "movies.db";
    public static final int DATABASE_VERSION = 1;

    public MovieDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*
        * CREATE TABLE movie (
        * movie_id INTEGER NOT NULL PRIMARY KEY,
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
                        MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_IMAGE_PATH + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_RATINGS + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        ");";
        /*
        * CREATE TABLE trailer (
        * trailer_id INTEGER PRIMARY KEY AUTOINCREMENT,
        * movie_id INTEGER NOT NULL,
        * youtube_key TEXT NOT NULL,
        * FOREIGN KEY (movie_id) REFERENCES movie(movie_id)
        * );
        * */
        final String SQL_CREATE_TRAILER_TABLE =
                "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME +"("  +
                        MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        MovieContract.TrailerEntry.COLUMN_TRAILER_KEY + " TEXT NOT NULL, " +
                        "FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + ") " +
                        "REFERENCES " + MovieContract.MovieEntry.TABLE_NAME + "(" + MovieContract.MovieEntry._ID + ")" +
                        ");";

        /*
        * CREATE TABLE review (
        * review_id INTEGER PRIMARY KEY AUTOINCREMENT,
        * movie_id INTEGER NOT NULL,
        * url TEXT NOT NULL,
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
                        ")";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
