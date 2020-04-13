package com.ajzamora.flixdb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ajzamora.flixdb.R;
import com.ajzamora.flixdb.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> mMovies;

    public MovieAdapter(List<Movie> movies) {
        mMovies = movies;
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
        Movie currentMovie = mMovies.get(position);
        holder.bind(currentMovie);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public final class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView mItemMovieIV;
        Movie mCurrentMovie;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mItemMovieIV = (ImageView) itemView.findViewById(R.id.imageview_item_movie);
        }

        void bind(Movie currentMovie) {
            mCurrentMovie = currentMovie;
            Picasso.get()
                    .load(currentMovie.getThumbnail())
                    .into(mItemMovieIV);
        }
    }

    public void setData(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    public void clear() {
        setData(new ArrayList<Movie>());
    }
}
