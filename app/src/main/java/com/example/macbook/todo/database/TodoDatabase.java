package com.example.macbook.todo.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;

import com.example.macbook.todo.database.converter.DateTypeConverter;
import com.example.macbook.todo.database.dao.TodoDao;
import com.example.macbook.todo.database.entity.Todo;

/**
 * Created by macbook on 16/12/2017.
 */

@Database(entities = {Todo.class}, version = 1)
@TypeConverters({DateTypeConverter.class})
public abstract class TodoDatabase extends RoomDatabase {
    public abstract TodoDao todoDao();

}
