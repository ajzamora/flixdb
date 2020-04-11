package com.ajzamora.flixdb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ajzamora.flixdb.R;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private int mNumOfItems;

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
        TextView mListItemMovieView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mListItemMovieView = (TextView) itemView.findViewById(R.id.textview_item_movie);
        }

        void bind(int listItemIndex) {
            mListItemMovieView.setText(String.valueOf(listItemIndex));
        }
    }
}
