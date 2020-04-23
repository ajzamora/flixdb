package com.ajzamora.flixdb.utils;

import com.ajzamora.flixdb.models.Movie;
import com.ajzamora.flixdb.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public final class TheMovieDbUtils {

    public static ArrayList<Movie> getSimpleMovieStringsFromJson(String movieJSONstr) throws JSONException {
        final int LOAD_TIME_IN_MILLISECONDS = 2000;
        try {
            Thread.sleep(LOAD_TIME_IN_MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject movieJson = new JSONObject(movieJSONstr);
        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        final String TMDB_TITLE = "title";
        final String TMDB_THUMBNAIL = "poster_path";
        final String TMDB_BACKDROP = "backdrop_path";
        final String TMDB_PLOT = "overview";
        final String TMDB_RATING = "vote_average";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_STATUS_CODE = "status_code";

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

        ArrayList<Movie> movies = new ArrayList<>();

        JSONObject baseJSON = new JSONObject(movieJSONstr);
        JSONArray resultsArray = baseJSON.getJSONArray(TMDB_RESULTS);
        for (int i = 0; i < resultsArray.length(); i++) {
            Movie.Builder movieBuilder = new Movie.Builder();
            JSONObject currentMovie = resultsArray.getJSONObject(i);
            String id = currentMovie.getString(TMDB_ID);
            String title = currentMovie.getString(TMDB_TITLE);
            String thumbnail = currentMovie.getString(TMDB_THUMBNAIL);
            String backdrop = currentMovie.getString(TMDB_BACKDROP);
            String plot = currentMovie.getString(TMDB_PLOT);
            String rating = currentMovie.getString(TMDB_RATING);
            String releaseDate = currentMovie.getString(TMDB_RELEASE_DATE);

            movieBuilder.id(id)
                    .title(title)
                    .thumbnail(thumbnail)
                    .backdrop(backdrop)
                    .plot(plot)
                    .rating(rating)
                    .releaseDate(releaseDate);
            movies.add(movieBuilder.build());
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
}
