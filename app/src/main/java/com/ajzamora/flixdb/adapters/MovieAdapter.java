package com.ajzamora.flixdb.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ajzamora.flixdb.R;
import com.ajzamora.flixdb.models.Movie;
import com.ajzamora.flixdb.models.MovieContract.MovieEntry;
import com.ajzamora.flixdb.models.Review;
import com.ajzamora.flixdb.models.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

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
        // TODO: delete after testing
        TextView mItemMovieTrailerCountTV;

        MovieViewHolder(View itemView) {
            super(itemView);
            mItemMovieIV = itemView.findViewById(R.id.imageview_item_movie);
            mItemMovieTrailerCountTV = itemView.findViewById(R.id.textview_item_trailer_count);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            Movie currentMovie = getMovieAt(position);
//            Log.v("asdf", currentMovie.getThumbnailUrl());
            Picasso.get()
                    .load(currentMovie.getThumbnailUrl())
                    .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                    .error(R.drawable.ic_sad)
                    .into(mItemMovieIV);
            int trailerCount = 0, reviewCount = 0;
            if (!(currentMovie.getTrailers() == null || currentMovie.getTrailers().isEmpty())) {
                trailerCount = currentMovie.getTrailers().size();
            }

            if (!(currentMovie.getReviews() == null || currentMovie.getReviews().isEmpty())) {
                reviewCount = currentMovie.getReviews().size();
            }
            mItemMovieTrailerCountTV.setText(String.valueOf(trailerCount));
            mItemMovieTrailerCountTV.append(" - " + (reviewCount));
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }

    public void setTrailerListAt(int position, List<Trailer> trailers) {
        Movie currentMovie = getMovieAt(position);
        currentMovie.setTrailers(trailers);
        notifyItemChanged(position);
    }

    public void setReviewListAt(int position, List<Review> reviews) {
        Movie currentMovie = getMovieAt(position);
        currentMovie.setReviews(reviews);
        notifyItemChanged(position);
    }

    public Movie getMovieAt(int position) {
//        return mMoviesCursor.get(position);
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

    public Cursor getCursor() {
        return mMoviesCursor;
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
