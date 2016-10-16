package com.maheshgaya.android.popularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Mahesh Gaya on 10/15/16.
 */

@Database(version = MovieDatabase.VERSION)
public class MovieDatabase {
    private MovieDatabase(){}
    public static final int VERSION = 1;
    @Table(MovieColumns.class) public static final String MOVIES = "movies";
    @Table(FavoriteColumns.class) public static final String FAVORITES = "favorites";
    @Table(TrailerColumns.class) public static final String TRAILERS = "trailers";
    @Table(ReviewColumns.class) public static final String REVIEW = "reviews";
    @Table(MostPopularColumns.class) public static final String MOST_POPULAR = "most_popular";
    @Table(TopRatedColumns.class) public static final String TOP_RATED = "top_rated";
}
