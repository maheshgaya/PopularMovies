package com.maheshgaya.android.popularmovies;

/**
 * Created by Mahesh Gaya on 10/17/16.
 */

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;

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
 * FetchMovieTask
 * fetch movie data using async task
 */
//AsyncTask<params, progress, result>
public class FetchMovieTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName(); //logging purposes
    private Context mContext;
    private final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final String APPID_PARAM = "api_key";

    private static final String POPULAR_MOVIE_RANKING = "popular";
    private static final String TOP_RATED_MOVIE_RANKING = "top_rated";

    public FetchMovieTask(Context context){
        this.mContext = context;
    }

    long addMovie(int movieApiId, String title, String imageUrl, String plot, double ratings, String releaseDate){
        long movieId;
        //First, query database and check if record already exists
        Cursor movieCursor = mContext.getContentResolver().query(
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
                Uri insertUri = mContext.getContentResolver().insert(
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

    long addMovieToMostPopular(int movieId){
        long mostPopularId;
        //query table to see if the movie already has a record
        Cursor mostPopularCursor = mContext.getContentResolver().query(
                MovieContract.MostPopularEntry.CONTENT_URI,
                new String[]{MovieContract.MostPopularEntry._ID},
                MovieContract.MostPopularEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{Integer.toString(movieId)},
                null
        );
        try {
            if (mostPopularCursor.moveToFirst()){
                int mostPopularIndex = mostPopularCursor.getColumnIndex(MovieContract.MostPopularEntry._ID);
                mostPopularId = mostPopularCursor.getLong(mostPopularIndex);
            } else {
                ContentValues mostPopularValues = new ContentValues();
                mostPopularValues.put(MovieContract.MostPopularEntry.COLUMN_MOVIE_ID, movieId);
                //Then add most popular to database
                Uri insertUri = mContext.getContentResolver().insert(
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

    long addMovieToTopRated(int movieId){
        long topRatedId;
        //query table to see if the movie already has a record
        Cursor topRatedCursor = mContext.getContentResolver().query(
                MovieContract.TopRatedEntry.CONTENT_URI,
                new String[]{MovieContract.TopRatedEntry._ID},
                MovieContract.TopRatedEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{Integer.toString(movieId)},
                null
        );

        try {
            if (topRatedCursor.moveToFirst()){
                int topRatedIndex = topRatedCursor.getColumnIndex(MovieContract.TopRatedEntry._ID);
                topRatedId = topRatedCursor.getLong(topRatedIndex);
            } else {
                ContentValues topRatedValues = new ContentValues();
                topRatedValues.put(MovieContract.TopRatedEntry.COLUMN_MOVIE_ID, movieId);
                //Then add top rated to database
                Uri insertUri = mContext.getContentResolver().insert(
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


    /**
     * doInBackground
     * @param params
     * @return movies as array
     * will fetch data from The Movie DB API
     */
    @Override
    protected Void doInBackground(String... params) {
        //Log.d(LOG_TAG, "doInBackground: method executed");

        //check if device is connected to internet
        //      if not just show blank fragment
        if (isOnline() == false){
            //Log.d(TAG, "doInBackground: isOnline(): " + isOnline());
            return null;
        }

        //if params are empty, return null
        if (params.length == 0) {
            //Log.d(TAG, "doInBackground: empty params");
            return null;
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
            if (params[0].equals(mContext.getResources().getString(R.string.pref_popular))){
                baseUrl = MOVIE_POPULAR_BASE_URL;
                movieRanking = POPULAR_MOVIE_RANKING;
            } else if (params[0].equals(mContext.getResources().getString(R.string.pref_top_rated))){
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
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();

            //if stream empty, return null
            if (inputStream == null){
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            //read line by line
            String line;
            while ((line = reader.readLine()) != null){
                stringBuffer.append(line + "\n");
            }

            //if buffer empty, return null
            if (stringBuffer.length() == 0){
                return null;
            }

            //Getting data is successful
            movieJsonStr = stringBuffer.toString();
            //Log.d(TAG, "doInBackground: " + movieJsonStr);

        } catch (IOException e){
            Log.e(LOG_TAG, "Error ", e);
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
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try{
            getMovieDatafromJson(movieJsonStr, movieRanking);
        } catch (JSONException e){
            Log.e(LOG_TAG, "doInBackground: ", e );
        }

        return null; //nothing happened
    }

    /**
     * isOnline
     * @return internet connectivity status of device
     */
    public boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }






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

        //create an array of all the movies from the JSON
        Movie[] movies = new Movie[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++){

            JSONObject movie = movieArray.getJSONObject(i);
            int movieApiId = movie.getInt(Constant.TMDB_ID);
            String movieTitle = movie.getString(Constant.TMDB_TITLE);
            String movieThumbnailURL = movie.getString(Constant.TMDB_THUMBNAIL_URL);
            movieThumbnailURL = BASE_IMAGE_URL + movieThumbnailURL;
            String moviePlot = movie.getString(Constant.TMDB_PLOT);
            double movieRatings = movie.getDouble(Constant.TMDB_RATINGS);
            String movieReleaseDate = movie.getString(Constant.TMDB_RELEASE_DATE);

            //add movie to movie table
            long movieId = addMovie(movieApiId, movieTitle, movieThumbnailURL, moviePlot, movieRatings, movieReleaseDate);
            //then add to top_rated or most_popular table based on

        }
    }

    private boolean getMovieReview(int movieApiId){
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
            Log.e(LOG_TAG, "Error ", e);
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
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
          //TODO: read review and add to database in bulk
        } catch (JSONException e){
            Log.e(LOG_TAG, "doInBackground: ", e );
        }

        return true;

    }

    private boolean getMovieTrailer(int movieApiId){
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
            trailerJsonStr = stringBuffer.toString();
        } catch (IOException e){
            Log.e(LOG_TAG, "Error ", e);
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
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            //TODO: read trailer and add to database in bulk
        } catch (JSONException e){
            Log.e(LOG_TAG, "doInBackground: ", e );
        }
        return true;
    }

}