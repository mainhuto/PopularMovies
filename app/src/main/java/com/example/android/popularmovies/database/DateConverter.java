package com.example.android.popularmovies.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp){
        return timestamp != null ? new Date(timestamp) : null;
    }

    @TypeConverter
    public static Long toTimeStamp(Date date) {
        return date != null ? date.getTime() : null;
    }

}
