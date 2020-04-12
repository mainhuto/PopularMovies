package com.example.android.popularmovies.utils;

import android.net.Uri;

import com.example.android.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TMDBUtil {

    public enum SortBy {
        MOST_POPULAR("/popular"),
        HIGHEST_RATED("/top_rated");

        private final String path;

        SortBy(String path) {
            this.path = path;
        }

        String getPath() {
            return path;
        }
    }

    private final static String API_KEY_PARAM = "api_key";
    private static final String RESULTS_KEY = "results";
    private static final String TITLE_KEY = "title";
    private static final String IMAGE_PATH_KEY = "poster_path";
    private static final String PLOT_SYNOPSIS_KEY = "overview";
    private static final String USER_RATING_KEY = "vote_average";
    private static final String RELEASE_DATE_KEY = "release_date";

    public static ArrayList<Movie> createMovieList(String jsonString) {

        ArrayList<Movie> movies = new ArrayList<>();

        try {

            JSONObject moviesJSON = new JSONObject(jsonString);

            List<String> results = new ArrayList<>();
            JSONArray resultsList = moviesJSON.optJSONArray(RESULTS_KEY);
            if ( resultsList != null ) {
                for (int index = 0; index < resultsList.length(); index++) {
                    JSONObject result = resultsList.getJSONObject(index);
                    String title = result.optString(TITLE_KEY);
                    String imagePath = result.optString(IMAGE_PATH_KEY);
                    String plotSynopsis = result.optString(PLOT_SYNOPSIS_KEY);
                    String userRating = result.optString(USER_RATING_KEY);
                    String releaseDate = result.optString(RELEASE_DATE_KEY);
                    Movie movie = new Movie(title, imagePath, plotSynopsis, userRating, releaseDate);
                    movies.add(movie);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }

    public static URL buildUrl(String baseUrl, SortBy sortBy, String apiKey) {
        Uri uri = Uri.parse(baseUrl + sortBy.getPath()).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


}
