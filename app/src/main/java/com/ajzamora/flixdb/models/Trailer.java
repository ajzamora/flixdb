package com.ajzamora.flixdb.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {
    private String mId;
    private String mKey;
    private String mSite;

    private Trailer(Builder b) {
        mId = b.id;
        mKey = b.key;
        mSite = b.site;
    }

    public static final class Builder {
        private String id;
        private String key;
        private String site;

        public Builder() {

        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder site(String site) {
            this.site = site;
            return this;
        }

        public Trailer build() {
            return new Trailer(this);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "id='" + id + '\'' +
                    ", key='" + key + '\'' +
                    ", site='" + site + '\'' +
                    '}';
        }
    }

    public String getId() {
        return mId;
    }

    public String getKey() {
        return mKey;
    }

    public String getSite() {
        return mSite;
    }

    @Override
    public String toString() {
        return "Trailer{" +
                "mId='" + mId + '\'' +
                ", mKey='" + mKey + '\'' +
                ", mSite='" + mSite + '\'' +
                '}';
    }

    protected Trailer(Parcel in) {
        mId = in.readString();
        mKey = in.readString();
        mSite = in.readString();
    }


    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mKey);
        dest.writeString(mSite);
    }
}
