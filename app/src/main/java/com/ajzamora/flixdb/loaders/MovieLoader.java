package com.ajzamora.flixdb.loaders;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.ajzamora.flixdb.models.Movie;
import com.ajzamora.flixdb.utils.NetworkUtils;
import com.ajzamora.flixdb.utils.TheMovieDbUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {
    private static final String LOG_TAG = MovieLoader.class.getName();

    private String mApiKey;
    private String mSortOrder;
    private List<Movie> mMovies;

    public MovieLoader(Context context, String apiKey, String sortOrder) {
        super(context);
        mApiKey = apiKey;
        mSortOrder = sortOrder;
    }

    @Override
    protected void onStartLoading() {
        if (mMovies != null) {
            deliverResult(mMovies);
        } else if (takeContentChanged() || mMovies == null) {
            forceLoad();
        }
    }

    public void deliverResult(List<Movie> movies) {
        mMovies = movies;
        super.deliverResult(movies);
    }

    @Nullable
    @Override
    public List<Movie> loadInBackground() {
        if (TextUtils.isEmpty(mApiKey)) return null;
        URL movieRequestUrl = NetworkUtils.buildUrl(mApiKey, mSortOrder);

        List<Movie> simpleJsonMovieData = null;
        String jsonMovieResponse = null;
        try {
            jsonMovieResponse = NetworkUtils
                    .getResponseFromHttpUrl(movieRequestUrl);

            simpleJsonMovieData = TheMovieDbUtils
                    .getSimpleMovieStringsFromJson(jsonMovieResponse);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movie JSON results: " + jsonMovieResponse, e);
        }

        return simpleJsonMovieData;
    }
}
