package com.maheshgaya.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Copyright (c) Mahesh Gaya
 * Author: Mahesh Gaya
 * Date: 9/17/16.
 */

/**
 * MovieFragment
 * Deals with getting data from TheMovieDB in JSON
 * and updating the gridview for showing the posters/thumbnails
 */
public class MovieFragment extends Fragment {

    private FetchMovieTask fetchMovieTask;

    private static final String TAG = MovieFragment.class.getSimpleName(); //logging purposes
    private MovieAdapter mMovieAdapter; //for managing the gridview
    private GridView mGridView;
    private Movie[] mMovies = {}; //array of movies

    /**
     * MovieFragment Constructor
     * initialize the Fragment
     */
    public MovieFragment(){
        //empty constructor
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Constant.INSTANCE_STATE_MOVIES_ARRAY)) {
                Parcelable[] savedArray = savedInstanceState.getParcelableArray(Constant.INSTANCE_STATE_MOVIES_ARRAY);
                try{
                    mMovies = new Movie[savedArray.length];
                    System.arraycopy(savedArray, 0, mMovies, 0, savedArray.length );
                }catch (java.lang.NullPointerException e){
                    Log.e(TAG, "onCreate: ",e );
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(Constant.INSTANCE_STATE_MOVIES_ARRAY, mMovies);
        super.onSaveInstanceState(outState);
    }

    /**
     * onOptionsItemSelected
     * @param item
     * @return item selected
     * does nothing useful currently
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /**
     * updateMovie
     * get data from asynctask and update the gridview
     */
    public void updateMovie(){
        //will get movies according to sort preference
        //default is Most Popular (popular)
        fetchMovieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortPref = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        fetchMovieTask.execute(sortPref);


    }


    /**
     * onStart
     * upon start fragment get data from TheMovieDB and update the gridview
     */
    @Override
    public void onStart() {
        super.onStart();
        // sort order has changed or the movies list is empty
        if(mMovies.length == 0){
            // fetch the movie list
            updateMovie();
        } else {
            // do nothing
        }

    }

    /**
     * onCreateView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return rootView
     * Initialize the views
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Log.d(TAG, "onCreateView: method executed");
        mMovieAdapter = new MovieAdapter(getActivity(), mMovies);
        //Log.d(TAG, "onCreateView: " + mMovieAdapter);

        // Get a reference to the GridView, and attach this adapter to it.
        mGridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        mGridView.setAdapter(mMovieAdapter);


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Movie movie = mMovieAdapter.getItem(position);
                //get more details about the particular movie
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Constant.EXTRA_MOVIE_PARCELABLE, movie);
                startActivity(intent);
            }
        });


        return rootView;
    }



    /**
     * FetchMovieTask
     * fetch movie data using async task
     */
    //AsyncTask<params, progress, result>
    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]>{

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName(); //logging purposes

        /**
         * doInBackground
         * @param params
         * @return movies as array
         * will fetch data from The Movie DB API
         */
        @Override
        protected Movie[] doInBackground(String... params) {
            Log.d(TAG, "doInBackground: method executed");

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

            //get the data from TheMovieDB
            try{
                
                //build url
                final String MOVIE_POPULAR_BASE_URL = "http://api.themoviedb.org/3/movie/popular?";
                final String MOVIE_TOP_RATED_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?";
                final String APPID_PARAM = "api_key";

                String baseUrl = MOVIE_POPULAR_BASE_URL; //default value

                //check the preference
                if (params[0].equals(getResources().getString(R.string.pref_popular))){
                    baseUrl = MOVIE_POPULAR_BASE_URL;
                } else if (params[0].equals(getResources().getString(R.string.pref_top_rated))){
                    baseUrl = MOVIE_TOP_RATED_BASE_URL;
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
                return getMovieDatafromJson(movieJsonStr);
            }catch (JSONException e){
                Log.e(TAG, "doInBackground: ", e );
            }

            return  null; //nothing happened
        }

        /**
         * isOnline
         * @return internet connectivity status of device
         */
        public boolean isOnline(){
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
                    mMovieAdapter = new MovieAdapter(getContext(), result);
                    //Log.d(TAG, "onPostExecute: " + mMovieAdapter.getCount());
                    mMovieAdapter.notifyDataSetChanged();
                    mGridView.setAdapter(mMovieAdapter);
                } catch (UnsupportedOperationException e) {
                    Log.e(TAG, "onPostExecute: " + e.getMessage(), e);
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
        private Movie[] getMovieDatafromJson(String movieJsonStr)
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
                String movieTitle = movie.getString(Constant.TMDB_TITLE);
                //Log.d(TAG, "getMovieDatafromJson: " + movieTitle);
                String movieThumbnailURL = movie.getString(Constant.TMDB_THUMBNAIL_URL);
                String moviePlot = movie.getString(Constant.TMDB_PLOT);
                double movieRatings = movie.getDouble(Constant.TMDB_RATINGS);
                String movieReleaseDate = movie.getString(Constant.TMDB_RELEASE_DATE);

                movies[i] = new Movie(movieTitle, movieThumbnailURL, moviePlot, movieRatings, movieReleaseDate);
                //Log.d(TAG, "getMovieDatafromJson: " + mMovies[i]);
            }
            return movies;
        }

    }
}
