package com.topqal.tarik.moviesbox.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
import com.topqal.tarik.moviesbox.model.Movie;
import com.topqal.tarik.moviesbox.R;
import com.topqal.tarik.moviesbox.Urls;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final ArrayList<Movie> moviesList;
    private final Context context;
    private final MovieClickListener onClickListener;

    public MovieAdapter(Context context, ArrayList<Movie> moviesList, MovieClickListener listener) {
        this.context = context;
        this.moviesList = moviesList;
        onClickListener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder holder, int position) {

        Movie currentMovie = moviesList.get(position);

        holder.title.setText(currentMovie.getTitle());
        holder.title.setSelected(true);

        String date, year;
        date = currentMovie.getDate();
        if (date.equals("")) {
            year = " ";
        } else {
            year = date.substring(0, 4);
        }
        holder.year.setText(year);

        Picasso.with(context)
                .load(Urls.POSTER_IMAGE_BASE_URL + Urls.POSTER_IMAGE_SIZE + currentMovie.getPoster())
                .placeholder(R.drawable.poster_replace_image)
                .error(R.drawable.poster_replace_image)
                .fit()
                .centerInside()
                .into(holder.moviePoster, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) holder.moviePoster.getDrawable()).getBitmap();
                        Palette palette = Palette.from(bitmap).maximumColorCount(32).generate();
                        //Palette.Swatch lightVibrantSwatch = palette.getLightVibrantSwatch();
                        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                        if (vibrantSwatch != null) {
                            holder.frameLayout.setBackgroundColor(vibrantSwatch.getRgb());
                            //holder.title.setTextColor(vibrantSwatch.getTitleTextColor());
                            //holder.year.setTextColor(vibrantSwatch.getBodyTextColor());
                        } else {
                            holder.frameLayout.setBackgroundColor(ContextCompat.getColor(context, (R.color.menuBackground)));
                            //holder.title.setTextColor(Color.WHITE);
                            // holder.year.setTextColor(Color.WHITE);
                        }
                        holder.frameLayout.getBackground().setAlpha(160);
                    }

                    @Override
                    public void onError() {
                        Log.e(MovieAdapter.class.getSimpleName(), "Picasso error!!");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return moviesList == null ? 0 : moviesList.size();
    }

    public void updateMovies(List<Movie> newList) {
        moviesList.clear();
        moviesList.addAll(newList);
        notifyDataSetChanged();
    }

    public void clearMovies() {
        // Check if the list is not empty, if not clear
        // all items and then check if every item was removed
        if (moviesList != null) {
            int listSize = moviesList.size();
            moviesList.clear();
            notifyItemRangeRemoved(0, listSize);
        }
    }

    public Movie getMovie(int position) {

        return moviesList.get(position);
    }

    public interface MovieClickListener {
        void onMovieClick(int position);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView moviePoster;
        TextView title;
        TextView year;
        FrameLayout frameLayout;

        public MovieViewHolder(View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.movie_poster_image);
            title = itemView.findViewById(R.id.rv_title);
            year = itemView.findViewById(R.id.rv_year);
            frameLayout = itemView.findViewById(R.id.rv_framelayout);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            onClickListener.onMovieClick(clickedPosition);
        }
    }
}
