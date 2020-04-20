package com.ajzamora.flixdb;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.ajzamora.flixdb.models.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE = "extra_movie";

    private ImageView mThumbIv;
    private ImageView mBackdropIv;
    private TextView mTitleTv;
    private TextView mPlotTv;
    private TextView mRatingTv;
    private TextView mDateTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initUI();
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        Movie movie = getIntent().getParcelableExtra(EXTRA_MOVIE);
        if (movie != null) {
            populateUI(movie);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initUI() {
        mThumbIv = findViewById(R.id.iv_thumbnail_detail);
        mBackdropIv = findViewById(R.id.iv_backdrop_detail);
        mTitleTv = findViewById(R.id.tv_title_detail);
        mPlotTv = findViewById(R.id.tv_plot_detail);
        mRatingTv = findViewById(R.id.tv_rating_detail);
        mDateTv = findViewById(R.id.tv_date_detail);
    }

    private void closeOnError() {
        finish();
    }

    private void populateUI(Movie movie) {
        final String rateLabel = "Rating: ";
        final String dateLabel = "Release Date: ";

        Picasso.get()
                .load(movie.getThumbnail())
                .into(mThumbIv);
        Picasso.get()
                .load(movie.getBackdrop())
                .into(mBackdropIv);
        mTitleTv.setText(movie.getTitle());
        mPlotTv.setText(movie.getPlot());
        mRatingTv.setText(rateLabel.concat(movie.getRating()));
        mDateTv.setText(dateLabel.concat(movie.getReleaseDate()));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
