package com.ajzamora.flixdb.utils;

import com.ajzamora.flixdb.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public final class TheMovieDbUtils {

    public static ArrayList<Movie> getSimpleMovieStringsFromJson(String movieJSON) throws JSONException {
        JSONObject forecastJson = new JSONObject(movieJSON);

        final String TMDB_RESULTS = "results";
        final String TMDB_TITLE = "title";
        final String TMDB_THUMBNAIL = "poster_path";
        final String TMDB_PLOT = "overview";
        final String TMDB_RATING = "vote_average";
        final String TMDB_RELEASE_DATE = "release_date";

        final String TMDB_STATUS_CODE = "status_code";

        if (forecastJson.has(TMDB_STATUS_CODE)) {
            int errorCode = forecastJson.getInt(TMDB_STATUS_CODE);

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

        JSONObject baseJSON = new JSONObject(movieJSON);
        JSONArray resultsArray = baseJSON.getJSONArray(TMDB_RESULTS);
        for (int i = 0; i < resultsArray.length(); i++) {
            Movie.Builder movieBuilder = new Movie.Builder();
            JSONObject currentMovie = resultsArray.getJSONObject(i);
            String title = currentMovie.getString(TMDB_TITLE);
            String thumbnail = currentMovie.getString(TMDB_THUMBNAIL);
            String plot = currentMovie.getString(TMDB_PLOT);
            String rating = currentMovie.getString(TMDB_RATING);
            String releaseDate = currentMovie.getString(TMDB_RELEASE_DATE);

            movieBuilder.title(title)
                    .thumbnail(thumbnail)
                    .plot(plot)
                    .rating(rating)
                    .releaseDate(releaseDate);
            movies.add(movieBuilder.build());
        }
        return movies;
    }
}
