package com.maheshgaya.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright (c) Mahesh Gaya
 * Author: Mahesh Gaya
 * Date: 9/18/16.
 */

/**
 * Movie
 * This is the template for Movie
 */
public class Movie implements Parcelable{
    /*
    * Allow the user to tap on a movie poster and transition to a details screen with
    * additional information such as:
    * @param title - original title
    * @param thumbnailURL - movie poster image thumbnail
    * @param plot - A plot synopsis (called overview in the api)
    * @param ratings - user rating (called vote_average in the api)
    * @param releaseDate - release date
    *
    * */

    private static final String BASE_URL = "http://image.tmdb.org/t/p/w185/"; //base url for  getting poster


    private int id; //id from Movie DB
    private String title; //original title
    private String thumbnailURL; //movie poster image thumbnail
    private String plot; //A plot synopsis (called overview in the api)
    private double ratings; //user rating (called vote_average in the api)
    private String releaseDate; //release date

    private final String TAG = Movie.class.getSimpleName(); //logging purposes

    /**
     * Movie Constructor
     * @param  id id of movie
     * @param title title of movie
     * @param thumbnailURL thumbnail/poster url of the movie
     * @param plot plot/overview of the movie
     * @param ratings user ratings of the movie
     * @param releaseDate release date of the movie, format: yyyy-MM-dd
     */
    public Movie(int id, String title, String thumbnailURL, String plot,
                 double ratings, String releaseDate){
        this.id = id;
        this.title = title;
        this.thumbnailURL = BASE_URL + thumbnailURL;
        //Log.d(TAG, "Movie: " + this.thumbnailURL);
        this.plot = plot;
        this.ratings = ratings;
        this.releaseDate = releaseDate;

    }

    /**
     * Movie constructor
     * @param in
     * reads data from parcels
     */
    private Movie(Parcel in){
        title = in.readString();
        thumbnailURL = in.readString();
        plot = in.readString();
        ratings = in.readDouble();
        releaseDate = in.readString();
    }


    /**
     * getId()
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * setId
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * getBaseUrl
     * @return base url
     */
    public static String getBaseUrl() {
        return BASE_URL;
    }

    /**
     * getPlot
     * @return plot/overview
     */
    public String getPlot() {
        return plot;
    }

    /**
     * setPlot
     * @param plot
     * set plot/overview
     */
    public void setPlot(String plot) {
        this.plot = plot;
    }

    /**
     * getRatings
     * @return ratings
     */
    public double getRatings() {
        return ratings;
    }

    /**
     * setRatings
     * @param ratings
     * set ratings
     */
    public void setRatings(double ratings) {
        this.ratings = ratings;
    }

    /**
     * getReleaseDate
     * @return releaseDate
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * setReleaseDate
     * @param releaseDate
     * set release date
     */
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * getThumbnailURL
     * @return thumbnailURL
     */
    public String getThumbnailURL() {
        //Log.d(TAG, "getThumbnailURL: " + thumbnailURL);
        return thumbnailURL;
    }

    /**
     * setThumbnailURL
     * @param thumbnailURL
     * set the thumbnail url
     */
    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    /**
     * getTitle
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * setTitle
     * @param title
     * set the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * toString
     * @return the instance fields in string
     */
    public String toString(){
        return title + "--" + thumbnailURL + "--" +
                plot + "--" + ratings + "--" +releaseDate;
    }

    /**
     * describeContents
     * @return 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * writeToParcel
     * @param parcel
     * @param i
     * write the parcel (used to pass data from one intent to another)
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(thumbnailURL);
        parcel.writeString(plot);
        parcel.writeDouble(ratings);
        parcel.writeString(releaseDate);

    }

    /**
     * Parcelable Creator
     */
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }



    };
}
