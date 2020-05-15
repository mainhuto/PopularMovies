package com.example.android.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmovies.database.AppDatabase;
import com.example.android.popularmovies.database.FavMovieEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<FavMovieEntry>> movieEntryList;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase moviesDB = AppDatabase.getInstance(this.getApplication());
        movieEntryList = moviesDB.favMovieDao().loadAllFavMovies();
    }

    public LiveData<List<FavMovieEntry>> geFavMovies(){
        return movieEntryList;
    }

}
