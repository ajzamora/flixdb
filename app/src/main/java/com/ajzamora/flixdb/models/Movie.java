package com.ajzamora.flixdb.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Movie implements Parcelable {
    private static final String IMAGE_URL = "https://image.tmdb.org/t/p/w185";
    private static final String IMAGE_BACKDROP_URL = "https://image.tmdb.org/t/p/w500";

    private String mId;
    private String mTitle;
    private String mThumbnail;
    private String mBackdrop;
    private String mPlot;
    private String mRating;
    private String mReleaseDate;
    private List<Trailer> mTrailers;

    private Movie(Builder b) {
        mId = b.id;
        mTitle = b.title;
        mThumbnail = b.thumbnail;
        mBackdrop = b.backdrop;
        mPlot = b.plot;
        mRating = b.rating;
        mReleaseDate = b.releaseDate;
        mTrailers = b.trailers;
    }

    public static final class Builder {
        private String id;
        private String title;
        private String thumbnail;
        private String backdrop;
        private String plot;
        private String rating;
        private String releaseDate;
        private List<Trailer> trailers;

        public Builder() {

        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder thumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public Builder backdrop(String backdrop) {
            this.backdrop = backdrop;
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

        public Builder trailers(List<Trailer> trailers) {
            this.trailers = trailers;
            return this;
        }

        public Movie build() {
            return new Movie(this);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "id='" + id + '\'' +
                    "title='" + title + '\'' +
                    ", thumbnail='" + thumbnail + '\'' +
                    ", backdrop='" + backdrop + '\'' +
                    ", plot='" + plot + '\'' +
                    ", rating='" + rating + '\'' +
                    ", releaseDate='" + releaseDate + '\'' +
                    '}';
        }
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getThumbnail() {
        return IMAGE_URL.concat(mThumbnail);
    }

    public String getBackdrop() {
        return IMAGE_BACKDROP_URL.concat(mBackdrop);
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

    public void setTrailers(List<Trailer> trailers) { mTrailers = trailers; }

    public List<Trailer> getTrailers() {
        return mTrailers;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "mId='" + mId + '\'' +
                "mTitle='" + mTitle + '\'' +
                ", mThumbnail='" + mThumbnail + '\'' +
                ", mBackdrop='" + mBackdrop + '\'' +
                ", mPlot='" + mPlot + '\'' +
                ", mRating='" + mRating + '\'' +
                ", mReleaseDate='" + mReleaseDate + '\'' +
                '}';
    }

    protected Movie(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mThumbnail = in.readString();
        mBackdrop = in.readString();
        mPlot = in.readString();
        mRating = in.readString();
        mReleaseDate = in.readString();
        mTrailers = in.createTypedArrayList(Trailer.CREATOR);
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
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mThumbnail);
        dest.writeString(mBackdrop);
        dest.writeString(mPlot);
        dest.writeString(mRating);
        dest.writeString(mReleaseDate);
        dest.writeTypedList(mTrailers);
    }
}
