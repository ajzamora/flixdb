package com.ajzamora.flixdb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajzamora.flixdb.models.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE = "extra_movie";

    private ImageView mThumbIv;
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
        if(movie != null) {
            populateUI(movie);
        }
    }

    private void initUI() {
        mThumbIv = (ImageView) findViewById(R.id.iv_thumbnail_detail);
        mTitleTv = (TextView) findViewById(R.id.tv_title_detail);
        mPlotTv = (TextView) findViewById(R.id.tv_plot_detail);
        mRatingTv = (TextView) findViewById(R.id.tv_rating_detail);
        mDateTv = (TextView) findViewById(R.id.tv_date_detail);
    }

    private void closeOnError() {
        finish();
    }

    private void populateUI(Movie movie) {
        Picasso.get()
                .load(movie.getThumbnail())
                .into(mThumbIv);
        mTitleTv.setText(movie.getTitle());
        mPlotTv.setText(movie.getPlot());
        mRatingTv.setText(movie.getRating());
        mDateTv.setText(movie.getReleaseDate());
    }
}
