package com.maheshgaya.android.popularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by Mahesh Gaya on 10/15/16.
 */

public interface TrailerColumns {
    /**
     * _id, movie_api_key (FOREIGN KEY), trailer_url
     */

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.INTEGER) @Unique
    @NotNull
    @References(table = MovieDatabase.MOVIES, column = MovieColumns.MOVIE_API_ID)
    public static final String MOVIE_API_ID = "movie_api_id";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String TRAILER_URL = "trailer_url";
}
