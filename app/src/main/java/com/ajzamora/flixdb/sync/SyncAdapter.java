package com.ajzamora.flixdb.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.ajzamora.flixdb.models.FlixPreferences;
import com.ajzamora.flixdb.models.Movie;
import com.ajzamora.flixdb.models.MovieContract;
import com.ajzamora.flixdb.models.MovieContract.MovieEntry;
import com.ajzamora.flixdb.utils.AccountUtils;
import com.ajzamora.flixdb.utils.NetworkUtils;
import com.ajzamora.flixdb.utils.TheMovieDbUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "SYNC_ADAPTER";

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    Context mContext;


    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        mContext = context;
    }

    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        mContext = context;
    }

    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {
        /*
         * Put the data transfer code here.
         */

        final String apiKey = FlixPreferences.getPreferredAPI(mContext);
        final String sortOrder = FlixPreferences.getPreferredSortOrder(mContext);
        if (TextUtils.isEmpty(apiKey)) return;

        Log.w(TAG, "Starting synchronization...");
        URL movieRequestUrl = NetworkUtils.buildUrl(apiKey, sortOrder);

        List<Movie> simpleJsonMovieData = null;
        String jsonMovieResponse = null;
        try {
            jsonMovieResponse = NetworkUtils
                    .getResponseFromHttpUrl(movieRequestUrl);

            // Synchronize our news feed
            syncMovies(syncResult, jsonMovieResponse);

            // Add any other things you may want to sync

        } catch (IOException ex) {
            Log.e(TAG, "Error IO synchronizing!", ex);
            syncResult.stats.numIoExceptions++;
        } catch (JSONException ex) {
            Log.e(TAG, "Error JSON synchronizing!", ex);
            syncResult.stats.numParseExceptions++;
        } catch (RemoteException | OperationApplicationException ex) {
            Log.e(TAG, "Error RemOp synchronizing!", ex);
            syncResult.stats.numAuthExceptions++;
        }

        Log.w(TAG, "Finished synchronization!");
    }

    /**
     * Performs synchronization of our pretend news feed source.
     *
     * @param syncResult Write our stats to this
     */
    private void syncMovies2(SyncResult syncResult, String jsonMovieResponse) throws IOException, JSONException, RemoteException, OperationApplicationException {
        // We need to collect all the network items in a hash table
        Log.i(TAG, "Fetching server entries...");
        Map<String, Movie> networkEntries = new HashMap<>();

        mContentResolver.delete(MovieEntry.CONTENT_URI, null, null);
        // Create list for batching ContentProvider transactions
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();
        JSONArray movieJSONArray = TheMovieDbUtils.getMovieResultsJsonArray(jsonMovieResponse);
        for (int i = 0; i < movieJSONArray.length(); i++) {
            Movie movie = TheMovieDbUtils.parseMovie(movieJSONArray.getJSONObject(i));
            networkEntries.put(movie.getId(), movie);
            Log.i(TAG, "Scheduling insert: " + movie.getTitle());
            batch.add(ContentProviderOperation.newInsert(MovieEntry.CONTENT_URI)
                    .withValue(MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle())
                    .withValue(MovieEntry.COLUMN_MOVIE_PLOT, movie.getPlot())
                    .withValue(MovieEntry.COLUMN_MOVIE_THUMBNAIL, movie.getThumbnail())
                    .withValue(MovieEntry.COLUMN_MOVIE_BACKDROP, movie.getBackdrop())
                    .withValue(MovieEntry.COLUMN_MOVIE_RATING, movie.getRating())
                    .withValue(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate())
                    .build());
            syncResult.stats.numInserts++;
        }

        // Synchronize by performing batch update
        Log.i(TAG, "Merge solution ready, applying batch update...");
        mContentResolver.applyBatch(MovieContract.CONTENT_AUTHORITY, batch);
        mContentResolver.notifyChange(MovieEntry.CONTENT_URI, // URI where data was modified
                null, // No local observer
                false); // IMPORTANT: Do not sync to network
    }

    /**
     * Performs synchronization of our pretend news feed source.
     *
     * @param syncResult Write our stats to this
     */
    private void syncMovies(SyncResult syncResult, String jsonMovieResponse) throws IOException, JSONException, RemoteException, OperationApplicationException {
        // We need to collect all the network items in a hash table
        Log.i(TAG, "Fetching server entries...");
        Map<String, Movie> networkEntries = new HashMap<>();

        JSONArray movieJSONArray = TheMovieDbUtils.getMovieResultsJsonArray(jsonMovieResponse);
        for (int i = 0; i < movieJSONArray.length(); i++) {
            Movie movie = TheMovieDbUtils.parseMovie(movieJSONArray.getJSONObject(i));
            networkEntries.put(movie.getId(), movie);
        }

        //        TODO: A
        mContentResolver.delete(MovieEntry.CONTENT_URI, null, null);
        // Create list for batching ContentProvider transactions
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();
        // Compare the hash table of network entries to all the local entries
        Log.i(TAG, "Fetching local entries...");
        Cursor moviesCursor = mContentResolver.query(MovieEntry.CONTENT_URI, null, null, null, null, null);
        assert moviesCursor != null;
        moviesCursor.moveToFirst();


        while (moviesCursor.moveToNext()) {
            syncResult.stats.numEntries++;
            Movie found;

            int idColIdx = moviesCursor.getColumnIndex(MovieEntry._ID);
            int titleColIdx = moviesCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_TITLE);
            int plotColIdx = moviesCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_PLOT);
            int thumbnailColIdx = moviesCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_THUMBNAIL);
            int backdropColIdx = moviesCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_BACKDROP);
            int rateColIdx = moviesCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RATING);
            int dateColIdx = moviesCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RELEASE_DATE);

            // Create local movie entry
            String id = moviesCursor.getString(idColIdx);
            String title = moviesCursor.getString(titleColIdx);
            String plot = moviesCursor.getString(plotColIdx);
            String thumbnail = moviesCursor.getString(thumbnailColIdx);
            String backdrop = moviesCursor.getString(backdropColIdx);
            String rate = moviesCursor.getString(rateColIdx);
            String date = moviesCursor.getString(dateColIdx);
            //        List<Trailer> trailers;
            //        List<Review> reviews;

            // Try to retrieve the local entry from network entries
            found = networkEntries.get(id);
            if (found != null) {
                // The entry exists, remove from hash table to prevent re-inserting it
                networkEntries.remove(id);

                // Check to see if it needs to be updated
                if (!title.equals(found.getTitle())
                        || !plot.equals(found.getPlot())
                        || !thumbnail.equals(found.getThumbnail())
                        || !backdrop.equals(found.getBackdrop())
                        || !rate.equals(found.getRating())
                        || !date.equals(found.getReleaseDate())
                ) {
                    // Batch an update for the existing record
                    Log.i(TAG, "Scheduling update: " + title);
                    batch.add(ContentProviderOperation.newUpdate(MovieEntry.CONTENT_URI)
                            .withSelection(MovieEntry._ID + "='" + id + "'", null)
                            .withValue(MovieEntry.COLUMN_MOVIE_TITLE, found.getTitle())
                            .withValue(MovieEntry.COLUMN_MOVIE_PLOT, found.getPlot())
                            .withValue(MovieEntry.COLUMN_MOVIE_THUMBNAIL, found.getThumbnail())
                            .withValue(MovieEntry.COLUMN_MOVIE_BACKDROP, found.getBackdrop())
                            .withValue(MovieEntry.COLUMN_MOVIE_RATING, found.getRating())
                            .withValue(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, found.getReleaseDate())
                            .build());
                    syncResult.stats.numUpdates++;
                }
            } else {
                // Entry doesn't exist, remove it from the local database
                Log.i(TAG, "Scheduling delete: " + title);
                batch.add(ContentProviderOperation.newDelete(MovieEntry.CONTENT_URI)
                        .withSelection(MovieEntry._ID + "='" + id + "'", null)
                        .build());
                syncResult.stats.numDeletes++;
            }
        }
        moviesCursor.close();

        // Add all the new entries
        for (Movie movie : networkEntries.values()) {
            Log.i(TAG, "Scheduling insert: " + movie.getTitle());
            batch.add(ContentProviderOperation.newInsert(MovieEntry.CONTENT_URI)
                    .withValue(MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle())
                    .withValue(MovieEntry.COLUMN_MOVIE_PLOT, movie.getPlot())
                    .withValue(MovieEntry.COLUMN_MOVIE_THUMBNAIL, movie.getThumbnail())
                    .withValue(MovieEntry.COLUMN_MOVIE_BACKDROP, movie.getBackdrop())
                    .withValue(MovieEntry.COLUMN_MOVIE_RATING, movie.getRating())
                    .withValue(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate())
                    .build());
            syncResult.stats.numInserts++;
        }

        // Synchronize by performing batch update
        Log.i(TAG, "Merge solution ready, applying batch update...");
        mContentResolver.applyBatch(MovieContract.CONTENT_AUTHORITY, batch);
        mContentResolver.notifyChange(MovieEntry.CONTENT_URI, // URI where data was modified
                null, // No local observer
                false); // IMPORTANT: Do not sync to network
    }

    /**
     * A blocking method to stream the server's content and build it into a string.
     *
     * @param url API call
     * @return String response
     */
//    private String download(String url) throws IOException {
//        // Ensure we ALWAYS close these!
//        HttpURLConnection client = null;
//        InputStream is = null;
//
//        try {
//            // Connect to the server using GET protocol
//            URL server = new URL(url);
//            client = (HttpURLConnection) server.openConnection();
//            client.connect();
//
//            // Check for valid response code from the server
//            int status = client.getResponseCode();
//            is = (status == HttpURLConnection.HTTP_OK)
//                    ? client.getInputStream() : client.getErrorStream();
//
//            // Build the response or error as a string
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            StringBuilder sb = new StringBuilder();
//            for (String temp; ((temp = br.readLine()) != null); ) {
//                sb.append(temp);
//            }
//
//            return sb.toString();
//        } finally {
//            if (is != null) {
//                is.close();
//            }
//            if (client != null) {
//                client.disconnect();
//            }
//        }
//    }


    /**
     * Manual force Android to perform a sync with our SyncAdapter.
     */
    public static void performSync() {
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(AccountUtils.getAccount(),
                MovieContract.CONTENT_AUTHORITY, b);
    }
}
