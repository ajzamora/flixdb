package com.ajzamora.flixdb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ajzamora.flixdb.adapters.MovieAdapter;
import com.ajzamora.flixdb.loaders.MovieLoader;
import com.ajzamora.flixdb.models.FlixPreferences;
import com.ajzamora.flixdb.models.Movie;
import com.ajzamora.flixdb.utils.LayoutUtils;
import com.ajzamora.flixdb.utils.NetworkUtils;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<List<Movie>>,
        MovieAdapter.RecyclerItemClickListener {

    public final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 1;

    private MovieAdapter mMovieAdapter;
    private RecyclerView mMainRV;
    private TextView mEmptyStateTV;
    private ImageView mEmptyIconIV;
    private TextView mEmptyIconLabelTV;
    private ProgressBar mIndicatorPb;

    private static boolean sharedPreferenceFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

        if (isOnline()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
        } else {
            setEmptyState(R.string.main_emptystate_no_internet, R.drawable.ic_panda, R.string.main_emptystate_icon_panda);
        }

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    public void initUI() {
        mIndicatorPb = (ProgressBar) findViewById(R.id.pb_indicator_main);
        mEmptyStateTV = (TextView) findViewById(R.id.tv_empty_main);
        mEmptyIconIV = (ImageView) findViewById(R.id.iv_empty_icon_main);
        mEmptyIconLabelTV = (TextView) findViewById(R.id.tv_empty_icon_label_main);
        mMainRV = (RecyclerView) findViewById(R.id.rv_movie_main);
        int mNoOfColumns = LayoutUtils.calculateNoOfColumns(getApplicationContext(), Integer.valueOf(getString(R.string.item_movie_columnWidth)));
        mMainRV.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        mMainRV.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(new ArrayList<Movie>(), this);
        mMainRV.setAdapter(mMovieAdapter);
    }

    private void setEmptyState(int resEmptyStateText, int resEmptyIcoImage, int resEmptyStateIcoLabel) {
        mIndicatorPb.setVisibility(View.GONE);

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

        mIndicatorPb.setVisibility(View.VISIBLE);
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

        if (sharedPreferenceFlag) {
            Log.i(LOG_TAG, "onStart: preferences were updated");
            hideEmptyState();

            mMovieAdapter.clear();
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            sharedPreferenceFlag = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        String api = FlixPreferences.getPreferredAPI(this);
        String sortOrder = FlixPreferences.getPreferredSortOrder(this);

        return new MovieLoader(this, api, sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> movies) {
        mIndicatorPb.setVisibility(View.GONE);
        mMovieAdapter.clear();
        if (movies != null && !movies.isEmpty()) {
            mMovieAdapter.setData(movies);
        } else {
            if (NetworkUtils.STATUS_CODE == HttpURLConnection.HTTP_UNAUTHORIZED) {
                setEmptyState(R.string.main_emptystate_api_invalid, R.drawable.ic_smart_key, R.string.main_emptystate_api_instructions);
            } else {
                setEmptyState(R.string.main_emptystate_no_movies, R.drawable.ic_sad, R.string.main_emptystate_icon_sad);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
        mMovieAdapter.clear();
    }

    @Override
    public void onListItemClick(Movie clickedItemIndex) {
        launchDetailActivity(clickedItemIndex);
    }

    private void launchDetailActivity(Movie currentMovie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, currentMovie);
        startActivity(intent);
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
