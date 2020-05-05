package com.ajzamora.flixdb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ajzamora.flixdb.adapters.MovieAdapter;
import com.ajzamora.flixdb.models.FlixPreferences;
import com.ajzamora.flixdb.models.Movie;
import com.ajzamora.flixdb.models.MovieContract.MovieEntry;
import com.ajzamora.flixdb.models.Review;
import com.ajzamora.flixdb.models.Trailer;
import com.ajzamora.flixdb.sync.SyncAdapter;
import com.ajzamora.flixdb.utils.AccountUtils;
import com.ajzamora.flixdb.utils.LayoutUtils;
import com.ajzamora.flixdb.utils.NetworkUtils;

import java.net.HttpURLConnection;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        MovieAdapter.RecyclerItemClickListener {

    public final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 1;
    public static final String EXTRA_MOVIE_POS = "extra_movie_pos";
    public static final int EXTRA_MOVIE_POS_DEFAULT = -1;

    private MovieAdapter mMovieAdapter;
    private RecyclerView mMainRV;
    private TextView mEmptyStateTV;
    private ImageView mEmptyIconIV;
    private TextView mEmptyIconLabelTV;
    ;
    private MovieObserver movieObserver;

    /**
     * Example content observer for observing article data changes.
     */
    private final class MovieObserver extends ContentObserver {
        private MovieObserver() {
            // Ensure callbacks happen on the UI thread
            super(new Handler(Looper.getMainLooper()));
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            // Handle your data changes here!!!
            refreshMovies();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        // Create your sync account
        AccountUtils.createSyncAccount(this);

        movieObserver = new MovieObserver();

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
    }

    public void initUI() {
        mEmptyStateTV = findViewById(R.id.tv_empty_main);
        mEmptyIconIV = findViewById(R.id.iv_empty_icon_main);
        mEmptyIconLabelTV = findViewById(R.id.tv_empty_icon_label_main);
        mMainRV = findViewById(R.id.rv_movie_main);
        int mNoOfColumns = LayoutUtils.calculateNoOfColumns(getApplicationContext(), Integer.valueOf(getString(R.string.item_movie_columnWidth)));
        mMainRV.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        mMainRV.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mMainRV.setAdapter(mMovieAdapter);
    }

    private void setEmptyState(int resEmptyStateText, int resEmptyIcoImage, int resEmptyStateIcoLabel) {
        mEmptyStateTV.setVisibility(View.VISIBLE);
        mEmptyIconIV.setVisibility(View.VISIBLE);
        mEmptyIconLabelTV.setVisibility(View.VISIBLE);
        mEmptyStateTV.setText(getString(resEmptyStateText));
        mEmptyIconIV.setImageResource(resEmptyIcoImage);
        mEmptyIconLabelTV.setText(getString(resEmptyStateIcoLabel));
    }

    private void hideEmptyState() {
        mEmptyStateTV.setVisibility(View.GONE);
        mEmptyIconIV.setVisibility(View.GONE);
        mEmptyIconLabelTV.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
            case R.id.action_favorites:
                Intent startFavoritesActivity = new Intent(this, FavoritesActivity.class);
                startActivity(startFavoritesActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_api_key))) {
            SyncAdapter.performSync();
        } else {
            mMovieAdapter.clear();
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Register the observer at the start of our activity
        getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unregister the observer at the stop of our activity
        if (movieObserver != null) getContentResolver().unregisterContentObserver(movieObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String sortType = "DESC";
        String sortOrder = (FlixPreferences.isSortPopular(this) ? MovieEntry.COLUMN_MOVIE_POPULARITY : MovieEntry.COLUMN_MOVIE_RATING).concat(" ").concat(sortType);
        Log.v(LOG_TAG, "onCreateLoader ".concat(sortOrder));
        return new CursorLoader(this,
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                sortOrder);
    }


    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished");
        if (data.getCount() > 0) {
            mMovieAdapter.swapCursor(data);
            hideEmptyState();
        } else {
            if (NetworkUtils.getLastStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
                setEmptyState(R.string.empty_state_api_invalid, R.drawable.ic_smart_key, R.string.empty_state_api_instructions);
            else
                setEmptyState(R.string.empty_state_no_movies, R.drawable.ic_sad, R.string.empty_state_icon_sad);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoadReset");
        mMovieAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        launchDetailActivity(clickedItemIndex);
    }

    private void launchDetailActivity(int moviePosition) {
        Movie currentMovie = mMovieAdapter.getMovieAt(moviePosition);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(MainActivity.EXTRA_MOVIE_POS, moviePosition);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, currentMovie);
        startActivityForResult(intent, DetailActivity.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DetailActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            int moviePosition = data.getIntExtra(MainActivity.EXTRA_MOVIE_POS, MainActivity.EXTRA_MOVIE_POS_DEFAULT);
            List<Trailer> trailers = data.getParcelableArrayListExtra(DetailActivity.EXTRA_TRAILERS);
            mMovieAdapter.setTrailerListAt(moviePosition, trailers);

            List<Review> reviews = data.getParcelableArrayListExtra(DetailActivity.EXTRA_REVIEWS);
            mMovieAdapter.setReviewListAt(moviePosition, reviews);
        }
    }

    private void refreshMovies() {
        mMovieAdapter.clear();
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }
}
