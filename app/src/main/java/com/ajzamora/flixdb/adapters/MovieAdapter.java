package com.ajzamora.flixdb.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ajzamora.flixdb.R;
import com.ajzamora.flixdb.models.Movie;
import com.ajzamora.flixdb.models.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Cursor mMoviesCursor;
    final private RecyclerItemClickListener mOnClickListener;

    public MovieAdapter(RecyclerItemClickListener listener) {
        this(null, listener);
    }

    public MovieAdapter(Cursor moviesCursor, RecyclerItemClickListener listener) {
        mMoviesCursor = moviesCursor;
        mOnClickListener = listener;
    }

    public interface RecyclerItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_movie, parent, false);

        MovieViewHolder movieViewHolder = new MovieViewHolder(itemView);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return (mMoviesCursor == null) ? 0 : mMoviesCursor.getCount();
    }

    public final class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        ImageView mItemMovieIV;

        MovieViewHolder(View itemView) {
            super(itemView);
            mItemMovieIV = itemView.findViewById(R.id.imageview_item_movie);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            Movie currentMovie = getMovieAt(position);
            Picasso.get()
                    .load(currentMovie.getThumbnailUrl())
                    .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                    .error(R.drawable.ic_sad)
                    .into(mItemMovieIV);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }

    public Movie getMovieAt(int position) {
        if (position < 0 || position >= getItemCount()) {
            throw new IllegalArgumentException("Item position is out of adapter's range");
        } else if (mMoviesCursor.moveToPosition(position)) {
            // Find the columns of pet attributes that we're interested in
            return parseMovie(mMoviesCursor);
        }
        return null;
    }

    public Cursor swapCursor(Cursor cursor) {
        if (mMoviesCursor == cursor) {
            return null;
        }
        Cursor oldCursor = mMoviesCursor;
        mMoviesCursor = cursor;
        if (cursor != null) {
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    public void clear() {
        mMoviesCursor = null;
        notifyDataSetChanged();
    }

    private Movie parseMovie(Cursor movieCursor) {
        int idColIdx = movieCursor.getColumnIndex(MovieEntry._ID);
        int titleColIdx = movieCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_TITLE);
        int plotColIdx = movieCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_PLOT);
        int popColIdx = movieCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_POPULARITY);
        int thumbnailColIdx = movieCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_THUMBNAIL);
        int backdropColIdx = movieCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_BACKDROP);
        int rateColIdx = movieCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RATING);
        int dateColIdx = movieCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RELEASE_DATE);
        int isFavColIdx = mMoviesCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_IS_FAVORITED);

        // Extract out the value from the Cursor for the given column index
        String id = movieCursor.getString(idColIdx);
        String title = movieCursor.getString(titleColIdx);
        String plot = movieCursor.getString(plotColIdx);
        String popularity = movieCursor.getString(popColIdx);
        String thumbnail = movieCursor.getString(thumbnailColIdx);
        String backdrop = movieCursor.getString(backdropColIdx);
        String rate = movieCursor.getString(rateColIdx);
        String date = movieCursor.getString(dateColIdx);
        boolean isFavorited = movieCursor.getInt(isFavColIdx) == 1;

        return new Movie.Builder()
                .id(id)
                .title(title)
                .plot(plot)
                .popularity(popularity)
                .thumbnail(thumbnail)
                .backdrop(backdrop)
                .releaseDate(date)
                .rating(rate)
                .isFavorited(isFavorited)
                .build();
    }
}
