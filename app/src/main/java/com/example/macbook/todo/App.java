package com.example.macbook.todo;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.macbook.todo.database.TodoDatabase;

/**
 * Created by macbook on 16/12/2017.
 */

public class App extends Application {

    private static final String DATABASE_NAME = "TodoDatabase";

    private TodoDatabase database;
    public static App INSTANCE;

    public static App get() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // create database
        database = Room.databaseBuilder(getApplicationContext(), TodoDatabase.class, DATABASE_NAME)
//                .addMigrations(TodoDatabase.MIGRATION_1_2)
                .build();

        INSTANCE = this;
    }

    public TodoDatabase getDB() {
        return database;
    }
}
