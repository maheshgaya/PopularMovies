package com.maheshgaya.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
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
public class MovieAdapter extends CursorAdapter {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    /**
     * Constructor
     * @param context
     * @param cursor
     */
    public MovieAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, flags);
    }
    

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view =  LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String imageUrl = ""; //cursor.getString(); //TODO: To replace with projection

        try {
            Picasso
                    .with(context)
                    .load(imageUrl)
                    .error(R.drawable.ic_photo_placeholder)
                    .into(viewHolder.thumbnailImageView);
            //Log.d(LOG_TAG, "getView: Picasso executed");
        }catch (java.lang.IllegalArgumentException e){
            Log.e(LOG_TAG, "getView: " + e.getMessage(), e );
            e.printStackTrace();
        }
    }

    /**
     * template for getting views about each gridview item
     */
    public static class ViewHolder {
        ImageView thumbnailImageView;

        public ViewHolder(View view){
            this.thumbnailImageView = (ImageView)view.findViewById(R.id.thumbnail_image_view);
        }
    }
}
