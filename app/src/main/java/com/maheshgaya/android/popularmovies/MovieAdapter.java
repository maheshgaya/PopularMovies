package com.maheshgaya.android.popularmovies;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Arrays;

/**
 * Copyright (c) Mahesh Gaya
 * Author: Mahesh Gaya
 * Date: 9/18/16.
 */

/**
 * MovieAdapter
 * Deals with setting images/poster/thumbnail to the gridview
 */
public class MovieAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    /**
     * Constructor
     * @param context
     * @param movies
     * Change movies array to List
     */
    public MovieAdapter(Context context, Movie[] movies){
        super(context, 0, Arrays.asList(movies));
        //Log.d(LOG_TAG, "MovieAdapter: " + context.getPackageName() + " : " + movies.length);
    }


    /**
     * getView
     * @param position
     * @param convertView
     * @param parent
     * @return converView
     * Deals with setting the images for the gridview
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.d(LOG_TAG, "getView: method executed");
        ViewHolder holder;

        Movie movie = getItem(position); //get current movie

        //initialize the thumbnail image view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
            holder = new ViewHolder();
            holder.thumbnailImageView = (ImageView)convertView.findViewById(R.id.thumbnail_image_view);
            convertView.setTag(holder);
        } else{
            holder= (ViewHolder) convertView.getTag();
        }

        //use picasso to deal with getting the images and setting them to the gridview

        try {
            Picasso
                    .with(getContext())
                    .load(movie.getThumbnailURL())
                    .error(R.drawable.ic_photo_placeholder)
                    .into(holder.thumbnailImageView);
            //Log.d(LOG_TAG, "getView: Picasso executed");
        }catch (java.lang.IllegalArgumentException e){
            Log.e(LOG_TAG, "getView: " + e.getMessage(), e );
            e.printStackTrace();
        }

        return convertView;
    }

    /**
     * template for getting views about each gridview item
     */
    private static class ViewHolder {
        ImageView thumbnailImageView;
    }
}
