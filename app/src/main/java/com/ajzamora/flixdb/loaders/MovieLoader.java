package com.ajzamora.flixdb.loaders;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.ajzamora.flixdb.MainActivity;
import com.ajzamora.flixdb.models.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {
    private static final String LOG_TAG = MovieLoader.class.getName();

    private String mUrl;
    private List<Movie> mMovies;

    public MovieLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        if (mMovies != null) {
            deliverResult(mMovies);
        }
        if (takeContentChanged() || mMovies == null) {
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
        if (TextUtils.isEmpty(mUrl)) return null;
        Log.i(LOG_TAG, "Uri: " + mUrl);
        return new ArrayList<>();
    }
}
