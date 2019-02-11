package com.topqal.tarik.moviesbox.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.topqal.tarik.moviesbox.R;
import com.topqal.tarik.moviesbox.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{


    private List<Review> reviews;
    private Context mContext;

    public ReviewAdapter(Context context, List<Review> reviews) {
        mContext = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_list_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.authorTv.setText(review.getAuthor());
        holder.contentTv.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviews == null ? 0 : reviews.size();
    }

    public void updateReviews(List<Review> newList) {
        reviews.clear();
        reviews.addAll(newList);
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView authorTv;
        TextView contentTv;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            authorTv = itemView.findViewById(R.id.author);
            contentTv = itemView.findViewById(R.id.review_content);
        }
    }
}
