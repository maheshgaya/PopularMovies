package com.maheshgaya.android.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.maheshgaya.android.popularmovies.BuildConfig;
import com.maheshgaya.android.popularmovies.Constant;
import com.maheshgaya.android.popularmovies.R;
import com.maheshgaya.android.popularmovies.Utility;
import com.maheshgaya.android.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mahesh Gaya on 10/22/16.
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = MovieSyncAdapter.class.getSimpleName();

    private final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final String APPID_PARAM = "api_key";

    private static final String POPULAR_MOVIE_RANKING = "popular";
    private static final String TOP_RATED_MOVIE_RANKING = "top_rated";

    //in seconds
    //60 seconds * 60 minutes * 24 hours
    public static final int SYNC_INTERVAL = 60 * 60 * 24;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    private int mMoviesNumber = 0;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }
    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync Called.");
        String sortPref = Utility.getSortPreference(getContext());
        mMoviesNumber = 0;
        //check if device is connected to internet
        //      if not just show blank fragment
        if (!isOnline()){
            //Log.d(TAG, "doInBackground: isOnline(): " + isOnline());
            return;
        }

        //if params are empty, return null
        if (sortPref.equals("")) {
            //Log.d(TAG, "doInBackground: empty params");
            return;
        }


        //initializing
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null;

        String movieRanking = POPULAR_MOVIE_RANKING; //default

        //get the data from TheMovieDB
        try{

            //build url
            final String MOVIE_POPULAR_BASE_URL = BASE_URL + "popular?";
            final String MOVIE_TOP_RATED_BASE_URL = BASE_URL + "top_rated?";

            String baseUrl = MOVIE_POPULAR_BASE_URL; //default value

            //check the preference
            if (sortPref.equals(getContext().getResources().getString(R.string.pref_popular))){
                baseUrl = MOVIE_POPULAR_BASE_URL;
                movieRanking = POPULAR_MOVIE_RANKING;
            } else if (sortPref.equals(getContext().getResources().getString(R.string.pref_top_rated))){
                baseUrl = MOVIE_TOP_RATED_BASE_URL;
                movieRanking = TOP_RATED_MOVIE_RANKING;
            }

            //build the whole url and parse it
            Uri builtUri = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();
            //Make connection to api
            URL url = new URL(builtUri.toString());
            //make connection to the api and GET the data
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            //read data(input stream) to string
            InputStream inputStream = null;
            try{
                inputStream = urlConnection.getInputStream();
            }catch (java.io.FileNotFoundException e){
                Log.e(TAG, "onPerformSync: ", e);
            }
            StringBuffer stringBuffer = new StringBuffer();

            //if stream empty, return null
            if (inputStream == null){
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            //read line by line
            String line;
            while ((line = reader.readLine()) != null){
                stringBuffer.append(line + "\n");
            }

            //if buffer empty, return null
            if (stringBuffer.length() == 0){
                return;
            }

            //Getting data is successful
            movieJsonStr = stringBuffer.toString();
            //Log.d(TAG, "doInBackground: " + movieJsonStr);

        } catch (IOException e){
            Log.e(TAG, "Error ", e);
        } finally {
            //disconnect if cannot make connection
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            //close reader if it null
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        try{
            getMovieDatafromJson(movieJsonStr, movieRanking);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
                    .setSmallIcon(R.drawable.ic_logo)
                    .setContentTitle(getContext().getString(R.string.app_name))
                    .setContentText(mMoviesNumber + " " + getContext().getString(R.string.movies_notification));

            NotificationManager mNotificationManager =
                    (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
            mNotificationManager.notify(111, builder.build());

        } catch (JSONException e){
            Log.e(TAG, "doInBackground: ", e );
        }

        return; //nothing happened

    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        Log.d(TAG, "onAccountCreated: account is just created");
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Log.d(TAG, "syncImmediately: syncing data");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            Log.d(TAG, "getSyncAccount: account created");
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }


    /*******************
     * Add to Database
     *******************/

    /**
     * long addMovie
     * @param movieApiId
     * @param title
     * @param imageUrl
     * @param plot
     * @param ratings
     * @param releaseDate
     * @return movieId
     */

    long addMovie(int movieApiId, String title, String imageUrl, String plot, double ratings, String releaseDate){
        long movieId;
        //First, query database and check if record already exists
        Cursor movieCursor = getContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_API_ID + " = ? ",
                new String[]{Integer.toString(movieApiId)},
                null
        );

        try {
            if (movieCursor.moveToFirst()) {
                //if record exists, return that record
                int movieIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
                movieId = movieCursor.getLong(movieIdIndex);
            } else {
                //insert the record into database
                //Create contentValue to hold the data
                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_API_ID, movieApiId);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_URL, imageUrl);
                movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT, plot);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RATINGS, ratings);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);

                //Then add movie to database
                Uri insertUri = getContext().getContentResolver().insert(
                        MovieContract.MovieEntry.CONTENT_URI,
                        movieValues
                );
                movieId = ContentUris.parseId(insertUri);
            }
        } finally {
            movieCursor.close();
        }

        return  movieId;
    }

    /**
     * long addMovieToMostPopular
     * @param movieId
     * @return mostPopularId
     */
    long addMovieToMostPopular(long movieId){
        long mostPopularId;
        //query table to see if the movie already has a record
        Cursor mostPopularCursor = getContext().getContentResolver().query(
                MovieContract.MostPopularEntry.CONTENT_URI,
                new String[]{MovieContract.MostPopularEntry._ID},
                MovieContract.MostPopularEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{Long.toString(movieId)},
                null
        );
        try {
            if (mostPopularCursor.moveToFirst()){
                int mostPopularIndex = mostPopularCursor.getColumnIndex(MovieContract.MostPopularEntry._ID);
                mostPopularId = mostPopularCursor.getLong(mostPopularIndex);
                ContentValues updateValues = new ContentValues();
                updateValues.put(MovieContract.MostPopularEntry.COLUMN_CURRENT_MOVIE, 1);
                //Update current movie to 1
                int updateUri = getContext().getContentResolver().update(
                        MovieContract.MostPopularEntry.CONTENT_URI,
                        updateValues,
                        MovieContract.MostPopularEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{Long.toString(movieId)}
                );
            } else {
                ContentValues mostPopularValues = new ContentValues();
                mostPopularValues.put(MovieContract.MostPopularEntry.COLUMN_MOVIE_ID, movieId);
                mostPopularValues.put(MovieContract.MostPopularEntry.COLUMN_CURRENT_MOVIE, 1);
                //Then add most popular to database
                Uri insertUri = getContext().getContentResolver().insert(
                        MovieContract.MostPopularEntry.CONTENT_URI,
                        mostPopularValues
                );
                mostPopularId = ContentUris.parseId(insertUri);
            }
        } finally {
            mostPopularCursor.close();
        }

        return mostPopularId;
    }

    /**
     * long addMovieToTopRated
     * @param movieId
     * @return topRatedId
     */
    long addMovieToTopRated(long movieId){
        long topRatedId;
        //query table to see if the movie already has a record
        Cursor topRatedCursor = getContext().getContentResolver().query(
                MovieContract.TopRatedEntry.CONTENT_URI,
                new String[]{MovieContract.TopRatedEntry._ID},
                MovieContract.TopRatedEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{Long.toString(movieId)},
                null
        );

        try {
            if (topRatedCursor.moveToFirst()){
                int topRatedIndex = topRatedCursor.getColumnIndex(MovieContract.TopRatedEntry._ID);
                topRatedId = topRatedCursor.getLong(topRatedIndex);
                ContentValues updateValues = new ContentValues();
                updateValues.put(MovieContract.TopRatedEntry.COLUMN_CURRENT_MOVIE, 1);
                //Update current movie to 1
                int updateUri = getContext().getContentResolver().update(
                        MovieContract.TopRatedEntry.CONTENT_URI,
                        updateValues,
                        MovieContract.TopRatedEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{Long.toString(movieId)}
                );
            } else {
                ContentValues topRatedValues = new ContentValues();
                topRatedValues.put(MovieContract.TopRatedEntry.COLUMN_MOVIE_ID, movieId);
                topRatedValues.put(MovieContract.TopRatedEntry.COLUMN_CURRENT_MOVIE, 1);
                //Then add top rated to database
                Uri insertUri = getContext().getContentResolver().insert(
                        MovieContract.TopRatedEntry.CONTENT_URI,
                        topRatedValues
                );
                topRatedId = ContentUris.parseId(insertUri);
            }
        } finally {
            topRatedCursor.close();
        }
        return topRatedId;

    }

    long addReview(long movieId, String reviewUrl){
        long reviewId;
        //query table to see if review already exists
        Cursor reviewCursor = getContext().getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                new String[]{MovieContract.ReviewEntry._ID},
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? AND " +
                        MovieContract.ReviewEntry.COLUMN_REVIEW_URL + " = ?",
                new String[]{ Long.toString(movieId), reviewUrl},
                null
        );

        try {
            if (reviewCursor.moveToFirst()){
                int reviewIndex = reviewCursor.getColumnIndex(MovieContract.ReviewEntry._ID);
                reviewId = reviewCursor.getLong(reviewIndex);
            } else {
                ContentValues reviewValues = new ContentValues();
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL, reviewUrl);
                Uri insertUri = getContext().getContentResolver().insert(
                        MovieContract.ReviewEntry.CONTENT_URI,
                        reviewValues
                );
                reviewId = ContentUris.parseId(insertUri);
            }
        } finally {
            reviewCursor.close();
        }
        return  reviewId;
    }

    long addTrailer(long movieId, String trailerUrl){
        long trailerId;
        Cursor trailerCursor = getContext().getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                new String[]{MovieContract.TrailerEntry._ID},
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? AND " +
                        MovieContract.TrailerEntry.COLUMN_TRAILER_URL + " = ? ",
                new String[]{Long.toString(movieId), trailerUrl},
                null
        );
        try {
            if (trailerCursor.moveToFirst()){
                int trailerIndex = trailerCursor.getColumnIndex(MovieContract.TrailerEntry._ID);
                trailerId = trailerCursor.getLong(trailerIndex);
            } else {
                ContentValues trailerValues = new ContentValues();
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_URL, trailerUrl);
                Uri insertUri = getContext().getContentResolver().insert(
                        MovieContract.TrailerEntry.CONTENT_URI,
                        trailerValues
                );
                trailerId = ContentUris.parseId(insertUri);
            }
        } finally {
            trailerCursor.close();
        }
        return  trailerId;
    }

    /**
     * boolean getMovieReview
     * @param movieApiId
     * @return status
     */
    private boolean getMovieReview(int movieApiId, long movieId){
        final String MOVIE_REVIEW_URL = BASE_URL + Integer.toString(movieApiId) + "/reviews?";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String reviewJsonStr = null;

        try{
            //build the whole url and parse it
            Uri builtUri = Uri.parse(MOVIE_REVIEW_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();
            //Make connection to api
            URL url = new URL(builtUri.toString());
            //make connection to the api and GET the data
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            //read data(input stream) to string
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();

            //if stream empty, return null
            if (inputStream == null){
                return false;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            //read line by line
            String line;
            while ((line = reader.readLine()) != null){
                stringBuffer.append(line + "\n");
            }

            //if buffer empty, return null
            if (stringBuffer.length() == 0){
                return false;
            }

            //Getting data is successful
            reviewJsonStr = stringBuffer.toString();
        } catch (IOException e){
            Log.e(TAG, "Error ", e);
        } finally {
            //disconnect if cannot make connection
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            //close reader if it null
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        try {
            getMovieReviewDataFromJson(reviewJsonStr, movieId);
        } catch (JSONException e){
            Log.e(TAG, "doInBackground: ", e );
        }

        return true;

    }

    /**
     * boolean getMovieTrailer
     * @param movieApiId
     * @return status
     */
    private boolean getMovieTrailer(int movieApiId, long movieId){
        final String MOVIE_TRAILER_URL = BASE_URL + Integer.toString(movieApiId) + "/videos?";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String trailerJsonStr = null;
        try{
            //build the whole url and parse it
            Uri builtUri = Uri.parse(MOVIE_TRAILER_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();
            //Make connection to api
            URL url = new URL(builtUri.toString());
            //make connection to the api and GET the data
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            //read data(input stream) to string
            InputStream inputStream = null;
            try {
                inputStream = urlConnection.getInputStream();
            } catch (java.io.FileNotFoundException e){
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            StringBuffer stringBuffer = new StringBuffer();

            //if stream empty, return null
            if (inputStream == null){
                return false;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            //read line by line
            String line;
            while ((line = reader.readLine()) != null){
                stringBuffer.append(line + "\n");
            }

            //if buffer empty, return null
            if (stringBuffer.length() == 0){
                return false;
            }

            //Getting data is successful
            trailerJsonStr = stringBuffer.toString();
        } catch (IOException e){
            Log.e(TAG, "Error ", e);
        } finally {
            //disconnect if cannot make connection
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            //close reader if it null
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        try {
            getMovieTrailerDataFromJson(trailerJsonStr, movieId);
        } catch (JSONException e){
            Log.e(TAG, "doInBackground: ", e );
        }
        return true;
    }

    /**
     * isOnline
     * @return internet connectivity status of device
     */
    public boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }


    /************************
     * Read from Json data
     ************************/


    /**
     * getMovieDatafromJson
     * @param movieJsonStr
     * @return movies as array
     * @throws JSONException
     * only get what is needed. Then make an array of movies
     */
    private void getMovieDatafromJson(String movieJsonStr, String movieRanking)
            throws JSONException{
        final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";

        //get the useful information from the JSON
        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(Constant.TMDB_RESULTS);
        //Log.d(TAG, "getMovieDatafromJson: " + movieArray.toString());
        mMoviesNumber = movieArray.length();

        if (movieRanking.equals(POPULAR_MOVIE_RANKING)){
            //delete all Most Popular movies
            int deleteUri = getContext().getContentResolver().delete(
                    MovieContract.MostPopularEntry.CONTENT_URI,
                    null,
                    null
            );
            //Log.d(TAG, "delete all getMovieDatafromJson: " + deleteUri);
        } else {
            //delete all topRated movies
            int deleteUri = getContext().getContentResolver().delete(
                    MovieContract.TopRatedEntry.CONTENT_URI,
                    null,
                    null
            );
            //Log.d(TAG, "delete all getMovieDatafromJson: " + deleteUri);
        }




        for (int i = 0; i < movieArray.length(); i++){

            JSONObject movie = movieArray.getJSONObject(i);
            int movieApiId = movie.getInt(Constant.TMDB_ID);
            String movieTitle = movie.getString(Constant.TMDB_TITLE);
            String movieThumbnailURL = BASE_IMAGE_URL + movie.getString(Constant.TMDB_THUMBNAIL_URL);
            String moviePlot = movie.getString(Constant.TMDB_PLOT);
            double movieRatings = movie.getDouble(Constant.TMDB_RATINGS);
            String movieReleaseDate = movie.getString(Constant.TMDB_RELEASE_DATE);

            //add movie to movie table
            long movieId = addMovie(movieApiId, movieTitle, movieThumbnailURL, moviePlot, movieRatings, movieReleaseDate);
            if (movieRanking.equals(POPULAR_MOVIE_RANKING)){
                //add current movie
                long mostPopularMovieId = addMovieToMostPopular(movieId);
            } else {
                //add current movie
                long topRatedMovieId = addMovieToTopRated(movieId);
            }
            //then search for reviews and trailers
            getMovieReview(movieApiId, movieId);
            getMovieTrailer(movieApiId, movieId);
        }
    }

    private void getMovieReviewDataFromJson(String movieReviewJsonStr, long movieId)
            throws JSONException{
        try {
            if (movieReviewJsonStr != null) {
                JSONObject reviewJson = new JSONObject(movieReviewJsonStr);
                JSONArray reviewArray = reviewJson.getJSONArray(Constant.TMDB_REVIEW_RESULTS);
                //Log.d(LOG_TAG, "getMovieReviewDataFromJson: " + movieReviewJsonStr);
                if (reviewArray.length() != 0) {
                    for (int i = 0; i < reviewArray.length(); i++) {
                        JSONObject review = reviewArray.getJSONObject(i);
                        String reviewUrl = review.getString(Constant.TMDB_REVIEW_URL);
                        long reviewId = addReview(movieId, reviewUrl);
                        //Log.d(LOG_TAG, "getMovieReviewDataFromJson: " + review.toString());
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void getMovieTrailerDataFromJson(String movieTrailerJsonStr, long movieId)
            throws JSONException{
        final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

        //Log.d(LOG_TAG, "getMovieTrailerDataFromJson: " + movieTrailerJsonStr);
        try {
            if (movieTrailerJsonStr != null) {
                JSONObject trailerJson = new JSONObject(movieTrailerJsonStr);
                JSONArray trailerArray = trailerJson.getJSONArray(Constant.TMDB_TRAILER_RESULTS);
                //Log.d(LOG_TAG, "getMovieTrailerDataFromJson: " + movieTrailerJsonStr);
                if (trailerArray.length() != 0) {
                    for (int i = 0; i < trailerArray.length(); i++) {
                        JSONObject trailer = trailerArray.getJSONObject(i);
                        String trailerUrl = YOUTUBE_BASE_URL + trailer.getString(Constant.TMDB_TRAILER_KEY);
                        long trailerId = addTrailer(movieId, trailerUrl);
                        //Log.d(LOG_TAG, "getMovieTrailerDataFromJson: " + trailer.toString());
                    }

                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
