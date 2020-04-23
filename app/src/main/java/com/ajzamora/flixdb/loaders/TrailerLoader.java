package com.ajzamora.flixdb.loaders;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.ajzamora.flixdb.models.Trailer;
import com.ajzamora.flixdb.utils.NetworkUtils;
import com.ajzamora.flixdb.utils.TheMovieDbUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class TrailerLoader extends AsyncTaskLoader<List<Trailer>> {
    private static final String LOG_TAG = TrailerLoader.class.getName();

    private String mApiKey;
    private String mId;
    private List<Trailer> mTrailers;

    public TrailerLoader(Context context, String apiKey, String id) {
        super(context);
        mApiKey = apiKey;
        mId = id;
    }

    @Override
    protected void onStartLoading() {
        if (mTrailers != null) {
            deliverResult(mTrailers);
        } else if (takeContentChanged() || mTrailers == null) {
            forceLoad();
        }
    }

    public void deliverResult(List<Trailer> trailers) {
        mTrailers = trailers;
        super.deliverResult(trailers);;
    }

    @Nullable
    @Override
    public List<Trailer> loadInBackground() {
        if (TextUtils.isEmpty(mApiKey)) return null;
        URL trailerRequestUrl = NetworkUtils.buildTrailerUrl(mApiKey, mId);

        List<Trailer> simpleJsonTrailerData = null;
        String jsonTrailerResponse = null;
        try {
            jsonTrailerResponse = NetworkUtils
                    .getResponseFromHttpUrl(trailerRequestUrl);

            simpleJsonTrailerData = TheMovieDbUtils
                    .getSimpleTrailerStringsFromJson(jsonTrailerResponse);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the trailer JSON results.", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the trailer JSON results: " + jsonTrailerResponse, e);
        }

        return simpleJsonTrailerData;
    }
}
