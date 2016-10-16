package com.maheshgaya.android.popularmovies.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Mahesh Gaya on 10/15/16.
 */

@ContentProvider(authority = MovieProvider.AUTHORITY, database = MovieDatabase.class)
public final class MovieProvider {
    public static final String DIR_TYPE = "vnd.android.cursor.dir";
    public static final String ITEM_TYPE = "vnd.android.cursor.item";
    public static final  String AUTHORITY =
            "com.maheshgaya.android.popularmovies.data.MovieProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    //reference for the paths
    interface Path{
        String MOVIES = "movies";
        String FAVORITES = "favorites";
        String TOP_RATED = "top_rated";
        String MOST_POPULAR = "most_popular";
        String REVIEWS = "reviews";
        String TRAILERS = "trailers";

    }

    //Build the uris
    public static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = MovieDatabase.MOVIES) public static class Movies{
        @ContentUri(
                path = Path.MOVIES,
                type = DIR_TYPE + "/movie"
        )
        public static final Uri CONTENT_URI = buildUri(Path.MOVIES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.MOVIES + "/#",
                type = ITEM_TYPE + "/movie",
                whereColumn = MovieColumns._ID,
                pathSegment = 1
        )
        public static Uri withId(long id){
            return buildUri(Path.MOVIES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = MovieDatabase.FAVORITES) public static class Favorites{
        @ContentUri(
                path = Path.FAVORITES,
                type = DIR_TYPE + "/favorite"
        )
        public static final Uri CONTENT_URI = buildUri(Path.FAVORITES);

        @InexactContentUri(
                name = "FAVORITE_ID",
                path = Path.FAVORITES + "/#",
                type = ITEM_TYPE + "/favorite",
                whereColumn = FavoriteColumns._ID,
                pathSegment = 1
        )
        public static Uri withId(long id){
            return buildUri(Path.FAVORITES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = MovieDatabase.MOST_POPULAR) public static class MostPopular{
        @ContentUri(
                path = Path.MOST_POPULAR,
                type = DIR_TYPE + "/most_popular"
        )
        public static final Uri CONTENT_URI = buildUri(Path.MOST_POPULAR);
    }

    @TableEndpoint(table = MovieDatabase.TOP_RATED) public static class TopRated{
        @ContentUri(
                path = Path.TOP_RATED,
                type = DIR_TYPE + "/top_rated"
        )
        public static final Uri CONTENT_URI = buildUri(Path.TOP_RATED);
    }

    @TableEndpoint(table = MovieDatabase.TRAILERS) public static class Trailers{
        @ContentUri(
                path = Path.TRAILERS,
                type = DIR_TYPE + "/trailer"
        )
        public static final Uri CONTENT_URI = buildUri(Path.TRAILERS);

        @InexactContentUri(
                name = "TRAILER_ID",
                path = Path.TRAILERS + "/#",
                type = DIR_TYPE + "/trailer",
                whereColumn = TrailerColumns.MOVIE_ID,
                pathSegment = 1

        )
        public static Uri withId(long id){
            return buildUri(Path.TRAILERS, String.valueOf(id));
        }
    }

    @TableEndpoint(table = MovieDatabase.REVIEW) public static class Reviews{
        @ContentUri(
                path = Path.REVIEWS,
                type = DIR_TYPE + "/review"
        )
        public static final Uri CONTENT_URI = buildUri(Path.REVIEWS);

        @InexactContentUri(
                name = "REVIEW_ID",
                path = Path.REVIEWS + "/#",
                type = DIR_TYPE + "/review",
                whereColumn = ReviewColumns.MOVIE_ID,
                pathSegment = 1
        )
        public static Uri withId(long id){
            return buildUri(Path.REVIEWS, String.valueOf(id));
        }

    }

}
