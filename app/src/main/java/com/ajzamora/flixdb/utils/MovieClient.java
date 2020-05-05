package com.ajzamora.flixdb.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ajzamora.flixdb.models.MovieContract.MovieEntry;


public class MovieClient extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "flixdb.db";
    public static final int DATABASE_VERSION = 1;

    public MovieClient(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(MovieEntry.TABLE_NAME).append("(");
        sb.append(MovieEntry._ID).append(" INTEGER PRIMARY KEY NOT NULL, ");
        sb.append(MovieEntry.COLUMN_MOVIE_TITLE).append(" TEXT , ");
        sb.append(MovieEntry.COLUMN_MOVIE_THUMBNAIL).append(" TEXT, ");
        sb.append(MovieEntry.COLUMN_MOVIE_BACKDROP).append(" TEXT, ");
        sb.append(MovieEntry.COLUMN_MOVIE_PLOT).append(" TEXT, ");
        sb.append(MovieEntry.COLUMN_MOVIE_POPULARITY).append(" TEXT, ");
        sb.append(MovieEntry.COLUMN_MOVIE_RATING).append(" TEXT, ");
        sb.append(MovieEntry.COLUMN_MOVIE_RELEASE_DATE).append(" TEXT, ");
        sb.append(MovieEntry.COLUMN_MOVIE_IS_FAVORITED).append(" INTEGER NOT NULL DEFAULT 0, ");
        sb.append(MovieEntry.COLUMN_MOVIE_TRAILERS_ID).append(" INTEGER, ");
        sb.append(MovieEntry.COLUMN_MOVIE_REVIEWS_ID).append(" INTEGER);");

        String SQL_CREATE_ENTRIES = sb.toString();
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
