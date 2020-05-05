package com.ajzamora.flixdb;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ajzamora.flixdb.adapters.FavoriteAdapter;
import com.ajzamora.flixdb.models.FlixPreferences;
import com.ajzamora.flixdb.models.Movie;
import com.ajzamora.flixdb.models.MovieContract.MovieEntry;

public class FavoritesActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        FavoriteAdapter.RecyclerItemClickListener {

    private static final int FAV_LOADER_ID = 4;
    private FavoriteAdapter mFavAdapter;
    private RecyclerView mFavRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        initUI();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(FAV_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.action_edit_fav:
                toggleFavEdit(item);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void toggleFavEdit(MenuItem item) {
        boolean isEdit = item.getTitle().toString().equalsIgnoreCase(getString(R.string.action_edit));
        if (isEdit) {
            item.setTitle(getString(R.string.action_done));
            mFavAdapter.changeEditIconVisibility(View.VISIBLE);
            mFavAdapter.changeViewHolderBackgroundResource(R.color.colorDark);
        } else {
            item.setTitle(getString(R.string.action_edit));
            mFavAdapter.changeEditIconVisibility(View.INVISIBLE);
            mFavAdapter.changeViewHolderBackgroundResource(Color.TRANSPARENT);
        }
    }

    public void initUI() {
        mFavRV = findViewById(R.id.rv_movie_favorites);
        mFavRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mFavRV.setHasFixedSize(true);

        mFavAdapter = new FavoriteAdapter( this);
        mFavRV.setAdapter(mFavAdapter);
    }

    @Override
    public void onListItemClick(int moviePosition) {
        Movie currentMovie = mFavAdapter.getMovieAt(moviePosition);
        Intent startDetail = new Intent(this, DetailActivity.class);
        startDetail.putExtra(MainActivity.EXTRA_MOVIE_POS, moviePosition);
        startDetail.putExtra(DetailActivity.EXTRA_MOVIE, currentMovie);
        startActivity(startDetail);
    }

    @Override
    public void onDelItemClick(int clickedItemIndex) {
        Movie currentMovie = mFavAdapter.getMovieAt(clickedItemIndex);
        currentMovie.setIsFavorited(!currentMovie.getIsFavorited());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry.COLUMN_MOVIE_IS_FAVORITED, currentMovie.getIsFavorited());
        Uri updateUri = MovieEntry.CONTENT_URI.buildUpon().appendEncodedPath(currentMovie.getId()).build();
        getContentResolver().update(updateUri, contentValues, null, null);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String sortType = "DESC";
        String sortOrder = (FlixPreferences.isSortPopular(this) ? MovieEntry.COLUMN_MOVIE_POPULARITY : MovieEntry.COLUMN_MOVIE_RATING).concat(" ").concat(sortType);

        String selection = MovieEntry.COLUMN_MOVIE_IS_FAVORITED.concat(" = ?");
        String[] selectionArgs = new String[]{"1"};
        return new CursorLoader(this,
                MovieEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) mFavAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mFavAdapter.swapCursor(null);
    }


}
