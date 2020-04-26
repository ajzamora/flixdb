package com.ajzamora.flixdb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ajzamora.flixdb.R;
import com.ajzamora.flixdb.models.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> mReviews;

    public ReviewAdapter() {
        this(new ArrayList<Review>());
    }

    public ReviewAdapter(List<Review> reviews) {
        mReviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_review, parent, false);

        ReviewViewHolder reviewViewHolder = new ReviewViewHolder(itemView);
        return reviewViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public final class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView mItemReviewAuthorTV;
        private TextView mItemReviewContentTV;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemReviewAuthorTV = itemView.findViewById(R.id.textview_item_review_author);
            mItemReviewContentTV = itemView.findViewById(R.id.textview_item_review_content);
        }

        void bind(int position) {
            Review review = getReviewAt(position);
            mItemReviewAuthorTV.setText(review.getAuthor());
            mItemReviewContentTV.setText(review.getContent());
        }
    }

    public Review getReviewAt(int position) {
        return mReviews.get(position);
    }
}
