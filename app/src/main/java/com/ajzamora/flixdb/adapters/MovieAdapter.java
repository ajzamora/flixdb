package com.ajzamora.flixdb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ajzamora.flixdb.R;
import com.ajzamora.flixdb.models.Movie;
import com.ajzamora.flixdb.models.Review;
import com.ajzamora.flixdb.models.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> mMovies;
    final private RecyclerItemClickListener mOnClickListener;

    public MovieAdapter(RecyclerItemClickListener listener) {
        this(new ArrayList<Movie>(), listener);
    }

    public MovieAdapter(List<Movie> movies, RecyclerItemClickListener listener) {
        mMovies = movies;
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
        return mMovies.size();
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
            Picasso.get()
                    .load(currentMovie.getThumbnailUrl())
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
        Movie currentMovie = mMovies.get(position);
        currentMovie.setTrailers(trailers);
        notifyItemChanged(position);
    }

    public void setReviewListAt(int position, List<Review> reviews) {
        Movie currentMovie = mMovies.get(position);
        currentMovie.setReviews(reviews);
        notifyItemChanged(position);
    }

    public void setData(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    public Movie getMovieAt(int position) {
        return mMovies.get(position);
    }

    public void clear() {
        setData(new ArrayList<Movie>());
    }
}
