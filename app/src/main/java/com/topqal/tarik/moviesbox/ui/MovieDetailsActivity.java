package com.topqal.tarik.moviesbox.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.topqal.tarik.moviesbox.model.Movie;
import com.topqal.tarik.moviesbox.adapters.MovieAdapter;
import com.topqal.tarik.moviesbox.R;
import com.topqal.tarik.moviesbox.Urls;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        final ImageView imageView = findViewById(R.id.backdrop_image);
        final TextView title = findViewById(R.id.title);
        final CollapsingToolbarLayout colapLayout = findViewById(R.id.colap_toolbar_layout);
        Toolbar toolbar = findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Movie movie = getIntent().getParcelableExtra("Movie");
        // Set focus to the textview for the ellipSize to work
        title.setSelected(true);
        title.setText(movie.getTitle());

        Picasso.with(this)
                .load(Urls.POSTER_IMAGE_BASE_URL + Urls.POSTER_IMAGE_SIZE + movie.getBackdrop())
                .placeholder(R.drawable.poster_replace_image)
                .error(R.drawable.poster_replace_image)
                .fit()
                .centerInside()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        Palette palette = Palette.from(bitmap).maximumColorCount(32).generate();
                        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                        //Palette.Swatch lightVibrantSwatch = palette.getLightVibrantSwatch();
                        if (vibrantSwatch != null) {
                            colapLayout.setBackgroundColor(vibrantSwatch.getRgb());
                            colapLayout.setStatusBarScrimColor(vibrantSwatch.getRgb());
                            colapLayout.setContentScrimColor(vibrantSwatch.getRgb());
                           // title.setTextColor(darkVibrantSwatch.getTitleTextColor());
                        } else {
                            colapLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.menuBackground));

                            //title.setTextColor(Color.WHITE);

                        }
                        //colapLayout.getBackground().setAlpha(160);
                        
                    }

                    @Override
                    public void onError() {
                        Log.e(MovieAdapter.class.getSimpleName(), "Picasso error!!");
                    }

                });
    }

}
