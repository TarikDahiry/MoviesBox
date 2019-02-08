package com.topqal.tarik.moviesbox.ui;


import android.app.Fragment;
import android.app.LoaderManager;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.topqal.tarik.moviesbox.AppExecutors;
import com.topqal.tarik.moviesbox.model.Movie;
import com.topqal.tarik.moviesbox.adapters.MovieAdapter;
import com.topqal.tarik.moviesbox.MovieLoader;
import com.topqal.tarik.moviesbox.R;
import com.topqal.tarik.moviesbox.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.topqal.tarik.moviesbox.Urls.API_KEY;
import static com.topqal.tarik.moviesbox.Urls.BASE_URL;
import static com.topqal.tarik.moviesbox.Urls.LANGUAGE;
import static com.topqal.tarik.moviesbox.Urls.PAGE;
import static com.topqal.tarik.moviesbox.Urls.PARAM_ADULT;
import static com.topqal.tarik.moviesbox.Urls.PARAM_API;
import static com.topqal.tarik.moviesbox.Urls.PARAM_DISCOVER;
import static com.topqal.tarik.moviesbox.Urls.PARAM_LANGUAGE;
import static com.topqal.tarik.moviesbox.Urls.PARAM_MOVIE;
import static com.topqal.tarik.moviesbox.Urls.PARAM_PAGE_NO;
import static com.topqal.tarik.moviesbox.Urls.PARAM_QUESTION_MARK;
import static com.topqal.tarik.moviesbox.Urls.PARAM_SORT;

public class MoviesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Movie>>,
        MovieAdapter.MovieClickListener {

    private static final int MOVIE_LOADER_ID = 1;
    public static MenuItem pop, top, nowPlaying, upcoming, favorite;
    private AppDatabase mDb;
    // <List<Movie>>
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private TextView emptyView;
    private ImageView image;
    private SharedPreferences prefs;
    private LoaderManager loaderManager;
    private  MovieLoader loader;

    //Mandatory constructor for instantiating the fragment
    public MoviesListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies_list, container, false);

        setHasOptionsMenu(true);
        recyclerView = rootView.findViewById(R.id.movies_list_rv);
        emptyView = rootView.findViewById(R.id.empty_view);
        image = rootView.findViewById(R.id.error_image);
        adapter = new MovieAdapter(getActivity(), new ArrayList<Movie>(), this);

        mDb = AppDatabase.getInstance(getActivity());
        //TODO Change the integer colmuns from dimens.xml to new file named integer.xml under values directory and include land file
        final int columns = getResources().getInteger(R.integer.movies_columns);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columns));
        recyclerView.setAdapter(adapter);

        // TODO Remove these line of code if ActionBar works fine from MainActivity
        // Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        // ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

            if (getItemSp("favoriteChecked")) {
                loadFavorites();
            } else {
                loadMoviesIfConnected();
            }
        return rootView;
    }

    private void loadMoviesIfConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            showData();
            loaderManager = getLoaderManager();
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
        } else {
            //TODO Fix appearance of error message if shown for no connection
            showErrorMessage();
            emptyView.setText(R.string.connection_error_message);
            image.setImageResource(R.drawable.app_welcome_image_red);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getItemSp("favoriteChecked")) {
            loadFavorites();
        }
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.GONE);
        image.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.VISIBLE);
    }

    private void showData() {
        image.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {

        loader = null;

        if (getItemSp("topChecked")) {
            loader = new MovieLoader(getActivity(), mainUrl("vote_average.desc"));
        } else if (getItemSp("latestChecked")) {
            loader = new MovieLoader(getActivity(), shortUrl("now_playing"));
        } else if (getItemSp("upcomingChecked")) {
            loader = new MovieLoader(getActivity(), shortUrl("upcoming"));
        } else {
            loader = new MovieLoader(getActivity(), mainUrl("popularity.desc"));
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        if (movies != null && !movies.isEmpty()) {
            //recyclerView.setVisibility(View.VISIBLE);
            //emptyView.setVisibility(View.GONE);
            showData();
            adapter.updateMovies(movies);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        adapter.clearMovies();
    }

    @Override
    public void onMovieClick(int position) {
        // TODO Poster only have the last path not the all URL
        Movie movie = adapter.getMovie(position);
        if (movie != null) {
            // TODO Go through intent and sending data between fragment
            // Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
            Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
            intent.putExtra("Movie", movie);
            startActivity(intent);
        }
    }

    private String mainUrl(String sort) {
        return BASE_URL + PARAM_DISCOVER + PARAM_MOVIE + PARAM_QUESTION_MARK
                + PARAM_PAGE_NO + PAGE + PARAM_SORT + sort + PARAM_ADULT + PARAM_API + API_KEY;
    }

    private String shortUrl(String sort) {
        return BASE_URL + PARAM_MOVIE + "/" + sort + PARAM_QUESTION_MARK + PARAM_API + API_KEY
                + PARAM_LANGUAGE + LANGUAGE;
    }

    private void updateUI(MenuItem item, String prefsKey) {
        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
            saveItemSp(prefsKey, true);
            if (loader != null) {
                ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                assert connMgr != null;
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    showData();
                    loaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
                } else {
                    //TODO Fix appearance of error message if shown for no connection
                    loaderManager.destroyLoader(MOVIE_LOADER_ID);
                    showErrorMessage();
                    emptyView.setText(R.string.connection_error_message);
                    image.setImageResource(R.drawable.app_welcome_image_red);
                }
            } else {
                loadMoviesIfConnected();
            }
        }
    }

    private void loadFavorites() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<Movie> favoriteMovies = mDb.movieDao().loadFavoriteMovies();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loader != null) {
                            loaderManager.destroyLoader(MOVIE_LOADER_ID);
                        }
                        showData();
                        adapter.updateMovies(favoriteMovies);
                    }
                });
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_items, menu);

        pop = menu.findItem(R.id.popular);
        top = menu.findItem(R.id.high_rated);
        nowPlaying = menu.findItem(R.id.now_playing);
        upcoming = menu.findItem(R.id.upcoming);
        favorite = menu.findItem(R.id.favorite);

        if (getItemSp("topChecked")) {
            top.setChecked(true);
        } else if (getItemSp("nowPlayingChecked")) {
            nowPlaying.setChecked(true);
        } else if (getItemSp("upcomingChecked")) {
            upcoming.setChecked(true);
        } else if (getItemSp("favoriteChecked")) {
            favorite.setChecked(true);
            //loadFavorites();
        } else {
            pop.setChecked(true);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.popular:
                updateUI(pop, "popChecked");
                break;
            case R.id.high_rated:
                updateUI(top, "topChecked");
                break;
            case R.id.now_playing:
                //TODO latest url is null CHECK URL again for validation
                updateUI(nowPlaying, "nowPlayingChecked");
                break;
            case R.id.upcoming:
                updateUI(upcoming, "upcomingChecked");
                break;
            case R.id.favorite:
                if (favorite.isChecked()) {
                    favorite.setChecked(false);
                } else {
                    favorite.setChecked(true);
                    saveItemSp("favoriteChecked", true);
                    //TODO This is for saving favorites DB
                    loadFavorites();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean getItemSp(String key) {
        prefs = getActivity().getSharedPreferences("menu_items", Context.MODE_PRIVATE);
        return prefs.getBoolean(key, false);

    }

    private void saveItemSp(String key, Boolean state) {
        prefs = getActivity().getSharedPreferences("menu_items", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putBoolean(key, state);
        editor.apply();
    }
}

