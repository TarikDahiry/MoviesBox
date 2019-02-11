package com.topqal.tarik.moviesbox.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.squareup.picasso.Picasso;
import com.topqal.tarik.moviesbox.AppExecutors;
import com.topqal.tarik.moviesbox.QueryUtils;
import com.topqal.tarik.moviesbox.Urls;
import com.topqal.tarik.moviesbox.adapters.CastAdapter;
import com.topqal.tarik.moviesbox.adapters.ReviewAdapter;
import com.topqal.tarik.moviesbox.model.Cast;
import com.topqal.tarik.moviesbox.model.Movie;
import com.topqal.tarik.moviesbox.R;
import com.topqal.tarik.moviesbox.adapters.MovieTrailersAdapter;
import com.topqal.tarik.moviesbox.adapters.MovieTrailersClickListener;
import com.topqal.tarik.moviesbox.model.Review;
import com.topqal.tarik.moviesbox.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.topqal.tarik.moviesbox.Urls.API_KEY;
import static com.topqal.tarik.moviesbox.Urls.BASE_URL;
import static com.topqal.tarik.moviesbox.Urls.LANGUAGE;
import static com.topqal.tarik.moviesbox.Urls.PARAM_CREDITS;
import static com.topqal.tarik.moviesbox.Urls.PARAM_LANGUAGE;
import static com.topqal.tarik.moviesbox.Urls.PARAM_MOVIE;
import static com.topqal.tarik.moviesbox.Urls.PARAM_QUESTION_MARK;
import static com.topqal.tarik.moviesbox.Urls.PARAM_REVIEWS;
import static com.topqal.tarik.moviesbox.Urls.PARAM_TRAILERS;
import static com.topqal.tarik.moviesbox.Urls.TRAILERS_PARAM_API;


public class MovieDetailsFragment extends Fragment {

    private static final String TAG = MovieDetailsFragment.class.getSimpleName();
    private ImageView fav;
    private ImageView unfav;
    private ImageView poster;
    //private ImageView backdrop;
    //private TextView title;
    private TextView date;
    private TextView description;
    private RatingBar ratingBar;

    private LinearLayout reviewsContainer;

    private RecyclerView trailersRecyclerView;
    //youtube player fragment
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private List<String> movieTrailersList;
    //youtube player to play video when new video selected
    private YouTubePlayer youTubePlayer;
    private int movieId;

    private RecyclerView castsRv;
    private CastAdapter castAdapter;

    private RecyclerView reviewsRv;
    private ReviewAdapter reviewAdapter;

    private AppDatabase mDb;

    // Required empty public constructor
    public MovieDetailsFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_details_fragment, container, false);

        setHasOptionsMenu(true);
        movieTrailersList = new ArrayList<>();

        mDb = AppDatabase.getInstance(getActivity());
        reviewsContainer = rootView.findViewById(R.id.reviews_container);

        fav = rootView.findViewById(R.id.favorite);
        unfav = rootView.findViewById(R.id.unfavorite);
        poster = rootView.findViewById(R.id.poster_image);

        trailersRecyclerView = rootView.findViewById(R.id.recycler_view);
        trailersRecyclerView.setHasFixedSize(true);
        castsRv = rootView.findViewById(R.id.casts_recycler_view);
        castsRv.setHasFixedSize(true);
        reviewsRv = rootView.findViewById(R.id.reviews_recycler_view);
        reviewsRv.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        reviewsRv.setLayoutManager(linearLayoutManager);
        reviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<Review>());
        reviewsRv.setAdapter(reviewAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        castsRv.setLayoutManager(layoutManager);
        castAdapter = new CastAdapter(getActivity(), new ArrayList<Cast>());
        castsRv.setAdapter(castAdapter);

        //backdrop = rootView.findViewById(R.id.backdrop_image);
        //title = rootView.findViewById(R.id.title);
        ratingBar = rootView.findViewById(R.id.rating_bar);
        DrawableCompat.setTint(ratingBar.getProgressDrawable(), ContextCompat.getColor(getActivity(), R.color.colorAccent));
        date = rootView.findViewById(R.id.year);
        description = rootView.findViewById(R.id.description);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Movie movie = getActivity().getIntent().getParcelableExtra("Movie");
        if (movie != null) {
            String year = movie.getDate();
            String mPoster = movie.getPoster();

            movieId = movie.getId();

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (mDb.movieDao().loadMovieById(movieId) != null) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                unfav.setVisibility(View.GONE);
                                fav.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            });
            ratingBar.setRating(movie.getRating()/ 2);
            date.setText(year);
            description.setText(movie.getMovieInfo());
            displayImage(mPoster, poster);
        }

        new TrailersAsyncTask().execute();

        unfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unfav.setVisibility(View.GONE);
                fav.setVisibility(View.VISIBLE);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.movieDao().saveMovie(movie);
                    }
                });
            }
        });

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fav.setVisibility(View.GONE);
                unfav.setVisibility(View.VISIBLE);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.movieDao().deleteMovie(movie);
                    }
                });
            }
        });
    }

    private void displayImage(String imageUrl, ImageView image) {
        Picasso.with(getContext())
                .load(Urls.POSTER_IMAGE_BASE_URL + Urls.POSTER_IMAGE_SIZE + imageUrl)
                .placeholder(R.drawable.poster_replace_image)
                .error(R.drawable.poster_replace_image)
                .fit()
                .centerInside()
                .into(image);
    }

    @Override
    public void onStart() {
        super.onStart();
        new CastsAsyncTask().execute();
        new ReviewsAsyncTask().execute();
    }

    private void initializeYoutubePlayer() {

        youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_player_fragment, youTubePlayerFragment).commit();

        if (youTubePlayerFragment == null) {
            return;
        }
        youTubePlayerFragment.initialize(Urls.GOOGLE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                                boolean wasRestored) {
                if (!wasRestored) {
                    youTubePlayer = player;
                    //set the player style default
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    //cue the 1st video by default
                    youTubePlayer.cueVideo(movieTrailersList.get(0));
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                //print or show error if initialization failed
                Log.e(TAG, "Youtube Player View initialization failed");
            }
        });
    }

    private void setUpTrailersRecyclerView() {
        // Horizontal direction recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        trailersRecyclerView.setLayoutManager(linearLayoutManager);
    }

    // populate the recycler view and implement the click event here
    private void populateRecyclerView() {
        final MovieTrailersAdapter adapter = new MovieTrailersAdapter(getActivity(), movieTrailersList);
        trailersRecyclerView.setAdapter(adapter);

        // set click event
        trailersRecyclerView.addOnItemTouchListener(new MovieTrailersClickListener(getActivity(), new MovieTrailersClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (youTubePlayerFragment != null && youTubePlayer != null) {
                    // update selected position
                    adapter.setSelectedPosition(position);

                    //load selected video
                    youTubePlayer.cueVideo(movieTrailersList.get(position));
                }

            }
        }));
    }
    private class TrailersAsyncTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            String mUrl = BASE_URL + PARAM_MOVIE + "/" + movieId + PARAM_TRAILERS + PARAM_QUESTION_MARK
                    + TRAILERS_PARAM_API + API_KEY + PARAM_LANGUAGE + LANGUAGE;
            return QueryUtils.fetchTrailers(mUrl);
        }

        @Override
        protected void onPostExecute(List<String> trailers) {
            if (trailers != null && !trailers.isEmpty()) {
                movieTrailersList.clear();
                movieTrailersList.addAll(trailers);
                initializeYoutubePlayer();
                setUpTrailersRecyclerView();
                populateRecyclerView();
            }
        }
    }

    private class CastsAsyncTask extends AsyncTask<String, Void, List<Cast>> {
        @Override
        protected List<Cast> doInBackground(String... params) {
            String mUrl = BASE_URL + PARAM_MOVIE + "/" + movieId + PARAM_CREDITS + PARAM_QUESTION_MARK
                    + TRAILERS_PARAM_API + API_KEY;
            return QueryUtils.fetchCasts(mUrl);
        }

        @Override
        protected void onPostExecute(List<Cast> casts) {
            if (casts != null && !casts.isEmpty()) {
                castAdapter.updateCasts(casts);
            }
        }
    }

    private class ReviewsAsyncTask extends AsyncTask<String, Void, List<Review>> {
        @Override
        protected List<Review> doInBackground(String... params) {
            String mUrl = BASE_URL + PARAM_MOVIE + "/" + movieId + PARAM_REVIEWS + PARAM_QUESTION_MARK
                    + TRAILERS_PARAM_API + API_KEY + PARAM_LANGUAGE + LANGUAGE;

            return QueryUtils.fetchReviews(mUrl);
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            if (reviews != null && !reviews.isEmpty()) {
                reviewAdapter.updateReviews(reviews);
            } else {
                reviewsContainer.setVisibility(View.GONE);
            }
        }
    }
}