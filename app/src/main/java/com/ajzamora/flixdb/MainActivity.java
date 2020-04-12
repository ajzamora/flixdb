package com.ajzamora.flixdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ajzamora.flixdb.adapters.MovieAdapter;
import com.ajzamora.flixdb.utils.LayoutUtils;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int NUM_LIST_ITEMS = 100;

    private MovieAdapter mMovieAdapter;
    private RecyclerView mMainRV;

    private static boolean sharedPreferenceFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    public void initUI() {
        mMainRV = (RecyclerView) findViewById(R.id.recyclerview_main);
        int mNoOfColumns = LayoutUtils.calculateNoOfColumns(getApplicationContext(), Integer.valueOf(getString(R.string.item_movie_columnWidth)));
        mMainRV.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        mMainRV.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(NUM_LIST_ITEMS);
        mMainRV.setAdapter(mMovieAdapter);
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
            sharedPreferenceFlag = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
