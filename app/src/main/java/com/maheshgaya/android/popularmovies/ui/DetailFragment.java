package com.maheshgaya.android.popularmovies.ui;

/**
 * Created by Mahesh Gaya on 10/6/16.
 */

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.maheshgaya.android.popularmovies.R;
import com.maheshgaya.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * DetailFragment
 * Deals with showing the information about the movie on a fragment
 * */

public class DetailFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String POPULAR_MOVIES_HASH = " #PopularMovies";

    private static final int DETAIL_LOADER = 0;
    private static final int REVIEW_LOADER = 1;
    private static final int TRAILER_LOADER = 2;
    private static final int FAVORITE_LOADER = 3;

    private String mMovieTitle;
    private String mMovieFirstTrailerUrl;

    //these will make the connection to fragment_detail.xml
    TextView mTitleTextView;
    ImageView mThumbnailImageView;
    TextView mReleaseDateTextView;
    TextView mPlotTextView;
    TextView mRatingsTextView;
    Button mFavoriteButton;

    TextView mNoReviewTextView;
    TextView mNoTrailerTextView;



    //Adapters and recycleview
    private RecyclerView mTrailerRecycleView;
    private TrailerAdapter mTrailerAdapter;
    private RecyclerView mReviewRecycleView;
    private ReviewAdapter mReviewAdapter;


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

    //Movie Projection
    static final int COLUMN_MOVIE_ID = 0;
    static final int COLUMN_MOVIE_API_ID = 1;
    static final int COLUMN_MOVIE_TITLE = 2;
    static final int COLUMN_MOVIE_IMAGE_URL = 3;
    static final int COLUMN_MOVIE_PLOT = 4;
    static final int COLUMN_MOVIE_RATINGS = 5;
    static final int COLUMN_MOVIE_RELEASE_DATE = 6;

    private static final String[] FAVORITE_COLUMNS_PROJECTION = {
            MovieContract.FavoriteEntry.TABLE_NAME + "." + MovieContract.FavoriteEntry._ID,
            MovieContract.FavoriteEntry.COLUMN_MOVIE_ID
    };

    //Favorite Projection
    static final int COLUMN_FAVORITE_ID = 0;
    static final int COLUMN_FAVORITE_MOVIE_ID = 1;

    private static final String[] TRAILER_COLUMNS_PROJECTION = {
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_URL
    };

    //Trailer Projection
    static final int COLUMN_TRAILER_ID = 0;
    static final int COLUMN_TRAILER_MOVIE_ID = 1;
    static final int COLUMN_TRAILER_URL = 2;

    private static final String[] REVIEW_COLUMNS_PROJECTION = {
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_MOVIE_ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_URL
    };

    //Review Projection
    static final int COLUMN_REVIEW_ID = 0;
    static final int COLUMN_REVIEW_MOVIE_ID = 1;
    static final int COLUMN_REVIEW_URL = 2;

    private ShareActionProvider mShareActionProvider;
    public DetailFragment(){setHasOptionsMenu(true);}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mMovieTitle != null && mMovieFirstTrailerUrl != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    long addFavorite(int movieId){
        long favoriteId;
        //check if favorite is already in table
        Cursor favoriteCursor = getContext().getContentResolver().query(
                MovieContract.FavoriteEntry.CONTENT_URI,
                new String[]{MovieContract.FavoriteEntry._ID},
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{Integer.toString(movieId)},
                null
        );

        try {
            if (favoriteCursor.moveToFirst()){
                //if exists
                int favoriteIndex = favoriteCursor.getColumnIndex(MovieContract.FavoriteEntry._ID);
                favoriteId = favoriteCursor.getLong(favoriteIndex);
            } else {
                //else add
                ContentValues favoriteValues = new ContentValues();
                favoriteValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, movieId);
                Uri insertUri = getContext().getContentResolver().insert(
                        MovieContract.FavoriteEntry.CONTENT_URI,
                        favoriteValues
                );
                favoriteId = ContentUris.parseId(insertUri);
                getLoaderManager().restartLoader(FAVORITE_LOADER, null, this);
            }
        }finally {
            favoriteCursor.close();
        }
        return favoriteId;
    }

    boolean removeFavorite(int movieId){
        //check if movie exists in table
        Cursor favoriteCursor = getContext().getContentResolver().query(
                MovieContract.FavoriteEntry.CONTENT_URI,
                new String[]{MovieContract.FavoriteEntry._ID},
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{Integer.toString(movieId)},
                null
        );
        //then remove it
        try {
            if (favoriteCursor.moveToFirst()){
                getContext().getContentResolver().delete(
                        MovieContract.FavoriteEntry.CONTENT_URI,
                        MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{Integer.toString(movieId)}
                );
                getLoaderManager().restartLoader(FAVORITE_LOADER, null, this);
                return true;
            } else {
                return false;
            }
        } finally {
            favoriteCursor.close();
        }
    }

    private Intent createShareIntent(){
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Watch " + mMovieTitle + " " + mMovieFirstTrailerUrl + POPULAR_MOVIES_HASH);
        return shareIntent;
    }

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
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();

        //initialize views
        mTitleTextView = (TextView)rootView.findViewById(R.id.detail_title_text_view);
        mThumbnailImageView = (ImageView)rootView.findViewById(R.id.detail_thumbnail_image_view);
        mReleaseDateTextView = (TextView)rootView.findViewById(R.id.detail_release_date_text_view);
        mPlotTextView = (TextView)rootView.findViewById(R.id.detail_plot_text_view);
        mRatingsTextView = (TextView)rootView.findViewById(R.id.detail_ratings_text_view);

        //for no reviews
        mNoReviewTextView = (TextView)rootView.findViewById(R.id.empty_view_review);
        mNoTrailerTextView = (TextView) rootView.findViewById(R.id.empty_view_trailer);

        //buttons
        mFavoriteButton = (Button)rootView.findViewById(R.id.detail_favorite_button);

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int movieId = Integer.parseInt(getActivity().getIntent().getData().getLastPathSegment());
                String buttonText = mFavoriteButton.getText().toString();
                if (buttonText.equals(getString(R.string.unfavorite))){
                    //remove from db
                    boolean removeResult = removeFavorite(movieId);
                    mFavoriteButton.setText(getString(R.string.favorite));
                } else if (buttonText.equals(getString(R.string.favorite))) {
                    //add to db
                    long favoriteId = addFavorite(movieId);
                    mFavoriteButton.setText(getString(R.string.unfavorite));

                }
            }
        });


        //recycle views
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);

        mTrailerRecycleView = (RecyclerView) rootView.findViewById(R.id.recycleview_trailer);
        mTrailerRecycleView.setHasFixedSize(true);
        LinearLayoutManager linearTrailerLayoutManager = new LinearLayoutManager(getContext());
        linearTrailerLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTrailerRecycleView.setLayoutManager(linearTrailerLayoutManager);
        mTrailerRecycleView.setAdapter(mTrailerAdapter);
        mTrailerRecycleView.addItemDecoration(itemDecoration);

        mReviewRecycleView = (RecyclerView) rootView.findViewById(R.id.recycleview_review);
        mReviewRecycleView.setHasFixedSize(true);
        LinearLayoutManager linearReviewLayoutManager = new LinearLayoutManager(getContext());
        linearReviewLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mReviewRecycleView.setLayoutManager(linearReviewLayoutManager);
        mReviewRecycleView.setAdapter(mReviewAdapter);
        mReviewRecycleView.addItemDecoration(itemDecoration);

        return rootView;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }
        switch (id) {
            case DETAIL_LOADER: {
                return new CursorLoader(
                        getActivity(),
                        intent.getData(),
                        MOVIE_COLUMNS_PROJECTION,
                        null,
                        null,
                        null
                );
            }
            case REVIEW_LOADER:{
                int movieId = Integer.parseInt(intent.getData().getLastPathSegment()) ;
                Uri reviewUri = MovieContract.ReviewEntry.buildReviewMovie(movieId);
                return new CursorLoader(
                        getActivity(),
                        reviewUri,
                        REVIEW_COLUMNS_PROJECTION,
                        null,
                        null,
                        null
                );
            }
            case TRAILER_LOADER:{
                int movieId = Integer.parseInt(intent.getData().getLastPathSegment()) ;
                Uri trailerUri = MovieContract.TrailerEntry.buildTrailerMovie(movieId);

                return new CursorLoader(
                        getActivity(),
                        trailerUri,
                        TRAILER_COLUMNS_PROJECTION,
                        null,
                        new String[]{Integer.toString(movieId)},
                        null
                );
            }
            case FAVORITE_LOADER:{
                int movieId = Integer.parseInt(intent.getData().getLastPathSegment()) ;
                Uri favoriteUri = MovieContract.FavoriteEntry.CONTENT_URI;

                return new CursorLoader(
                        getActivity(),
                        favoriteUri,
                        FAVORITE_COLUMNS_PROJECTION,
                        MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Integer.toString(movieId)},
                        null
                );
            }
            default:{
                return null;
            }

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case DETAIL_LOADER: {
                if (data != null && data.moveToFirst()) {
                    //set title
                    String title = data.getString(COLUMN_MOVIE_TITLE);
                    mMovieTitle = title;
                    mTitleTextView.setText(title);

                    //image
                    String imageUrl = data.getString(COLUMN_MOVIE_IMAGE_URL);
                    Picasso
                            .with(getContext())
                            .load(imageUrl)
                            .error(R.drawable.ic_photo_placeholder)
                            .into(mThumbnailImageView);

                    //plot
                    String plot = data.getString(COLUMN_MOVIE_PLOT);
                    mPlotTextView.setText(plot);

                    //ratings
                    double ratings = data.getDouble(COLUMN_MOVIE_RATINGS);
                    String ratingsStr = ratings + "/10";
                    mRatingsTextView.setText(ratingsStr);

                    //ReleaseDate
                    String releaseDate = data.getString(COLUMN_MOVIE_RELEASE_DATE);
                    CharSequence year = releaseDate.subSequence(0, 4); //get year only
                    mReleaseDateTextView.setText(year);


                }
                break;
            }
            case REVIEW_LOADER:{
                if (data.getCount() > 0 && data.moveToFirst()){
                    mReviewRecycleView.setVisibility(View.VISIBLE);
                    mNoReviewTextView.setVisibility(View.GONE);
                    mReviewAdapter = new ReviewAdapter(getContext(), data);
                    mReviewRecycleView.setAdapter(mReviewAdapter);
                } else {
                    mReviewRecycleView.setVisibility(View.GONE);
                    mNoReviewTextView.setVisibility(View.VISIBLE);
                }
                break;
            }
            case TRAILER_LOADER:{

                if (data.getCount() > 0  && data.moveToFirst()){
                    mTrailerRecycleView.setVisibility(View.VISIBLE);
                    mNoTrailerTextView.setVisibility(View.GONE);
                    mTrailerAdapter = new TrailerAdapter(getContext(), data);
                    mTrailerRecycleView.setAdapter(mTrailerAdapter);
                    // If onCreateOptionsMenu has already happened, we need to update the share intent now.
                    if (mShareActionProvider != null) {
                        data.moveToFirst();
                        mMovieFirstTrailerUrl = data.getString(COLUMN_TRAILER_URL);
                        mShareActionProvider.setShareIntent(createShareIntent());
                    }

                } else {
                    mTrailerRecycleView.setVisibility(View.GONE);
                    mNoTrailerTextView.setVisibility(View.VISIBLE);
                }
                break;
            }
            case FAVORITE_LOADER:{
                if (data.getCount() > 0 && data.moveToFirst()){
                    //Allow to Unfavorite
                    mFavoriteButton.setText(getResources().getString(R.string.unfavorite));
                } else {
                    //Allow to Favorite
                    mFavoriteButton.setText(getResources().getString(R.string.favorite));

                }
                break;
            }
        }
    }


    @Override
    public void onLoaderReset(Loader loader) {
        
    }
}