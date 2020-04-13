package com.ajzamora.flixdb.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private static final String IMAGE_URL = "https://image.tmdb.org/t/p/w185";

    private String mTitle;
    private String mThumbnail;
    private String mPlot;
    private String mRating;
    private String mReleaseDate;

    private Movie(Builder b) {
        mTitle = b.title;
        mThumbnail = b.thumbnail;
        mPlot = b.plot;
        mRating = b.rating;
        mReleaseDate = b.releaseDate;
    }

    public static final class Builder {
        private String title;
        private String thumbnail;
        private String plot;
        private String rating;
        private String releaseDate;

        public Builder() {

        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder thumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public Builder plot(String plot) {
            this.plot = plot;
            return this;
        }

        public Builder rating(String rating) {
            this.rating = rating;
            return this;
        }

        public Builder releaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public Movie build() {
            return new Movie(this);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "title='" + title + '\'' +
                    ", thumbnail='" + thumbnail + '\'' +
                    ", plot='" + plot + '\'' +
                    ", rating='" + rating + '\'' +
                    ", releaseDate='" + releaseDate + '\'' +
                    '}';
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public String getThumbnail() {
        return IMAGE_URL.concat(mThumbnail);
    }

    public String getPlot() {
        return mPlot;
    }

    public String getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "mTitle='" + mTitle + '\'' +
                ", mThumbnail='" + mThumbnail + '\'' +
                ", mPlot='" + mPlot + '\'' +
                ", mRating='" + mRating + '\'' +
                ", mReleaseDate='" + mReleaseDate + '\'' +
                '}';
    }

    protected Movie(Parcel in) {
        mTitle = in.readString();
        mThumbnail = in.readString();
        mPlot = in.readString();
        mRating = in.readString();
        mReleaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mThumbnail);
        dest.writeString(mPlot);
        dest.writeString(mRating);
        dest.writeString(mReleaseDate);
    }
}
