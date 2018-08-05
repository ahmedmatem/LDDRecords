package com.ahmedmatem.android.lddrecords.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.FileProvider;

import com.ahmedmatem.android.lddrecords.models.Picture;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "records")
public class Record implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "block")
    private String mBlock;
    @ColumnInfo(name = "level")
    private String mLevel;
    @ColumnInfo(name = "tag")
    private String mTag;
    @ColumnInfo(name = "type")
    private String mType;
    @ColumnInfo(name = "size")
    private String mSize;
    @ColumnInfo(name = "addInfo")
    private String mAdditionalInfo;
    @ColumnInfo(name = "fireStoppers")
    private String mFireStoppers;
    @ColumnInfo(name = "updatedAt")
    private Date mUpdatedAt;
    @ColumnInfo(name = "photos")
    private List<Picture> mPictures;
    @ColumnInfo(name = "deleted")
    private boolean mDeleted;

    @Ignore
    public Record(){}

    @Ignore
    public Record(String block,
                  String level,
                  String tag,
                  String type,
                  String size,
                  String additionalInfo,
                  String fireStoppers,
                  Date updatedAt) {
        mBlock = block;
        mLevel = level;
        mTag = tag;
        mType = type;
        mSize = size;
        mAdditionalInfo = additionalInfo;
        mFireStoppers = fireStoppers;
        mUpdatedAt = updatedAt;
        mDeleted = false;
    }

    public Record(int id,
                  String block,
                  String level,
                  String tag,
                  String type,
                  String size,
                  String additionalInfo,
                  String fireStoppers,
                  Date updatedAt) {
        this(block, level, tag, type, size, additionalInfo, fireStoppers, updatedAt);
        this.id = id;
    }


    protected Record(Parcel in) {
        id = in.readInt();
        mBlock = in.readString();
        mLevel = in.readString();
        mTag = in.readString();
        mType = in.readString();
        mSize = in.readString();
        mAdditionalInfo = in.readString();
        mFireStoppers = in.readString();
        mPictures = in.createTypedArrayList(Picture.CREATOR);
        mDeleted = in.readInt() == 1 ? true : false;
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBlock() {
        return mBlock;
    }

    public void setBlock(String block) {
        mBlock = block;
    }

    public String getLevel() {
        return mLevel;
    }

    public void setLevel(String level) {
        mLevel = level;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getSize() {
        return mSize;
    }

    public void setSize(String size) {
        mSize = size;
    }

    public String getAdditionalInfo() {
        return mAdditionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        mAdditionalInfo = additionalInfo;
    }

    public String getFireStoppers() {
        return mFireStoppers;
    }

    public void setFireStoppers(String fireStoppers) {
        mFireStoppers = fireStoppers;
    }

    public Date getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public List<Picture> getPictures() {
        return mPictures;
    }

    public void setPictures(List<Picture> pictures) {
        mPictures = pictures;
    }

    public boolean isDeleted() {
        return mDeleted;
    }

    public void setDeleted(boolean deleted) {
        mDeleted = deleted;
    }

    public void addPicture(Picture pic){
        if (mPictures == null) {
            mPictures = new ArrayList<>();
        }
        this.mPictures.add(pic);
    }

    public ArrayList<Uri> getPictureUris(Context context){
        if (mPictures == null) {
            return null;
        }
        ArrayList<Uri> uris = new ArrayList<>();
        Uri uri;
        for (Picture picture : mPictures) {
            uri = FileProvider.getUriForFile(context,
                    "com.ahmedmatem.android.lddrecords.fileprovider",
                    new File(picture.getPath()));
            uris.add(uri);
        }
        return uris;
    }

    public String toEmailBody() {
        StringBuilder body = new StringBuilder();
        body.append("Block " + getBlock())
                .append("\n")
                .append("Level " + getLevel())
                .append("\n")
                .append("Tag " + getTag())
                .append("\n")
                .append("Size - " + getSize())
                .append("\n")
                .append("Additional information:\n" + getAdditionalInfo());

        return body.toString();
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", mBlock='" + mBlock + '\'' +
                ", mLevel='" + mLevel + '\'' +
                ", mTag='" + mTag + '\'' +
                ", mType='" + mType + '\'' +
                ", mSize='" + mSize + '\'' +
                ", mAdditionalInfo='" + mAdditionalInfo + '\'' +
                ", mFireStoppers='" + mFireStoppers + '\'' +
                ", mUpdatedAt=" + mUpdatedAt +
                ", mPictures=" + mPictures +
                ", mDeleted=" + mDeleted +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(mBlock);
        dest.writeString(mLevel);
        dest.writeString(mTag);
        dest.writeString(mType);
        dest.writeString(mSize);
        dest.writeString(mAdditionalInfo);
        dest.writeString(mFireStoppers);
        dest.writeTypedList(mPictures);
        dest.writeInt(mDeleted ? 1 : 0);
    }
}
