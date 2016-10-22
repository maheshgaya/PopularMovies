package com.maheshgaya.android.popularmovies.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.maheshgaya.android.popularmovies.R;

/**
 * Created by Mahesh Gaya on 10/21/16.
 */

public class FavoriteActivity extends AppCompatActivity implements FavoriteFragment.Callback{
    private static final String DETAIL_FRAGMENT_TAG = "DFF";
    private boolean mTwoPane;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorite);
        if (findViewById(R.id.favorite_detail_container) != null){
            //tablet layout
            mTwoPane = true;
            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.favorite_detail_container, new DetailFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            //small screen layout
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        FavoriteFragment movieFragment = ((FavoriteFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_favorite));
    }

    @Override
    public void onItemSelected(Uri favoriteUri) {
        if (mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, favoriteUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.favorite_detail_container, fragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(favoriteUri);
            startActivity(intent);

        }

    }
}
