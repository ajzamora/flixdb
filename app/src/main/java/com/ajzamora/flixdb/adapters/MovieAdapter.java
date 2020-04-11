package com.ajzamora.flixdb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ajzamora.flixdb.R;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private int mNumOfItems;
    private final String tempImagePath = "https://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg";

    public MovieAdapter(int numOfItems) {
        mNumOfItems = numOfItems;
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
        return mNumOfItems;
    }

    public final class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView mItemMovieIV;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mItemMovieIV = (ImageView) itemView.findViewById(R.id.imageview_item_movie);
        }

        void bind(int currentItemMovie) {
            Picasso.get()
                    .load(tempImagePath)
                    .into(mItemMovieIV);
        }
    }
}
