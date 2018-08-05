package com.ahmedmatem.android.lddrecords.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ahmedmatem.android.lddrecords.R;
import com.ahmedmatem.android.lddrecords.database.entities.Record;
import com.ahmedmatem.android.lddrecords.models.Picture;
import com.ahmedmatem.android.lddrecords.utils.PictureUtil;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private OnPhotoClickListener mCallback;
    private LayoutInflater mLayoutInflater;
    private List<Picture> mPictures;

    public interface OnPhotoClickListener{
        void onPhotoClick();
        void onDeletePhotoClick(int position);
    }

    public ImageAdapter(Context context, OnPhotoClickListener callback, List<Picture> pictures) {
        mContext = context;
        mCallback = callback;
        mLayoutInflater = LayoutInflater.from(context);
        mPictures = pictures;
    }

    @Override
    public int getCount() {
        return mPictures == null ? 0 : mPictures.size();
    }

    @Override
    public Object getItem(int position) {
        return mPictures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.image_grid_view_item,
                    parent, false);

            viewHolder = new ViewHolder(convertView);
            viewHolder.deleteIcon.setOnClickListener(v -> {
                if (mCallback != null) {
                    mCallback.onDeletePhotoClick(position);
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Picture pic = mPictures.get(position);
        if (pic.isDeleted()) {
            viewHolder.photoImageView.setAlpha(0.3f);
            viewHolder.deleteIcon
                    .setImageDrawable(mContext.getDrawable(R.drawable.ic_refresh_black_24dp));
        } else {
            viewHolder.photoImageView.setAlpha(1f);
            viewHolder.deleteIcon
                    .setImageDrawable(mContext.getDrawable(R.drawable.ic_close_black_24dp));
        }
        Bitmap bmp = PictureUtil.decodeFile(pic.getPath(), 150);
        viewHolder.photoImageView.setImageBitmap(bmp);
        viewHolder.photoImageView.setOnClickListener( view -> {
            if (mCallback != null) {
                mCallback.onPhotoClick();
            }
        });

        return convertView;
    }

    public void setPictures(List<Picture> pictures) {
        mPictures = pictures;
        notifyDataSetChanged();
    }

    class ViewHolder{
        private ImageView photoImageView;
        private ImageView deleteIcon;

        public ViewHolder(View itemView) {
            this.photoImageView = itemView.findViewById(R.id.iv_photo);
            this.deleteIcon = itemView.findViewById(R.id.iv_delete);
        }
    }
}
