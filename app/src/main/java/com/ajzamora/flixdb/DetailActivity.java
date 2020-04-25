package com.ajzamora.flixdb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ajzamora.flixdb.adapters.TrailerAdapter;
import com.ajzamora.flixdb.loaders.TrailerLoader;
import com.ajzamora.flixdb.models.FlixPreferences;
import com.ajzamora.flixdb.models.Movie;
import com.ajzamora.flixdb.models.Trailer;
import com.ajzamora.flixdb.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Trailer>>,
        TrailerAdapter.RecyclerItemClickListener {
    public static final int REQUEST_CODE = 2;
    public static final String EXTRA_TRAILERS = "extra_trailers";
    public static final String EXTRA_MOVIE = "extra_movie";

    private static final int TRAILER_LOADER_ID = 2;

    private TrailerAdapter mTrailerAdapter;
    private RecyclerView mDetailRV;
    private ImageView mThumbIV;
    private ImageView mBackdropIV;
    private TextView mTitleTV;
    private TextView mPlotTV;
    private TextView mRatingTV;
    private TextView mDateTV;

    private String mId;
    private int mMoviePosition;
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_detail);

        initUI();
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mMovie = getIntent().getParcelableExtra(EXTRA_MOVIE);
        mMoviePosition = getIntent().getIntExtra(MainActivity.EXTRA_MOVIE_POS, MainActivity.EXTRA_MOVIE_POS_DEFAULT);

        if (mMovie != null) {
            mId = mMovie.getId();
            populateUI(mMovie);
            if (mMovie.getTrailers() != null && !mMovie.getTrailers().isEmpty()) {
                mTrailerAdapter.setData(mMovie.getTrailers());
            }
        }
        if (isOnline()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(TRAILER_LOADER_ID, null, this);
        } else {
            // no internet connection
        }
    }

    private void initUI() {
        mDetailRV = findViewById(R.id.rv_trailer_detail);
        mDetailRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mDetailRV.setHasFixedSize(true);
        mTrailerAdapter = new TrailerAdapter(new ArrayList<Trailer>(), this);
        mDetailRV.setAdapter(mTrailerAdapter);

        mThumbIV = findViewById(R.id.iv_thumbnail_detail);
        mBackdropIV = findViewById(R.id.iv_backdrop_detail);
        mTitleTV = findViewById(R.id.tv_title_detail);
        mPlotTV = findViewById(R.id.tv_plot_detail);
        mRatingTV = findViewById(R.id.tv_rating_detail);
        mDateTV = findViewById(R.id.tv_date_detail);
    }

    private void closeOnError() {
        finish();
    }

    private void populateUI(Movie movie) {
        Picasso.get()
                .load(movie.getThumbnail())
                .into(mThumbIV);
        Picasso.get()
                .load(movie.getBackdrop())
                .into(mBackdropIV);
        mTitleTV.setText(movie.getTitle());
        mPlotTV.setText(movie.getPlot());
        mRatingTV.setText(getString(R.string.detail_rate_label, movie.getRating()));
        mDateTV.setText(getString(R.string.detail_date_label, movie.getReleaseDate()));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void finish() {
        List<Trailer> oldTrailer = mMovie.getTrailers();
        List<Trailer> newTrailer = mTrailerAdapter.getData();
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra(EXTRA_TRAILERS, mTrailerAdapter.getData());
        intent.putExtra(MainActivity.EXTRA_MOVIE_POS, mMoviePosition);
//        if (!oldTrailer.equals(newTrailer)) {
        setResult(RESULT_OK, intent);
//        } else {
//            setResult(RESULT_CANCELED, intent);
//        }

        NavUtils.navigateUpTo(this, intent);
        if (oldTrailer != null) Log.v("asdf", "old" + oldTrailer.size());
        if (newTrailer != null) Log.v("asdf", "new" + newTrailer.size());
        if (oldTrailer != null && newTrailer != null)
            Log.v("asdf", "-------" + oldTrailer.toString().equals(newTrailer.toString()) + "\n");
        if (oldTrailer != null && newTrailer != null)
            Log.v("asdf", "-------" + oldTrailer.equals(newTrailer));

        super.finish();
    }

    @NonNull
    @Override
    public Loader<List<Trailer>> onCreateLoader(int id, @Nullable Bundle args) {
        String api = FlixPreferences.getPreferredAPI(this);

        return new TrailerLoader(this, api, mId);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Trailer>> loader, List<Trailer> trailers) {
        mTrailerAdapter.clear();
        if (trailers != null && !trailers.isEmpty()) {
            mTrailerAdapter.setData(trailers);
        } else {
            if (NetworkUtils.STATUS_CODE == HttpURLConnection.HTTP_UNAUTHORIZED) {
                // Invalid Api Key
            } else {
                // No Trailer Found
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Trailer>> loader) {
        mTrailerAdapter.clear();
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        launchTrailer(clickedItemIndex);
    }

    private void launchTrailer(int trailerPosition) {
        Trailer currentTrailer = mTrailerAdapter.getTrailerAt(trailerPosition);
        Uri trailerUri = NetworkUtils.buildVideoUri(currentTrailer.getKey());
        Intent playVideo = new Intent(Intent.ACTION_VIEW, trailerUri);
        startActivity(playVideo);
    }
}
