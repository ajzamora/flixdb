package com.ajzamora.flixdb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.ajzamora.flixdb.adapters.MovieAdapter;
import com.ajzamora.flixdb.utils.LayoutUtils;

public class MainActivity extends AppCompatActivity {

    private static final int NUM_LIST_ITEMS = 100;

    private MovieAdapter mMovieAdapter;
    private RecyclerView mMainRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    public void initUI() {
        mMainRV = (RecyclerView) findViewById(R.id.recyclerview_main);
        int mNoOfColumns = LayoutUtils.calculateNoOfColumns(getApplicationContext(), 100);
        mMainRV.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        mMainRV.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(NUM_LIST_ITEMS);
        mMainRV.setAdapter(mMovieAdapter);
    }
}
