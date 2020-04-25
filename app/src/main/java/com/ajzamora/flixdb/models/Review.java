package com.ajzamora.flixdb.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    private String mAuthor;
    private String mContent;

    public Review(Builder b) {
        mAuthor = b.author;
        mContent = b.content;
    }

    public static final class Builder {
        private String author;
        private String content;

        public Builder() {

        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Review build() {
            return new Review(this);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "author='" + author + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    @Override
    public String toString() {
        return "Review{" +
                "mAuthor='" + mAuthor + '\'' +
                ", mContent='" + mContent + '\'' +
                '}';
    }

    protected Review(Parcel in) {
        mAuthor = in.readString();
        mContent = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAuthor);
        dest.writeString(mContent);
    }

}
