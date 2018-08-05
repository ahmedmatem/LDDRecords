package com.ahmedmatem.android.lddrecords;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ahmedmatem.android.lddrecords.utils.PictureUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class PhotoFragment extends Fragment {

    private static final int PHOTO_REQUIRED_SIZE = 200;

    public interface OnFullScreenListener{
        void onFullScreenClick();
    }

    private OnFullScreenListener mListener;

    private static final String ARG_PHOTO_PATH = "photo_path";

    public PhotoFragment() {
    }

    public static PhotoFragment newInstance(String photoPath) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PHOTO_PATH, photoPath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        ImageView photo = rootView.findViewById(R.id.iv_photo_full_screen);
        Bitmap bmp = PictureUtil.decodeFile(getArguments().getString(ARG_PHOTO_PATH),
                PHOTO_REQUIRED_SIZE);
        Log.d("PhotoFragment", "onCreateView: file size = " + bmp.getByteCount() / 1000 + "KB");
        photo.setImageBitmap(bmp);
        photo.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onFullScreenClick();
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFullScreenListener) {
            mListener = (PhotoFragment.OnFullScreenListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFullScreenListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
