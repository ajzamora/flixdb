package com.ajzamora.flixdb.utils;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public final class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3";
    private static final String TMDB_MOVIE_URL = TMDB_BASE_URL.concat("/movie");
    public static int STATUS_CODE = -1;

    final static String API_PARAM = "api_key";

    // TODO: Refactor
    public static Uri buildVideoUri(String videoSite, String videoKey) {
        final String YOUTUBE = "YouTube";
        final String VIMEO = "Vimeo";
        final String YOUTUBE_AUTHORITY = "www.youtube.com";
        final String YOUTUBE_VIDEO_PATH = "watch";
        final String YOUTUBE_VIDEO_PARAM = "v";
        final String VIMEO_AUTHORITY = "www.vimeo.com";

        Uri.Builder uriBuilder = new Uri.Builder().scheme("https");
        switch(videoSite) {
            case YOUTUBE:
                uriBuilder.authority(YOUTUBE_AUTHORITY);
                uriBuilder.appendPath(YOUTUBE_VIDEO_PATH);
                uriBuilder.appendQueryParameter(YOUTUBE_VIDEO_PARAM, videoKey);
                break;
            case VIMEO:
                uriBuilder.authority(VIMEO_AUTHORITY);
                uriBuilder.appendPath(videoKey);
                break;
            default: return null;
        }
        return uriBuilder.build();
    }

    public static URL buildTrailerUrl(String api, String id) {
        Uri builtUri = Uri.parse(TMDB_MOVIE_URL).buildUpon()
                .appendPath(id)
                .appendPath("videos")
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
        String jsonResponse = "";
        if (url == null) return jsonResponse;
        HttpURLConnection urlConnection = null;
        try {
            InputStream inputStream = null;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            STATUS_CODE = urlConnection.getResponseCode();
            switch (STATUS_CODE) {
                case HttpURLConnection.HTTP_OK:
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                default:
                    Log.e(LOG_TAG, "HTTPresponse: " + STATUS_CODE);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
        } finally {
            urlConnection.disconnect();
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }
}
