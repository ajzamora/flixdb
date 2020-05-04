package com.ajzamora.flixdb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import androidx.core.widget.ContentLoadingProgressBar;
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
    private ContentLoadingProgressBar mIndicatorPB;
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

    private static boolean sharedPreferenceFlag = false;

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
        mIndicatorPB = findViewById(R.id.pb_indicator_main);
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
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        sharedPreferenceFlag = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Register the observer at the start of our activity
        getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);

        if (sharedPreferenceFlag && isOnline()) {
            Log.i(LOG_TAG, "onStart: preferences were updated");
            mMovieAdapter.swapCursor(null);
            hideEmptyState();
            mIndicatorPB.show();
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            sharedPreferenceFlag = false;
        }
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

        mIndicatorPB.hide();
        if (data.getCount() > 0) {
            mMovieAdapter.swapCursor(data);
            Log.v(LOG_TAG, "onLoadFinished: if" + data.getCount() + " " + mMovieAdapter.getItemCount());
            StringBuilder sb = new StringBuilder();
            int count = data.getCount();
            while (data.moveToNext()) {
                int titleColIdx = data.getColumnIndex(MovieEntry.COLUMN_MOVIE_TITLE);
                int rateColIdx = data.getColumnIndex(MovieEntry.COLUMN_MOVIE_RATING);
                String title = data.getString(titleColIdx);
                String rate = data.getString(rateColIdx);
                sb.append(title).append(" ").append(rate).append("\n");
            }
            sb.append("total count: ").append(count);
            Log.v(LOG_TAG, sb.toString());
        } else {
            setEmptyState(R.string.empty_state_no_movies, R.drawable.ic_sad, R.string.empty_state_icon_sad);
            Log.v(LOG_TAG, "onLoadFinished: else");
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
//        Movie currentMovie = mMovieAdapter.getMovieAt(moviePosition);
//        Intent intent = new Intent(this, DetailActivity.class);
//        intent.putExtra(MainActivity.EXTRA_MOVIE_POS, moviePosition);
//        intent.putExtra(DetailActivity.EXTRA_MOVIE, currentMovie);
//        startActivityForResult(intent, DetailActivity.REQUEST_CODE);
        // TODO: delete temporary test sync
        // Perform a manual sync by calling this:
        SyncAdapter.performSync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DetailActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            int moviePosition = data.getIntExtra(MainActivity.EXTRA_MOVIE_POS, MainActivity.EXTRA_MOVIE_POS_DEFAULT);
            List<Trailer> trailers = data.getParcelableArrayListExtra(DetailActivity.EXTRA_TRAILERS);
//            mMovieAdapter.setTrailerListAt(moviePosition, trailers);

            List<Review> reviews = data.getParcelableArrayListExtra(DetailActivity.EXTRA_REVIEWS);
//            mMovieAdapter.setReviewListAt(moviePosition, reviews);
        }
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void refreshMovies() {
        Log.i(getClass().getName(), "Movies data has changed!");
    }
}
