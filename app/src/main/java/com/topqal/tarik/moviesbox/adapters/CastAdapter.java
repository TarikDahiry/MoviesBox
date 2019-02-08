package com.topqal.tarik.moviesbox.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.topqal.tarik.moviesbox.R;
import com.topqal.tarik.moviesbox.Urls;
import com.topqal.tarik.moviesbox.model.Cast;

import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {
    private List<Cast> castsList;
    private Context context;

    public CastAdapter(Context context, List<Cast> castsList) {
        this.context = context;
        this.castsList = castsList;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.casts_list_item, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        Cast cast = castsList.get(position);
        holder.nameTextView.setText(cast.getActorName());
        holder.nameTextView.setSelected(true);
        holder.characterTv.setText(cast.getMovieCharacter());
        holder.characterTv.setSelected(true);

        Picasso.with(context)
                .load(Urls.POSTER_IMAGE_BASE_URL + Urls.POSTER_IMAGE_SIZE + cast.getProfilePath())
                .placeholder(R.drawable.cast_place_holder)
                .error(R.drawable.cast_place_holder)
                .fit()
                //.centerInside()
                .into(holder.actorIv);
    }

    public void updateCasts(List<Cast> newList) {
        castsList.clear();
        castsList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return castsList == null ? 0 : castsList.size();
    }

    class CastViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView;
        ImageView actorIv;
        TextView characterTv;

        public CastViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.actor_name_tv);
            actorIv = itemView.findViewById(R.id.actor_profile_picture);
            characterTv = itemView.findViewById(R.id.movie_character_tv);
        }
    }
}
