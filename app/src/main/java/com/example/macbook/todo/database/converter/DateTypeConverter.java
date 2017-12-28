package com.example.macbook.todo.database.converter;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by macbook on 17/12/2017.
 */

public class DateTypeConverter {

    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date value) {
        return value == null ? null : value.getTime();
    }
}