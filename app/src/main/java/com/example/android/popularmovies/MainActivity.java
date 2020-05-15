package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.database.FavMovieEntry;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utils.AppAsyncHttpLoader;
import com.example.android.popularmovies.utils.TMDBUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler
        , View.OnClickListener, LoaderManager.LoaderCallbacks<List<String>>, AppAsyncHttpLoader.LoaderListener {

    private RecyclerView mMoviesRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mNoConnectionTextView;
    private View mNoFavoritesLayout;
    private MovieAdapter movieAdapter;
    private String queryBaseUrl;
    private String apiKey;
    private TMDBUtil.SortBy sortBy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryBaseUrl = getResources().getString(R.string.tmdb_query_base_url);
        apiKey = BuildConfig.TMDB_API_KEY;
        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.movies_rv);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_pb);
        mNoConnectionTextView = (TextView) findViewById(R.id.no_connection_tv);
        mNoFavoritesLayout = (View) findViewById(R.id.no_favorites_tv);
        mNoConnectionTextView.setOnClickListener(this);

        retrievePreferences();

        int posterWidth = (int)getResources().getDimension(R.dimen.poster_width);
        int spanCount = calculateBestSpanCount(posterWidth);

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), spanCount);
        mMoviesRecyclerView.setLayoutManager(layoutManager);
        movieAdapter = new MovieAdapter(getResources().getString(R.string.tmdb_image_base_url), this);
        mMoviesRecyclerView.setAdapter(movieAdapter);

        if ( (savedInstanceState != null) && savedInstanceState.containsKey(getResources().getString(R.string.bundle_movies_key)) ) {
            ArrayList<Movie> movies = savedInstanceState.getParcelableArrayList(getResources().getString(R.string.bundle_movies_key));
            movieAdapter.setMovies(movies);
        } else {
            fetchMoviesData();
        }

        setupViewModel();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem item;
        switch (sortBy) {
            case MOST_POPULAR:
                item = menu.findItem(R.id.most_popular_sort);
                break;
            case HIGHEST_RATED:
                item = menu.findItem(R.id.highest_rated_sort);
                break;
            case FAVORITES:
                item = menu.findItem(R.id.favorites);
                break;
            default:
                item = menu.findItem(R.id.most_popular_sort);
                break;
        }
        item.setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.most_popular_sort:
                changeSortBy(TMDBUtil.SortBy.MOST_POPULAR);
                return true;
            case R.id.highest_rated_sort:
                changeSortBy(TMDBUtil.SortBy.HIGHEST_RATED);
                return true;
            case R.id.favorites:
                changeSortBy(TMDBUtil.SortBy.FAVORITES);
                return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ArrayList<Movie> movies = (ArrayList<Movie>) movieAdapter.getMovies();
        if ( ( movies != null ) && movies.size() > 0 ) {
            outState.putParcelableArrayList(getString(R.string.bundle_movies_key), movies);
        }
        super.onSaveInstanceState(outState);
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.geFavMovies().observe(this, new Observer<List<FavMovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<FavMovieEntry> favMovieEntries) {
                if (TMDBUtil.SortBy.FAVORITES.equals(sortBy)) {
                    loadMovies(favMovieEntries);
                }
            }
        });
    }

    private void fetchMoviesData() {
        if (!TMDBUtil.SortBy.FAVORITES.equals(sortBy)) {
            movieAdapter.setMovies(new ArrayList<Movie>());
            URL url = TMDBUtil.buildUrl(queryBaseUrl, sortBy, apiKey);
            if ( url != null ) {
                hideViews();
                Bundle queryBundle = new Bundle();
                queryBundle.putString(AppAsyncHttpLoader.SEARCH_QUERY_URL_1_EXTRA, url.toString());
                getSupportLoaderManager().restartLoader(sortBy.getLoaderId(), queryBundle, this);
            } else {
                showNoConnectionMessage();
            }
        }
    }

    private void hideViews() {
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);
        mNoFavoritesLayout.setVisibility(View.INVISIBLE);
        mNoConnectionTextView.setVisibility(View.INVISIBLE);
    }

    private void loadMovies(List<FavMovieEntry> movieEntryList) {
        if ( ( movieEntryList != null ) && movieEntryList.size() > 0 ) {
            ArrayList<Movie> movies = new ArrayList<>();
            for (FavMovieEntry favMovieEntry: movieEntryList) {
                Movie movie = new Movie(
                        favMovieEntry.getTitle(),
                        favMovieEntry.getImagePath(),
                        favMovieEntry.getPlotSynopsis(),
                        favMovieEntry.getUserRating(),
                        favMovieEntry.getReleaseDate().getTime(),
                        favMovieEntry.getTmdbMovieId()
                );
                movie.setFavMovieId(favMovieEntry.getId());
                movies.add(movie);
            }
            if ( movies.size() > 0 ) {
                mMoviesRecyclerView.setVisibility(View.VISIBLE);
                movieAdapter.setMovies(movies);
            } else {
                showNoFavoritesMessage();
            }
        } else {
            showNoFavoritesMessage();
        }
    }

    private void loadMovies(String fetchedData) {
        if ( !TextUtils.isEmpty(fetchedData) ) {
            ArrayList<Movie> movies = TMDBUtil.createMovieList(fetchedData);
            if ( movies.size() > 0 ) {
                movieAdapter.setMovies(movies);
            } else {
                showNoConnectionMessage();
            }
        } else {
            showNoConnectionMessage();
        }
    }

    private void showNoConnectionMessage() {
        mNoConnectionTextView.setVisibility(View.VISIBLE);
        mNoFavoritesLayout.setVisibility(View.INVISIBLE);
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showNoFavoritesMessage() {
        mNoFavoritesLayout.setVisibility(View.VISIBLE);
        mNoConnectionTextView.setVisibility(View.INVISIBLE);
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(Movie movie) {
        boolean fromFavoriteList = false;
        if (TMDBUtil.SortBy.FAVORITES.equals(sortBy)) {
            fromFavoriteList = true;
        }
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(getString(R.string.extra_selected_movie), movie);
        intent.putExtra(getString(R.string.extra_from_favorite), fromFavoriteList);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        fetchMoviesData();
    }

    @NonNull
    @Override
    public Loader<List<String>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new AppAsyncHttpLoader(this, bundle);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<String>> loader, List<String> data) {
        if (loader.getId() == sortBy.getLoaderId()) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if ( ( data != null) && ( data.size() > 0 ) ) {
                mMoviesRecyclerView.setVisibility(View.VISIBLE);
                loadMovies(data.get(0));
            } else {
                showNoConnectionMessage();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<String>> loader) {

    }

    @Override
    public void onForceLoad() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void retrievePreferences() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if ( sharedPref.contains(getString(R.string.saved_sort_by_key))) {
            int intSortBy = sharedPref.getInt(getString(R.string.saved_sort_by_key), TMDBUtil.SortBy.MOST_POPULAR.ordinal());
            sortBy = TMDBUtil.SortBy.values()[intSortBy];
        } else {
            sortBy = TMDBUtil.SortBy.MOST_POPULAR;
            savePreferences();
        }
    }

    private void savePreferences() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.saved_sort_by_key), sortBy.ordinal());
        editor.apply();
    }

    private void changeSortBy(TMDBUtil.SortBy newSortBy) {
        if (!sortBy.equals(newSortBy)) {
            sortBy = newSortBy;
            savePreferences();
            if (TMDBUtil.SortBy.FAVORITES.equals(sortBy)) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mNoConnectionTextView.setVisibility(View.INVISIBLE);
                setupViewModel();
            } else {
                fetchMoviesData();
            }
            mMoviesRecyclerView.scrollToPosition(0);
        }
    }

    private int calculateBestSpanCount(int posterWidth) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float screenWidth = outMetrics.widthPixels;
        return Math.round(screenWidth / posterWidth);
    }

}
