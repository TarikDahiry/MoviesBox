package com.topqal.tarik.moviesbox.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.topqal.tarik.moviesbox.model.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie")
    List<Movie> loadFavoriteMovies();

    @Query("SELECT * FROM movie WHERE id = :id")
    Movie loadMovieById(int id);

    @Insert
    void saveMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);
}
