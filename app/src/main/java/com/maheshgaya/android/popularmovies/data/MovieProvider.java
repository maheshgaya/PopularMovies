package com.maheshgaya.android.popularmovies.data;

import net.simonvt.schematic.annotation.ContentProvider;

/**
 * Created by Mahesh Gaya on 10/15/16.
 */

@ContentProvider(authority = MovieProvider.AUTHORITY, database = MovieDatabase.class)
public final class MovieProvider {
    public static final  String AUTHORITY =
            "com.maheshgaya.android.popularmovies.data.MovieProvider";
}
