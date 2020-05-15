package com.example.android.popularmovies.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "favorite_movies")
public class FavMovieEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    @ColumnInfo(name = "image_path")
    private String imagePath;
    @ColumnInfo(name = "plot_synopsis")
    private String plotSynopsis;
    @ColumnInfo(name = "user_rating")
    private double userRating;
    private int duration;
    @ColumnInfo(name = "release_date")
    private Date releaseDate;
    @ColumnInfo(name = "tmdb_movie_id")
    private int tmdbMovieId;

    @Ignore
    public FavMovieEntry(int id, String title, String imagePath, String plotSynopsis, double userRating, int duration, Date releaseDate, int tmdbMovieId) {
        this.id = id;
        this.title = title;
        this.imagePath = imagePath;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.tmdbMovieId = tmdbMovieId;
    }

    public FavMovieEntry(String title, String imagePath, String plotSynopsis, double userRating, int duration, Date releaseDate, int tmdbMovieId) {
        this.title = title;
        this.imagePath = imagePath;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.tmdbMovieId = tmdbMovieId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getTmdbMovieId() {
        return tmdbMovieId;
    }

    public void setTmdbMovieId(int tmdbMovieId) {
        this.tmdbMovieId = tmdbMovieId;
    }
}
