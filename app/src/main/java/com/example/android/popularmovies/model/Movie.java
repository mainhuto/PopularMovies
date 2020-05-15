package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Movie implements Parcelable {

    private static final String POSTER_SIZE = "w185";
    private static final String THUMBNAIL_SIZE = "w154";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private String title;
    private String imagePath;
    private String plotSynopsis;
    private double userRating;
    private long releaseDate;
    private int tmdbMovieId;
    private int favMovieId;

    public Movie(String title, String imagePath, String plotSynopsis, double userRating, long releaseDate, int tmdbMovieId) {
        this.title = title;
        this.imagePath = imagePath;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.tmdbMovieId = tmdbMovieId;
        this.favMovieId = -1;
    }

    private Movie(Parcel in) {
        this.title = in.readString();
        this.imagePath = in.readString();
        this.plotSynopsis = in.readString();
        this.userRating = in.readDouble();
        this.releaseDate = in.readLong();
        this.tmdbMovieId = in.readInt();
        this.favMovieId = in.readInt();
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

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDateString() {
        Date date = new Date(this.releaseDate);
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String strDate = dateFormat.format(date);
        return strDate;
    }

    public Date getReleaseDate() {
        Date date;
        date = new Date(this.releaseDate);
        return date;
    }

    public void setReleaseDate(long releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getFavMovieId() {
        return favMovieId;
    }

    public void setFavMovieId(int favMovieId) {
        this.favMovieId = favMovieId;
    }

    public void setFavMovieId(long favMovieId) {
        this.favMovieId = (int)favMovieId;
    }

    public boolean isFavorite() {
        return this.favMovieId < 0 ? false : true;
    }

    public void unmarkFavorite() {
        this.favMovieId = -1;
    }

    public int getTmdbMovieId() {
        return tmdbMovieId;
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
        dest.writeDouble(userRating);
        dest.writeLong(releaseDate);
        dest.writeInt(tmdbMovieId);
        dest.writeInt(favMovieId);
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
