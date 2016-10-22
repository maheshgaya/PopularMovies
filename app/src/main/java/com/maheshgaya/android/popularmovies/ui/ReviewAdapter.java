package com.maheshgaya.android.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maheshgaya.android.popularmovies.R;

/**
 * Created by Mahesh Gaya on 10/20/16.
 */

public class ReviewAdapter extends CursorRecyclerViewAdapter<ReviewAdapter.ViewHolder>{

    public ReviewAdapter(Context context, Cursor cursor){
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        int position = cursor.getPosition() + 1; //add 1 to zero
        viewHolder.mTextView.setText(
                viewHolder.itemView.getResources().getString(R.string.review_list_item) + " " + position);
        final String url = cursor.getString(DetailFragment.COLUMN_REVIEW_URL);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW,
                                Uri.parse(url)));
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_review, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public  TextView mTextView;
        public ViewHolder(View view){
            super(view);
            mTextView = (TextView)view.findViewById(R.id.review_item_text_view);
        }
    }

}
