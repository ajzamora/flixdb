package com.ajzamora.flixdb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ajzamora.flixdb.R;
import com.ajzamora.flixdb.models.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private List<Trailer> mTrailers;

    public TrailerAdapter(List<Trailer> trailers) {
        mTrailers = trailers;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_trailer, parent, false);

        TrailerViewHolder trailerViewHolder = new TrailerViewHolder(itemView);
        return trailerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer currentTrailer = mTrailers.get(position);
        holder.bind(currentTrailer);
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public final class TrailerViewHolder extends RecyclerView.ViewHolder {
        ImageView mItemTrailerIV;
        Trailer mCurrentTrailer;

        TrailerViewHolder(View itemView) {
            super(itemView);
            mItemTrailerIV = itemView.findViewById(R.id.imageview_item_trailer);
        }

        void bind(Trailer currentTrailer) {
            mCurrentTrailer = currentTrailer;

            String imgPath = "https://img.youtube.com/vi/"
                    .concat(currentTrailer.getKey())
                    .concat("/mqdefault.jpg");
            Picasso.get()
                    .load(imgPath)
                    .placeholder(android.R.color.background_dark)
                    .into(mItemTrailerIV);
        }
    }

    public void setData(List<Trailer> trailers) {
        mTrailers = trailers;
        notifyDataSetChanged();
    }

    public ArrayList<Trailer> getData() {
        return (ArrayList<Trailer>) mTrailers;
    }

    public void clear() {
        setData(new ArrayList<Trailer>());
    }
}
