package com.example.android.popularmovies.utils;

import android.net.Uri;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TMDBUtil {

    public enum SortBy {
        MOST_POPULAR("/popular", 11),
        HIGHEST_RATED("/top_rated", 12),
        FAVORITES(null, 0);

        private final String path;
        private int loaderId;

        SortBy(String path, int loaderId) {
            this.path = path;
            this.loaderId = loaderId;
        }

        String getPath() {
            return path;
        }

        public int getLoaderId() {
            return loaderId;
        }
    }

    private final static String API_KEY_PARAM = "api_key";
    private static final String RESULTS_KEY = "results";
    private static final String TITLE_KEY = "title";
    private static final String IMAGE_PATH_KEY = "poster_path";
    private static final String PLOT_SYNOPSIS_KEY = "overview";
    private static final String USER_RATING_KEY = "vote_average";
    private static final String RELEASE_DATE_KEY = "release_date";
    private static final String ID_KEY = "id";
    private static final String TRAILER_NAME_KEY = "name";
    private static final String TRAILER_KEY_KEY = "key";
    private static final String TRAILER_URL_PATH = "videos";
    private static final String REVIEW_AUTHOR_KEY = "author";
    private static final String REVIEW_CONTENT_KEY = "content";
    private static final String REVIEW_URL_PATH = "reviews";

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
                    int id = result.optInt(ID_KEY);
                    String plotSynopsis = result.optString(PLOT_SYNOPSIS_KEY);
                    double userRating = result.optDouble(USER_RATING_KEY);
                    String releaseDate = result.optString(RELEASE_DATE_KEY);
                    long releaseDateTime;
                    try {
                        Date date = new SimpleDateFormat(Movie.DATE_FORMAT).parse(releaseDate);
                        releaseDateTime = date.getTime();
                    } catch (ParseException e) {
                        releaseDateTime = 0;
                    }
                    Movie movie = new Movie(title, imagePath, plotSynopsis, userRating, releaseDateTime, id);
                    movies.add(movie);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }

    public static List<Trailer> createTrailerList(String jsonString) {

        List<Trailer> trailers = new ArrayList<>();

        if ( jsonString != null ) {
            try {

                JSONObject moviesJSON = new JSONObject(jsonString);

                List<String> results = new ArrayList<>();
                JSONArray resultsList = moviesJSON.optJSONArray(RESULTS_KEY);
                if ( resultsList != null ) {
                    for (int index = 0; index < resultsList.length(); index++) {
                        JSONObject result = resultsList.getJSONObject(index);
                        String name = result.optString(TRAILER_NAME_KEY);
                        String key = result.optString(TRAILER_KEY_KEY);
                        Trailer trailer = new Trailer(name, key);
                        trailers.add(trailer);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return trailers;
    }

    public static List<Review> createReviewList(String jsonString) {

        List<Review> reviews = new ArrayList<>();

        if ( jsonString != null ) {
            try {

                JSONObject moviesJSON = new JSONObject(jsonString);

                List<String> results = new ArrayList<>();
                JSONArray resultsList = moviesJSON.optJSONArray(RESULTS_KEY);
                if ( resultsList != null ) {
                    for (int index = 0; index < resultsList.length(); index++) {
                        JSONObject result = resultsList.getJSONObject(index);
                        String author = result.optString(REVIEW_AUTHOR_KEY);
                        String content = result.optString(REVIEW_CONTENT_KEY);
                        Review review = new Review(author, content);
                        reviews.add(review);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return reviews;
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

    public static URL buildTrailerUrl(String baseUrl, int movieId, String apiKey) {
        return buildDetailUrl(baseUrl, movieId, apiKey, TRAILER_URL_PATH);
    }

    public static URL buildReviewUrl(String baseUrl, int movieId, String apiKey) {
        return buildDetailUrl(baseUrl, movieId, apiKey, REVIEW_URL_PATH);
    }

    public static URL buildDetailUrl(String baseUrl, int movieId, String apiKey, String path) {
        Uri uri = Uri.parse(baseUrl + "/" + movieId + "/" + path).buildUpon()
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
