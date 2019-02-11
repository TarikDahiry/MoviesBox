package com.topqal.tarik.moviesbox;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.topqal.tarik.moviesbox.model.Movie;

import java.util.List;

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    // Query Url
    private String mUrl;

    /**
     * Construct a new {@Link MovieLoder}
     * @param context of the activity
     * @param mUrl for loading data
     */
    public MovieLoader(Context context, String mUrl) {
        super(context);
        this.mUrl = mUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    // Use Background thread to load the list of movies
    @Override
    public List<Movie> loadInBackground() {
        if (mUrl == null ){
            return null;
        }
        // Load and return the list of movies
        List<Movie> mMovies = QueryUtils.fetchMovies(mUrl);
        return mMovies;
    }
}
