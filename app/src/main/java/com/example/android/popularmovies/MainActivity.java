package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utils.TMDBUtil;
import com.example.android.popularmovies.utils.NetworkUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, View.OnClickListener {

    private ArrayList<Movie> movies;
    private RecyclerView mMoviesRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mNoConnectionTextView;
    private MovieAdapter movieAdapter;
    private String queryBaseUrl;
    private String apiKey;
    private TMDBUtil.SortBy sortBy;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryBaseUrl = getResources().getString(R.string.tmdb_query_base_url);
        apiKey = getResources().getString(R.string.tmdb_api_key);
        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.movies_rv);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_pb);
        mNoConnectionTextView = (TextView) findViewById(R.id.no_connection_tv);
        mNoConnectionTextView.setOnClickListener(this);

        retrievePreferences();

        int spanCount;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            spanCount = 3;
        } else {
            spanCount = 2;
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), spanCount);
        mMoviesRecyclerView.setLayoutManager(layoutManager);
        movieAdapter = new MovieAdapter(getResources().getString(R.string.tmdb_image_base_url), this);
        mMoviesRecyclerView.setAdapter(movieAdapter);

        if ( (savedInstanceState != null) && savedInstanceState.containsKey(getResources().getString(R.string.bundle_movies_key)) ) {
            movies = savedInstanceState.getParcelableArrayList(getResources().getString(R.string.bundle_movies_key));
            movieAdapter.setMovies(movies);
        } else {
            fetchMoviesData();
        }

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
        }
        return false;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if ( ( movies != null ) && movies.size() > 0 ) {
            outState.putParcelableArrayList(getString(R.string.bundle_movies_key), movies);
        }
        super.onSaveInstanceState(outState);
    }

    private void fetchMoviesData() {
        movies = new ArrayList<>();
        URL url = TMDBUtil.buildUrl(queryBaseUrl, sortBy, apiKey);
        if ( url != null ) {
            mMoviesRecyclerView.setVisibility(View.INVISIBLE);
            mNoConnectionTextView.setVisibility(View.INVISIBLE);
            FetchDataFromTMDB fetchDataFromTMDB = new FetchDataFromTMDB();
            fetchDataFromTMDB.execute(url);
        } else {
            showNoConnectionMessage();
        }
    }

    private void loadMovies(String fetchedData) {
        if ( !TextUtils.isEmpty(fetchedData) ) {
            movies = TMDBUtil.createMovieList(fetchedData);
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
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(getString(R.string.extra_selected_movie), movie);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        fetchMoviesData();
    }

    class FetchDataFromTMDB extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            String fetchedData = null;
            if ( urls.length == 1 ) {
                URL url = urls[0];
                try {
                    fetchedData = NetworkUtil.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return fetchedData;
        }

        @Override
        protected void onPostExecute(String fetchedData) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mMoviesRecyclerView.setVisibility(View.VISIBLE);
            loadMovies(fetchedData);
        }
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
            fetchMoviesData();
            mMoviesRecyclerView.scrollToPosition(0);
        }
    }

}
