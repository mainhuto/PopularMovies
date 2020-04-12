package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private static final String APPEND_USER_RATING = "/10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        Movie movie = null;
        if ( ( intent != null ) && intent.hasExtra(getString(R.string.extra_selected_movie)) ) {
            movie = (Movie)intent.getParcelableExtra(getResources().getString(R.string.extra_selected_movie));
        }

        if ( movie != null ) {

            TextView titleTextView = findViewById(R.id.title_tv);
            TextView releaseDataTextView = findViewById(R.id.release_date_tv);
            TextView userRatingTextView = findViewById(R.id.user_rating_tv);
            TextView plotSynopsisTextView = findViewById(R.id.plot_synopsis_tv);
            ImageView thumbnailImageView = findViewById(R.id.thumbnail_iv);

            titleTextView.setText(movie.getTitle());
            releaseDataTextView.setText(movie.getReleaseDate());
            String userRating = movie.getUserRating() + APPEND_USER_RATING;
            userRatingTextView.setText(userRating);
            plotSynopsisTextView.setText(movie.getPlotSynopsis());

            String movieDBImageHost = getResources().getString(R.string.tmdb_image_base_url);
            String url = movie.getPosterUrl(movieDBImageHost);
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder)
                    .into(thumbnailImageView);

        }

    }

}
