package com.ajzamora.flixdb.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3";
    private static final String TMDB_MOVIE_URL = TMDB_BASE_URL.concat("/movie");

    final static String API_PARAM = "api_key";

    public static URL buildUrl(String api, String sortOrder) {
        Uri builtUri = Uri.parse(TMDB_MOVIE_URL).buildUpon()
                .appendPath(sortOrder)
                .appendQueryParameter(API_PARAM, api)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
        }

        Log.v(LOG_TAG, "Built URI " + url);
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
