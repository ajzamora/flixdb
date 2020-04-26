package com.ajzamora.flixdb.loaders;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.ajzamora.flixdb.models.Review;
import com.ajzamora.flixdb.utils.NetworkUtils;
import com.ajzamora.flixdb.utils.TheMovieDbUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ReviewLoader extends AsyncTaskLoader<List<Review>> {
    private static final String LOG_TAG = ReviewLoader.class.getName();

    private String mApiKey;
    private String mId;
    private List<Review> mReviews;

    public ReviewLoader(Context context, String apiKey, String id) {
        super(context);
        mApiKey = apiKey;
        mId = id;
    }


    @Override
    protected void onStartLoading() {
        if (mReviews != null) {
            deliverResult(mReviews);
        } else if (takeContentChanged() || mReviews == null) {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(@Nullable List<Review> reviews) {
        mReviews = reviews;
        super.deliverResult(reviews);
    }

    @Nullable
    @Override
    public List<Review> loadInBackground() {
        if (TextUtils.isEmpty(mApiKey)) return null;
        URL reviewRequestUrl = NetworkUtils.buildReviewUrl(mApiKey, mId);

        List<Review> simpleJsonReviewData = null;
        String jsonReviewResponse = null;
        try {
            jsonReviewResponse = NetworkUtils
                    .getResponseFromHttpUrl(reviewRequestUrl);

            simpleJsonReviewData = TheMovieDbUtils
                    .getSimpleReviewStringsFromJson(jsonReviewResponse);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the review JSON results.", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the review JSON results: " + jsonReviewResponse, e);
        }
        return simpleJsonReviewData;
    }
}
