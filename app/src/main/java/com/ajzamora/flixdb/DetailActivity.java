package com.ajzamora.flixdb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

import com.ajzamora.flixdb.adapters.ReviewAdapter;
import com.ajzamora.flixdb.adapters.TrailerAdapter;
import com.ajzamora.flixdb.loaders.ReviewLoader;
import com.ajzamora.flixdb.loaders.TrailerLoader;
import com.ajzamora.flixdb.models.FlixPreferences;
import com.ajzamora.flixdb.models.Movie;
import com.ajzamora.flixdb.models.Review;
import com.ajzamora.flixdb.models.Trailer;
import com.ajzamora.flixdb.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.HttpURLConnection;
import java.util.List;

public class DetailActivity extends AppCompatActivity
        implements TrailerAdapter.RecyclerItemClickListener {
    public static final int REQUEST_CODE = 2;
    public static final String EXTRA_TRAILERS = "extra_trailers";
    public static final String EXTRA_REVIEWS = "extra_reviews";
    public static final String EXTRA_MOVIE = "extra_movie";

    private static final int TRAILER_LOADER_ID = 2;
    private static final int REVIEW_LOADER_ID = 3;

    private LoaderManager.LoaderCallbacks<List<Trailer>> trailerLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<Trailer>>() {
        @NonNull
        @Override
        public Loader<List<Trailer>> onCreateLoader(int id, @Nullable Bundle args) {
            String api = FlixPreferences.getPreferredAPI(DetailActivity.this);

            return new TrailerLoader(DetailActivity.this, api, mId);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<Trailer>> loader, List<Trailer> trailers) {
            mTrailerAdapter.clear();
            if (trailers != null && !trailers.isEmpty()) {
                mTrailerAdapter.setData(trailers);
            } else {
                if (NetworkUtils.STATUS_CODE == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    // Invalid Api Key
                    setEmptyTrailerState(R.string.empty_state_api_invalid);
                } else {
                    // No Trailer
                    setEmptyTrailerState(R.string.empty_state_no_preview);
                }
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<Trailer>> loader) {
            mTrailerAdapter.clear();
        }
    };

    private LoaderManager.LoaderCallbacks<List<Review>> reviewLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<Review>>() {
        @NonNull
        @Override
        public Loader<List<Review>> onCreateLoader(int id, @Nullable Bundle args) {
            String api = FlixPreferences.getPreferredAPI(DetailActivity.this);

            return new ReviewLoader(DetailActivity.this, api, mId);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<Review>> loader, List<Review> reviews) {
            mReviewAdapter.clear();
            if (reviews != null && !reviews.isEmpty()) {
                mReviewAdapter.setData(reviews);
            } else {
                if (NetworkUtils.STATUS_CODE == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    // Invalid Api Key
                    setEmptyReviewState(R.string.empty_state_api_invalid);
                } else {
                    // No Trailer
                    setEmptyReviewState(R.string.empty_state_no_review);
                }
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<Review>> loader) {
            mReviewAdapter.clear();
        }
    };

    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;
    private RecyclerView mReviewRV;
    private RecyclerView mDetailRV;
    private ImageView mThumbIV;
    private ImageView mBackdropIV;
    private TextView mTitleTV;
    private TextView mPlotTV;
    private TextView mRatingTV;
    private TextView mDateTV;
    private TextView mEmptyTrailerTV;
    private TextView mEmptyReviewTV;

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

            if (mMovie.getReviews() != null && !mMovie.getReviews().isEmpty()) {
                mReviewAdapter.setData(mMovie.getReviews());
            }
        }

        if (isOnline()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(TRAILER_LOADER_ID, null, trailerLoaderCallbacks);
            loaderManager.initLoader(REVIEW_LOADER_ID, null, reviewLoaderCallbacks);
        } else {
            // no internet connection
            setEmptyTrailerState(R.string.empty_state_no_internet);
        }
    }

    private void initUI() {
        mReviewRV = findViewById(R.id.rv_review_detail);
        mReviewRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mReviewRV.setHasFixedSize(true);
        mReviewAdapter = new ReviewAdapter();
        mReviewRV.setAdapter(mReviewAdapter);

        mDetailRV = findViewById(R.id.rv_trailer_detail);
        mDetailRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mDetailRV.setHasFixedSize(true);
        mTrailerAdapter = new TrailerAdapter(this);
        mDetailRV.setAdapter(mTrailerAdapter);

        mThumbIV = findViewById(R.id.iv_thumbnail_detail);
        mBackdropIV = findViewById(R.id.iv_backdrop_detail);
        mTitleTV = findViewById(R.id.tv_title_detail);
        mPlotTV = findViewById(R.id.tv_plot_detail);
        mRatingTV = findViewById(R.id.tv_rating_detail);
        mDateTV = findViewById(R.id.tv_date_detail);
        mEmptyTrailerTV = findViewById(R.id.tv_empty_trailer_detail);
        mEmptyReviewTV = findViewById(R.id.tv_empty_review_detail);
    }

    private void setEmptyTrailerState(int emptyTrailerState) {
        mEmptyTrailerTV.setVisibility(View.VISIBLE);
        String emptyTrailer = "-- ".concat(getString(emptyTrailerState)).concat(" --");
        mEmptyTrailerTV.setText(emptyTrailer);
    }

    private void setEmptyReviewState(int emptyReviewState) {
        mEmptyReviewTV.setVisibility(View.VISIBLE);
        String emptyTrailer = "-- ".concat(getString(emptyReviewState)).concat(" --");
        mEmptyReviewTV.setText(emptyTrailer);
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
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXTRA_TRAILERS, mTrailerAdapter.getData());
        intent.putExtra(MainActivity.EXTRA_MOVIE_POS, mMoviePosition);
        intent.putExtra(DetailActivity.EXTRA_REVIEWS, mReviewAdapter.getData());
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        launchTrailer(clickedItemIndex);
    }

    private void launchTrailer(int trailerPosition) {
        Trailer currentTrailer = mTrailerAdapter.getTrailerAt(trailerPosition);
        Uri trailerUri = NetworkUtils.buildVideoUri(currentTrailer.getSite(), currentTrailer.getKey());
        Intent playVideo = new Intent(Intent.ACTION_VIEW, trailerUri);
        startActivity(playVideo);
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
