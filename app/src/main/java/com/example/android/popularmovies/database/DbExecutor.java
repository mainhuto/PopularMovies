package com.example.android.popularmovies.database;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DbExecutor {

    private static final Object LOCK = new Object();
    private static DbExecutor sInstance;
    private final Executor diskIO;

    private DbExecutor(Executor diskIO) {
        this.diskIO = diskIO;
    }

    public static DbExecutor getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new DbExecutor(Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor diskIO() {
        return diskIO;
    }

}
