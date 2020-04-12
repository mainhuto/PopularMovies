package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private static final String POSTER_SIZE = "w185";
    private static final String THUMBNAIL_SIZE = "w154";

    private String title;
    private String imagePath;
    private String plotSynopsis;
    private String userRating;
    private String releaseDate;

    public Movie(String title, String imagePath, String plotSynopsis, String userRating, String releaseDate) {
        this.title = title;
        this.imagePath = imagePath;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    private Movie(Parcel in) {
        this.title = in.readString();
        this.imagePath = in.readString();
        this.plotSynopsis = in.readString();
        this.userRating = in.readString();
        this.releaseDate = in.readString();
    }

    public String getPosterUrl(String baseUrl) {
        return baseUrl + POSTER_SIZE + "/" + imagePath;
    }

    public String getThumbnailUrl(String baseUrl) {
        return baseUrl + THUMBNAIL_SIZE + "/" + imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(imagePath);
        dest.writeString(plotSynopsis);
        dest.writeString(userRating);
        dest.writeString(releaseDate);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
