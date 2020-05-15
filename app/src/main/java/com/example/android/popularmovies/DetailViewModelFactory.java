package com.example.android.popularmovies;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.database.AppDatabase;

public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase movieDb;
    private final int favMovieId;
    private final int tmdbMovieId;

    public DetailViewModelFactory(AppDatabase movieDb, int favMovieId, int tmdbMovieId) {
        this.movieDb = movieDb;
        this.favMovieId = favMovieId;
        this.tmdbMovieId = tmdbMovieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailViewModel(movieDb, favMovieId, tmdbMovieId);
    }
}
