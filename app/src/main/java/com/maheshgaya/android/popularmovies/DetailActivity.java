package com.maheshgaya.android.popularmovies;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.RelativeDateTimeFormatter;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;


/**
 * Copyright (c) Mahesh Gaya
 * Author: Mahesh Gaya
 * Date: 9/18/16.
 */

/**
 * This class deals with showing the details for each individual movie.
 * If user tap on the poster, this class will be executed.
 * It will show information about the movie
 * */

public class DetailActivity extends AppCompatActivity{
    private static final String  TAG = DetailActivity.class.getSimpleName();

    /**
     * onCreate Method
     * @param savedInstanceState
     * Functionality: initialize the fragment layout for DetailFragment
     * */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle(getResources().getString(R.string.movie_detail));
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_detail, new DetailFragment())
                    .commit();
        }
    }

    /**
     * onCreateOptionsMenu
     * @param menu
     * inflate the menu for this activity
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    /**
     * onOptionsItemSelected
     * @param item
     * check if action_settings is tapped
     *      opens settings activity if it is the case
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this.getApplicationContext(), SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * DetailFragment
     * Deals with show the information about the movie on a fragment
     * */

    public static class DetailFragment extends Fragment {
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        //these will make the connection to fragment_detail.xml
        TextView titleTextView;
        ImageView thumbnailImageView;
        TextView releaseDateTextView;
        TextView plotTextView;
        TextView ratingsTextView;

        /**
         * onCreateView
         * @param inflater
         * @param container
         * @param savedInstanceState
         * This deals with creating the view for the DetailFragment
         * It assigns each fields in the xml with the appropriate information
         * */
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();

            //initialize views
            titleTextView = (TextView)rootView.findViewById(R.id.detail_title_text_view);
            thumbnailImageView = (ImageView)rootView.findViewById(R.id.detail_thumbnail_image_view);
            releaseDateTextView = (TextView)rootView.findViewById(R.id.detail_release_date_text_view);
            plotTextView = (TextView)rootView.findViewById(R.id.detail_plot_text_view);
            ratingsTextView = (TextView)rootView.findViewById(R.id.detail_ratings_text_view);

            //get data from intent and populate the views
            if (intent != null){
                if (intent.hasExtra(Constant.EXTRA_MOVIE_PARCELABLE)){
                    Movie currentMovie = (Movie)intent.getParcelableExtra(Constant.EXTRA_MOVIE_PARCELABLE);
                    //Log.d(LOG_TAG, "onCreateView: " + currentMovie.toString());

                    titleTextView.setText(currentMovie.getTitle());
                    Picasso
                            .with(getContext())
                            .load(currentMovie.getThumbnailURL())
                            .error(R.drawable.ic_photo_placeholder)
                            .into(thumbnailImageView);
                    plotTextView.setText(currentMovie.getPlot());
                    ratingsTextView.setText(currentMovie.getRatings() + "/10");
                    CharSequence year = currentMovie.getReleaseDate().subSequence(0,4);
                    releaseDateTextView.setText(year);

                }
            }

            return rootView;
        }
    }
}
