package com.maheshgaya.android.popularmovies.ui;

/**
 * Created by Mahesh Gaya on 10/6/16.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maheshgaya.android.popularmovies.Constant;

import com.maheshgaya.android.popularmovies.R;
import com.squareup.picasso.Picasso;

/**
 * DetailFragment
 * Deals with showing the information about the movie on a fragment
 * */

public class DetailFragment extends Fragment  implements LoaderManager.LoaderCallbacks{
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private String mMovieStr;

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
            mMovieStr = intent.getDataString();
            Log.d(LOG_TAG, "onCreateView: " + mMovieStr);

            /*
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
            */
        }

        return rootView;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}