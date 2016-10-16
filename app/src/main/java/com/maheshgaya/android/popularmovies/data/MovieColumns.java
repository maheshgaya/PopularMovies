package com.maheshgaya.android.popularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by Mahesh Gaya on 10/15/16.
 */

public interface MovieColumns {
    /**
     * _id, movie_api_id, title, image_url, plot, ratings, release data
     */

    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.INTEGER) @Unique @NotNull
    public static final String MOVIE_API_ID = "movie_api_id"; //id from API

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String TITLE = "title";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String IMAGE_URL = "image_url";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String PLOT = "plot";

    @DataType(DataType.Type.REAL) @NotNull
    public static final String RATING = "ratings";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String RELEASE_DATE = "release_date";
}
