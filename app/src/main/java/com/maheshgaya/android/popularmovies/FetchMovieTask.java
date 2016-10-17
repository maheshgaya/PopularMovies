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
public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName(); //logging purposes
    private MovieAdapter mMovieAdapter;
    private Movie[] mMovies;
    private GridView mGridView;
    private Context mContext;

    private static final String POPULAR_MOVIE_RANKING = "popular";
    private static final String TOP_RATED_MOVIE_RANKING = "top_rated";

    public FetchMovieTask(Context context, MovieAdapter movieAdapter, GridView gridView){
        this.mContext = context;
        this.mMovieAdapter = movieAdapter;
        this.mGridView = gridView;
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


    /**
     * doInBackground
     * @param params
     * @return movies as array
     * will fetch data from The Movie DB API
     */
    @Override
    protected Movie[] doInBackground(String... params) {
        Log.d(LOG_TAG, "doInBackground: method executed");

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
            final String MOVIE_POPULAR_BASE_URL = "http://api.themoviedb.org/3/movie/popular?";
            final String MOVIE_TOP_RATED_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?";
            final String APPID_PARAM = "api_key";

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
                return  null;
            }

            //Getting data is successful
            movieJsonStr = stringBuffer.toString();
            //Log.d(TAG, "doInBackground: " + movieJsonStr);

        } catch (IOException e){
            Log.e(LOG_TAG, "Error ", e);
        }finally {
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

        //return movies as array based on the JSON
        try{
            return getMovieDatafromJson(movieJsonStr, movieRanking);
        }catch (JSONException e){
            Log.e(LOG_TAG, "doInBackground: ", e );
        }

        return  null; //nothing happened
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
     * onPostExecute
     * @param result
     * update the adapter and the gridview
     * based on new data
     */
    @Override
    protected void onPostExecute(Movie[] result) {

        if (result != null) {
            mMovies = result;
            try {
                //Log.d(TAG, "onPostExecute: " + mMovies.length);
                mMovieAdapter = new MovieAdapter(mContext, result);
                //Log.d(TAG, "onPostExecute: " + mMovieAdapter.getCount());
                mMovieAdapter.notifyDataSetChanged();
                mGridView.setAdapter(mMovieAdapter);
            } catch (UnsupportedOperationException e) {
                Log.e(LOG_TAG, "onPostExecute: " + e.getMessage(), e);
                e.printStackTrace();
            }

        }




    }

    /**
     * getMovieDatafromJson
     * @param movieJsonStr
     * @return movies as array
     * @throws JSONException
     * only get what is needed. Then make an array of movies
     */
    private Movie[] getMovieDatafromJson(String movieJsonStr, String movieRanking)
            throws JSONException{

        //get the useful information from the JSON
        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(Constant.TMDB_RESULTS);
        //Log.d(TAG, "getMovieDatafromJson: " + movieArray.toString());

        //create an array of all the movies from the JSON
        Movie[] movies = new Movie[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++){

            JSONObject movie = movieArray.getJSONObject(i);
            //Log.d(TAG, "getMovieDatafromJson: " + movie);
            int movieApiId = movie.getInt(Constant.TMDB_ID);
            String movieTitle = movie.getString(Constant.TMDB_TITLE);
            //Log.d(TAG, "getMovieDatafromJson: " + movieTitle);
            String movieThumbnailURL = movie.getString(Constant.TMDB_THUMBNAIL_URL);
            String moviePlot = movie.getString(Constant.TMDB_PLOT);
            double movieRatings = movie.getDouble(Constant.TMDB_RATINGS);
            String movieReleaseDate = movie.getString(Constant.TMDB_RELEASE_DATE);
            //TODO: Bulk Insert for movies, Top Rated movies, and Most popular movies
            long movieId = addMovie(movieApiId, movieTitle, movieThumbnailURL, moviePlot, movieRatings, movieReleaseDate);
            //TODO: Check for movieRanking then add to database
            //TODO: Show movies based on User's sharedPreferences
            //TODO: onPostExecute not needed so clean it up and add Database call in MovieFragment using loaders
            movies[i] = new Movie(movieApiId, movieTitle, movieThumbnailURL, moviePlot, movieRatings, movieReleaseDate);
            //Log.d(TAG, "getMovieDatafromJson: " + mMovies[i]);
        }
        return movies;
    }

}