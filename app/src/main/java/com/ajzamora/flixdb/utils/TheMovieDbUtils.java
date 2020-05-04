package com.ajzamora.flixdb.utils;

import com.ajzamora.flixdb.models.Movie;
import com.ajzamora.flixdb.models.Review;
import com.ajzamora.flixdb.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public final class TheMovieDbUtils {

    public static final String TMDB_RESULTS = "results";
    public static final String TMDB_ID = "id";
    public static final String TMDB_TITLE = "title";
    public static final String TMDB_THUMBNAIL = "poster_path";
    public static final String TMDB_BACKDROP = "backdrop_path";
    public static final String TMDB_PLOT = "overview";
    public static final String TMDB_RATING = "vote_average";
    public static final String TMDB_RELEASE_DATE = "release_date";
    public static final String TMDB_STATUS_CODE = "status_code";

    public static Movie parseMovie(JSONObject movieJSONObj) throws JSONException {
        String id = movieJSONObj.getString(TMDB_ID);
        String title = movieJSONObj.getString(TMDB_TITLE);
        String thumbnail = movieJSONObj.getString(TMDB_THUMBNAIL);
        String backdrop = movieJSONObj.getString(TMDB_BACKDROP);
        String plot = movieJSONObj.getString(TMDB_PLOT);
        String rating = movieJSONObj.getString(TMDB_RATING);
        String releaseDate = movieJSONObj.getString(TMDB_RELEASE_DATE);

        return new Movie.Builder().id(id)
                .title(title)
                .thumbnail(thumbnail)
                .backdrop(backdrop)
                .plot(plot)
                .rating(rating)
                .releaseDate(releaseDate)
                .build();
    }

    public static JSONArray getMovieResultsJsonArray(String movieJSONstr) throws JSONException {
        final int LOAD_TIME_IN_MILLISECONDS = 2000;
        try {
            Thread.sleep(LOAD_TIME_IN_MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject movieJson = new JSONObject(movieJSONstr);
        if (movieJson.has(TMDB_STATUS_CODE)) {
            int errorCode = movieJson.getInt(TMDB_STATUS_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONObject baseJSON = new JSONObject(movieJSONstr);
        return baseJSON.getJSONArray(TMDB_RESULTS);
    }

    public static ArrayList<Movie> getSimpleMovieStringsFromJson(String movieJSONstr) throws JSONException {
        JSONArray resultsArray = getMovieResultsJsonArray(movieJSONstr);
        ArrayList<Movie> movies = new ArrayList<>();
        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject currentMovieJSONObj = resultsArray.getJSONObject(i);
            Movie currentMovie = parseMovie(currentMovieJSONObj);
            movies.add(currentMovie);
        }
        return movies;
    }

    // TODO: Refactor
    public static ArrayList<Trailer> getSimpleTrailerStringsFromJson(String trailerJSONstr) throws JSONException {
        final int LOAD_TIME_IN_MILLISECONDS = 2000;
        try {
            Thread.sleep(LOAD_TIME_IN_MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject trailerJson = new JSONObject(trailerJSONstr);
        final String TRAILER_RESULTS = "results";
        final String TRAILER_ID = "id";
        final String TRAILER_KEY = "key";
        final String TRAILER_SITE = "site";
        final String TRAILER_STATUS_CODE = "status_code";

        if (trailerJson.has(TRAILER_STATUS_CODE)) {
            int errorCode = trailerJson.getInt(TRAILER_STATUS_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        ArrayList<Trailer> trailers = new ArrayList<>();
        JSONObject baseJSON = new JSONObject(trailerJSONstr);
        JSONArray resultsArray = baseJSON.getJSONArray(TRAILER_RESULTS);
        for (int i = 0; i < resultsArray.length(); i++) {
            Trailer.Builder trailerBuilder = new Trailer.Builder();
            JSONObject currentMovie = resultsArray.getJSONObject(i);
            String id = currentMovie.getString(TRAILER_ID);
            String key = currentMovie.getString(TRAILER_KEY);
            String site = currentMovie.getString(TRAILER_SITE);

            trailerBuilder.id(id)
                    .key(key)
                    .site(site);
            trailers.add(trailerBuilder.build());
        }
        return trailers;
    }

    public static ArrayList<Review> getSimpleReviewStringsFromJson(String reviewJSONstr) throws JSONException {
        final int LOAD_TIME_IN_MILLISECONDS = 2000;
        try {
            Thread.sleep(LOAD_TIME_IN_MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject reviewJson = new JSONObject(reviewJSONstr);
        final String REVIEW_RESULTS = "results";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        final String REVIEW_STATUS_CODE = "status_code";

        if (reviewJson.has(REVIEW_STATUS_CODE)) {
            int errorCode = reviewJson.getInt(REVIEW_STATUS_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        ArrayList<Review> reviews = new ArrayList<>();
        JSONObject baseJSON = new JSONObject(reviewJSONstr);
        JSONArray resultsArray = baseJSON.getJSONArray(REVIEW_RESULTS);
        for (int i = 0; i < resultsArray.length(); i++) {
            Review.Builder reviewBuilder = new Review.Builder();
            JSONObject currentReview = resultsArray.getJSONObject(i);
            String author = currentReview.getString(REVIEW_AUTHOR);
            String content = currentReview.getString(REVIEW_CONTENT);

            reviewBuilder.author(author)
                    .content(content);

            reviews.add(reviewBuilder.build());
        }
        return reviews;
    }
}
