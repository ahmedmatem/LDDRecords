package com.ahmedmatem.android.lddrecords.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Picture implements Parcelable {
    private String mPath;
    private boolean mDeleted;

    public Picture(String path) {
        mPath = path;
        mDeleted = false;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public boolean isDeleted() {
        return mDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        mDeleted = isDeleted;
    }

    protected Picture(Parcel in) {
        mPath = in.readString();
        mDeleted = in.readInt() == 1 ? true : false;
    }

    public static final Creator<Picture> CREATOR = new Creator<Picture>() {
        @Override
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
        dest.writeInt(mDeleted ? 1 : 0);
    }

    @Override
    public String toString() {
        return "Picture{" +
                "mPath='" + mPath + '\'' +
                ", mDeleted=" + mDeleted +
                '}';
    }
}
