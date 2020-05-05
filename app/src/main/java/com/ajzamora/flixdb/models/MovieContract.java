package com.ajzamora.flixdb.models;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.ajzamora.flixdb";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://".concat(CONTENT_AUTHORITY));
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_MOVIES_ID = "movies/#";

    private MovieContract() {
    }

    public static final class MovieEntry implements BaseColumns {
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of movies.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single movie.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES_ID;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        public static final String TABLE_NAME = "movies";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_THUMBNAIL = "thumbnail";
        public static final String COLUMN_MOVIE_BACKDROP = "backdrop";
        public static final String COLUMN_MOVIE_PLOT = "plot";
        public static final String COLUMN_MOVIE_POPULARITY = "popularity";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_IS_FAVORITED = "is_favorited";
        public static final String COLUMN_MOVIE_TRAILERS_ID = "trailers_id";
        public static final String COLUMN_MOVIE_REVIEWS_ID = "reviews_id";
    }


}
