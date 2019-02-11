package com.topqal.tarik.moviesbox.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.topqal.tarik.moviesbox.R;
import com.topqal.tarik.moviesbox.Urls;

import java.util.ArrayList;
import java.util.List;

public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.TrailersViewHolder> {
    private static final String TAG = MovieTrailersAdapter.class.getSimpleName();
    private Context context;
    private List<String> movieTrailersList;

    //position to check which position is selected
    private int selectedPosition = 0;


    public MovieTrailersAdapter(Context context, List<String> movieTrailersList) {
        this.context = context;
        this.movieTrailersList = movieTrailersList;
    }

    @NonNull
    @Override
    public TrailersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.movie_trailers_view, parent, false);
        return new TrailersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailersViewHolder holder, final int position) {
        //if selected position is equal to that mean view is selected so change the cardview color
        if (selectedPosition == position) {
            holder.trailersCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            //if selected position is not equal to that mean view is not selected so change the cardview color to white back again
            holder.trailersCardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        }

        /*  initialize the thumbnail image view , we need to pass Developer Key */
        holder.trailersThumbnailImage.initialize(Urls.GOOGLE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                //when initialization is sucess, set the video id to thumbnail to load
                youTubeThumbnailLoader.setVideo(movieTrailersList.get(position));

                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                        //when thumbnail loaded successfully release the thumbnail loader as we are showing thumbnail in adapter
                        youTubeThumbnailLoader.release();
                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                        //print or show error when thumbnail load failed
                        Log.e(TAG, "Youtube Thumbnail Error");
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                //print or show error when initialization failed
                Log.e(TAG, "Youtube Initialization Failure");

            }
        });

    }

    @Override
    public int getItemCount() {
        return movieTrailersList != null ? movieTrailersList.size() : 0;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        //when item selected notify the adapter
        notifyDataSetChanged();
    }

    public void updateTrailers(List<String> newList) {
        movieTrailersList.clear();
        movieTrailersList.addAll(newList);
        notifyDataSetChanged();
    }


    class TrailersViewHolder extends RecyclerView.ViewHolder {
        CardView trailersCardView;
        YouTubeThumbnailView trailersThumbnailImage;

        public TrailersViewHolder(View itemView) {
            super(itemView);
            trailersCardView = itemView.findViewById(R.id.youtube_card_view);
            trailersThumbnailImage = itemView.findViewById(R.id.movie_thumbnail_image);
        }
    }
}
