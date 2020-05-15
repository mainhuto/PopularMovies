package com.example.android.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface FavMovieDao {

    @Query("SELECT * FROM favorite_movies ORDER BY user_rating")
    LiveData<List<FavMovieEntry>> loadAllFavMovies();

    @Insert
    long insertFavMovie(FavMovieEntry favMovieEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavMovie(FavMovieEntry favMovieEntry);

    @Delete
    void deleteFavMovie(FavMovieEntry favMovieEntry);

    @Query("SELECT * FROM favorite_movies WHERE id = :id")
    LiveData<FavMovieEntry> loadMovieById(int id);

    @Query("SELECT * FROM favorite_movies WHERE tmdb_movie_id = :tmdbMovieId")
    LiveData<FavMovieEntry> loadMovieByTMDBId(int tmdbMovieId);
}
