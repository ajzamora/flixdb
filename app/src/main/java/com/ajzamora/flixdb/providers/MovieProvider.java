package com.ajzamora.flixdb.providers;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ajzamora.flixdb.models.MovieContract;
import com.ajzamora.flixdb.models.MovieContract.MovieEntry;
import com.ajzamora.flixdb.utils.MovieClient;

import java.util.ArrayList;

public class MovieProvider extends ContentProvider {

    public static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private MovieClient mMovieOpenHelper;
    static volatile boolean batchFlag = false;

    private static final int MOVIES = 100;
    private static final int MOVIE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES_ID, MOVIE_ID);
    }

    @Override
    public boolean onCreate() {
        mMovieOpenHelper = new MovieClient(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.v(LOG_TAG, "sync query");
        SQLiteDatabase movieReadDb = mMovieOpenHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                cursor = movieReadDb.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MOVIE_ID:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = movieReadDb.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        assert getContext() != null;
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;
        switch (match) {
            case MOVIES:
                _id = saveMovie(uri, contentValues);
                returnUri = ContentUris.withAppendedId(uri, _id);
                break;
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
        assert getContext() != null;
        if (!batchFlag) getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.v(LOG_TAG, "sync update");
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return updateMovie(uri, contentValues, selection, selectionArgs);
            case MOVIE_ID:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateMovie(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.v(LOG_TAG, "sync delete");
        SQLiteDatabase movieWriteDb = mMovieOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case MOVIES:
                rowsDeleted = movieWriteDb.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_ID:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = movieWriteDb.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }


        if (!batchFlag && rowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    private long saveMovie(Uri uri, ContentValues contentValues) {
//        TODO: Sanity Check
        SQLiteDatabase movieWriteDb = mMovieOpenHelper.getWritableDatabase();

        long id = movieWriteDb.insert(MovieEntry.TABLE_NAME, null, contentValues);
        Log.v(LOG_TAG, "sync insert " + id);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
        }

        return id;
    }

    private int updateMovie(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//        TODO: Sanity Check

        SQLiteDatabase movieWriteDb = mMovieOpenHelper.getWritableDatabase();

        int count = movieWriteDb.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);

        if (!batchFlag && count != 0) getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public String getType(Uri uri) {
        Log.v(LOG_TAG, "sync get type");
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return MovieEntry.CONTENT_LIST_TYPE;
            case MOVIE_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {

        SQLiteDatabase movieReadDb = mMovieOpenHelper.getReadableDatabase();
        movieReadDb.beginTransaction();
        try {
            batchFlag = true;
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            movieReadDb.setTransactionSuccessful();
            return results;
        } finally {
            movieReadDb.endTransaction();
            batchFlag = false;
            Log.v("apply", "batch");
        }
    }
}
