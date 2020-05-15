package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.android.popularmovies.database.AppDatabase;
import com.example.android.popularmovies.database.DbExecutor;
import com.example.android.popularmovies.database.FavMovieEntry;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.utils.AppAsyncHttpLoader;
import com.example.android.popularmovies.utils.TMDBUtil;

import java.net.URL;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements DetailAdapter.DetailAdapterOnClickHandler, LoaderManager.LoaderCallbacks<List<String>> {

    private static final int MOVIE_DETAIL_SEARCH_LOADER = 22;

    private Movie movie;
    private AppDatabase moviesDB;
    private DetailViewModel viewModel;
    private DetailAdapter detailAdapter;
    private RecyclerView detailRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        moviesDB = AppDatabase.getInstance(getApplicationContext());

        Intent intent = getIntent();
        movie = null;
        boolean fromFavoriteList = false;
        if ( ( intent != null ) && intent.hasExtra(getString(R.string.extra_selected_movie)) ) {
            if ( intent.hasExtra(getString(R.string.extra_selected_movie)) ) {
                movie = (Movie)intent.getParcelableExtra(getResources().getString(R.string.extra_selected_movie));
            }
            if ( intent.hasExtra(getString(R.string.extra_from_favorite)) ) {
                fromFavoriteList = intent.getBooleanExtra(getString(R.string.extra_from_favorite), false);
            }
        } else {
            getSupportLoaderManager().initLoader(MOVIE_DETAIL_SEARCH_LOADER, null, this);
        }

        if ( movie != null ) {

            String queryBaseUrl = getResources().getString(R.string.tmdb_query_base_url);
            String apiKey = BuildConfig.TMDB_API_KEY;
            URL trailerQueryUrl = TMDBUtil.buildTrailerUrl(queryBaseUrl, movie.getTmdbMovieId(), apiKey);
            URL reviewQueryUrl = TMDBUtil.buildReviewUrl(queryBaseUrl, movie.getTmdbMovieId(), apiKey);

            Bundle queryBundle = new Bundle();
            queryBundle.putString(AppAsyncHttpLoader.SEARCH_QUERY_URL_1_EXTRA, trailerQueryUrl.toString());
            queryBundle.putString(AppAsyncHttpLoader.SEARCH_QUERY_URL_2_EXTRA, reviewQueryUrl.toString());

            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(MOVIE_DETAIL_SEARCH_LOADER, queryBundle, this);

            detailRecyclerView = findViewById(R.id.detail_rv);

            AppDatabase viewModelDatabase;
            if (fromFavoriteList) {
                viewModelDatabase = null;
            } else {
                viewModelDatabase = moviesDB;
            }

            DetailViewModelFactory factory = new DetailViewModelFactory(viewModelDatabase, movie.getFavMovieId(), movie.getTmdbMovieId());
            viewModel = ViewModelProviders.of(this, factory).get(DetailViewModel.class);
            LiveData<FavMovieEntry> favMovie = viewModel.getFavMovie();
            if (favMovie != null) {
                favMovie.observe(this, new Observer<FavMovieEntry>() {
                    @Override
                    public void onChanged(@Nullable FavMovieEntry favMovieEntry) {
                        viewModel.getFavMovie().removeObserver(this);
                        if (favMovieEntry != null) {
                            movie.setFavMovieId(favMovieEntry.getId());
                            detailAdapter.setMovie(movie);
                        }
                    }
                });
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            detailRecyclerView.setLayoutManager(layoutManager);
            detailAdapter = new DetailAdapter(movie, this);
            detailRecyclerView.setAdapter(detailAdapter);

        }

    }

    private void addFavoriteMovie() {
        final FavMovieEntry favMovieEntry = new FavMovieEntry(movie.getTitle(), movie.getImagePath(), movie.getPlotSynopsis(), movie.getUserRating(), 102, movie.getReleaseDate(), movie.getTmdbMovieId());
        DbExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                long id = moviesDB.favMovieDao().insertFavMovie(favMovieEntry);
                movie.setFavMovieId(id);
            }
        });
    }

    private void deleteFavoriteMovie() {
        final LiveData<FavMovieEntry> favMovie = moviesDB.favMovieDao().loadMovieById(movie.getFavMovieId());
        favMovie.observe(this, new Observer<FavMovieEntry>() {
            @Override
            public void onChanged(@Nullable final FavMovieEntry favMovieEntry) {
                favMovie.removeObserver(this);
                if ( favMovieEntry != null) {
                    movie.unmarkFavorite();
                    DbExecutor.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            moviesDB.favMovieDao().deleteFavMovie(favMovieEntry);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClickTrailer(Trailer trailer) {
        Uri youtubeUri = Uri.parse("https://www.youtube.com/watch?v=" + trailer.getKey());
        Intent intent = new Intent(Intent.ACTION_VIEW, youtubeUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.need_app_to_watch_trailer_text), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClinkStar() {
        if ( movie.isFavorite() ) {
            deleteFavoriteMovie();
        } else {
            addFavoriteMovie();
        }
    }

    @NonNull
    @Override
    public Loader<List<String>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new AppAsyncHttpLoader(this, bundle);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<String>> loader, List<String> data) {
        if ( ( data != null ) && (data.size() == 2) ) {
            List<Trailer> trailers = TMDBUtil.createTrailerList(data.get(0));
            List<Review> reviews = TMDBUtil.createReviewList(data.get(1));
            if ( (trailers != null ) ) {
                detailAdapter.setDetailLists(trailers, reviews);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<String>> loader) {

    }
}
