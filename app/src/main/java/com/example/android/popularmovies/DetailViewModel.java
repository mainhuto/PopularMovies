package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.popularmovies.database.AppDatabase;
import com.example.android.popularmovies.database.FavMovieEntry;

public class DetailViewModel extends ViewModel {

    private LiveData<FavMovieEntry> favMovie;

    public DetailViewModel(AppDatabase moviesDB, int favMovieId, int tmdbMovieId) {
        if ( moviesDB != null ) {
            favMovie = moviesDB.favMovieDao().loadMovieByTMDBId(tmdbMovieId);
        }
    }

    public LiveData<FavMovieEntry> getFavMovie() {
        return favMovie;
    }
}
