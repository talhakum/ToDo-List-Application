package com.example.macbook.todo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.macbook.todo.database.entity.Todo;

import java.util.List;

/**
 * Created by macbook on 16/12/2017.
 */

@Dao
public interface TodoDao {
    @Query("SELECT * FROM todo ORDER BY priority,date ASC")
    List<Todo> getAll();

    @Query("SELECT * FROM todo ORDER BY priority ASC")
    List<Todo> getAllByPrioritySorting();

    @Query("SELECT * FROM todo ORDER BY date ASC")
    List<Todo> getAllByDateSorting();

    @Insert
    void insertAll(List<Todo> todos);

    @Update
    void update(Todo todo);

    @Delete
    void delete(Todo todo);

    @Insert
    void insert(Todo... todo);
}
