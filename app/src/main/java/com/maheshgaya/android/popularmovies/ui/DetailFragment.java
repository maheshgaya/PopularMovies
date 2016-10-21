package com.maheshgaya.android.popularmovies.ui;

/**
 * Created by Mahesh Gaya on 10/6/16.
 */

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maheshgaya.android.popularmovies.R;
import com.maheshgaya.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * DetailFragment
 * Deals with showing the information about the movie on a fragment
 * */

public class DetailFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private String mMovieStr;
    private static final int DETAIL_LOADER = 0;

    //these will make the connection to fragment_detail.xml
    TextView titleTextView;
    ImageView thumbnailImageView;
    TextView releaseDateTextView;
    TextView plotTextView;
    TextView ratingsTextView;

    //Projection
    private static final  String[] MOVIE_COLUMNS_PROJECTION = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_API_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_IMAGE_URL,
            MovieContract.MovieEntry.COLUMN_PLOT,
            MovieContract.MovieEntry.COLUMN_RATINGS,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE
    };

    static final int COLUMN_MOVIE_ID = 0;
    static final int COLUMN_MOVIE_API_ID = 1;
    static final int COLUMN_MOVIE_TITLE = 2;
    static final int COLUMN_MOVIE_IMAGE_URL = 3;
    static final int COLUMN_MOVIE_PLOT = 4;
    static final int COLUMN_MOVIE_RATINGS = 5;
    static final int COLUMN_MOVIE_RELEASE_DATE = 6;


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
            //Log.d(LOG_TAG, "onCreateView: " + mMovieStr);

        }

        return rootView;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null){
            return null;
        }
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                MOVIE_COLUMNS_PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()){
            //set title
            String title = data.getString(COLUMN_MOVIE_TITLE);
            titleTextView.setText(title);

            //image
            String imageUrl = data.getString(COLUMN_MOVIE_IMAGE_URL);
            Picasso
                    .with(getContext())
                    .load(imageUrl)
                    .error(R.drawable.ic_photo_placeholder)
                    .into(thumbnailImageView);

            //plot
            String plot = data.getString(COLUMN_MOVIE_PLOT);
            plotTextView.setText(plot);

            //ratings
            double ratings = data.getDouble(COLUMN_MOVIE_RATINGS);
            String ratingsStr = ratings + "/10";
            ratingsTextView.setText(ratingsStr);

            //ReleaseDate
            String releaseDate = data.getString(COLUMN_MOVIE_RELEASE_DATE);
            CharSequence year = releaseDate.subSequence(0,4); //get year only
            releaseDateTextView.setText(year);

        }
    }


    @Override
    public void onLoaderReset(Loader loader) {

    }
}