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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {
    private Cursor mFavoriteCursor;
    final private RecyclerItemClickListener mOnClickListener;
    List<FavoriteViewHolder> favoriteViewHolders = new ArrayList<>();

    public FavoriteAdapter(RecyclerItemClickListener listener) {
        this(null, listener);
    }

    public FavoriteAdapter(Cursor moviesCursor, RecyclerItemClickListener listener) {
        mFavoriteCursor = moviesCursor;
        mOnClickListener = listener;
    }

    public interface RecyclerItemClickListener {
        void onListItemClick(int clickedItemIndex);
        void onDelItemClick(int clickedItemIndex);
    }

    @NonNull
    @Override
    public FavoriteAdapter.FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_favorites, parent, false);

        FavoriteViewHolder favoriteViewHolder = new FavoriteViewHolder(itemView);
        favoriteViewHolders.add(favoriteViewHolder);
        return favoriteViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return (mFavoriteCursor == null) ? 0 : mFavoriteCursor.getCount();
    }

    public final class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView mItemFavThumbIV;
        TextView mItemFavTitleIV;
        TextView mItemFavDateIV;
        TextView mItemFavRateIV;
        ImageView mItemDelFavIV;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemFavThumbIV = itemView.findViewById(R.id.iv_thumb_item_favorites);
            mItemFavTitleIV = itemView.findViewById(R.id.tv_title_item_favorites);
            mItemFavDateIV = itemView.findViewById(R.id.tv_date_item_favorites);
            mItemFavRateIV = itemView.findViewById(R.id.tv_rate_item_favorites);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onListItemClick(getAdapterPosition());
                }
            });
            mItemDelFavIV = itemView.findViewById(R.id.iv_del_item_favorites);
            mItemDelFavIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onDelItemClick(getAdapterPosition());
                }
            });
        }

        public void bind(int position) {
            Movie currentMovie = getMovieAt(position);
            Picasso.get()
                    .load(currentMovie.getThumbnailUrl())
                    .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                    .error(R.drawable.ic_sad)
                    .into(mItemFavThumbIV);
            mItemFavTitleIV.setText(currentMovie.getTitle());
            mItemFavDateIV.setText(currentMovie.getReleaseDate());
            mItemFavRateIV.setText(currentMovie.getRating());
        }
    }

    public Movie getMovieAt(int position) {
        if (position < 0 || position >= getItemCount()) {
            throw new IllegalArgumentException("Item position is out of adapter's range");
        } else if (mFavoriteCursor.moveToPosition(position)) {
            // Find the columns of pet attributes that we're interested in
            return parseMovie(mFavoriteCursor);
        }
        return null;
    }

    public Cursor swapCursor(Cursor cursor) {
        if (mFavoriteCursor == cursor) {
            return null;
        }
        Cursor oldCursor = mFavoriteCursor;
        mFavoriteCursor = cursor;
        if (cursor != null) {
            notifyDataSetChanged();
        }
        return oldCursor;
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
        int isFavColIdx = mFavoriteCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_IS_FAVORITED);

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

    public void changeViewHolderBackgroundResource(int resourceColorId) {
        for (FavoriteViewHolder favHolder : favoriteViewHolders) {
            favHolder.itemView.setBackgroundResource(resourceColorId);
        }
    }

    public void changeEditIconVisibility(int visibility) {
        for (FavoriteViewHolder favHolder : favoriteViewHolders) {
            favHolder.itemView.findViewById(R.id.iv_del_item_favorites).setVisibility(visibility);
        }
    }
}
