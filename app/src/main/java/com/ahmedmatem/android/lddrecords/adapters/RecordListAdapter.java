package com.ahmedmatem.android.lddrecords.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahmedmatem.android.lddrecords.R;
import com.ahmedmatem.android.lddrecords.database.entities.Record;
import com.ahmedmatem.android.lddrecords.models.Picture;
import com.ahmedmatem.android.lddrecords.utils.DateUtil;
import com.ahmedmatem.android.lddrecords.utils.PictureUtil;

import java.util.List;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordViewHolder> {
    private static final int PHOTO_REQUIRED_SIZE = 200;

    private Context mContext;
    private final LayoutInflater mInflater;
    private List<Record> mRecords; // Cached copy of records

    public List<Record> getRecords() {
        return mRecords;
    }

    private OnRecordClickListener mCallback;

    public interface OnRecordClickListener{
        void onEditClick(Record record);
        void onEmailClick(Record record);
        void onAddPhotoClicked(Record record);
        void onPhotoClick(Record record);
    }

    public RecordListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        if (context instanceof OnRecordClickListener) {
            mCallback = (OnRecordClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecordClickListener");
        }
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.record_list_item, parent, false);
        return new RecordViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        if(mRecords != null){
            Record record = mRecords.get(position);

            holder.tagTextView.setText(record.getTag() != null ? record.getTag() : "");
            holder.blockTextView.setText(String.format(
                    mContext.getString(R.string.block_text), record.getBlock() != null ? record.getBlock() : ""));
            holder.levelTextView.setText(String.format(
                    mContext.getResources().getString(R.string.level_text), record.getLevel() != null ? record.getLevel() : ""));
            holder.typeTextView.setText(String.format(
                    mContext.getString(R.string.type_text), record.getType() != null ? record.getType() : ""));
            holder.dateTextView.setText(DateUtil.getSimpleDate(record.getUpdatedAt()));

            List<Picture> pictures = record.getPictures();
            if(pictures != null && pictures.size() > 0){
                Bitmap bitmap = PictureUtil.decodeFile(pictures.get(pictures.size() - 1).getPath(),
                        PHOTO_REQUIRED_SIZE);
                holder.photoImageView.setImageBitmap(bitmap);
                holder.photoImageView.setVisibility(View.VISIBLE);
                holder.photoImageView.setOnClickListener(view -> {
                    if (mCallback != null) {
                        mCallback.onPhotoClick(mRecords.get(position));
                    }
                });
            } else {
                holder.photoImageView.setVisibility(View.GONE);
            }

            String addInfo = buildAdditionalInfo(record);
            if(addInfo.isEmpty()){
                holder.additionalInfoTextView.setVisibility(View.GONE);
            } else {
                holder.additionalInfoTextView.setText(addInfo);
                holder.additionalInfoTextView.setVisibility(View.VISIBLE);
            }

            if (record.getPictures() != null && record.getPictures().size() > 0) {
                holder.photoCountTextView.setText(String.valueOf(record.getPictures().size()));
                holder.photoCountTextView.setVisibility(View.VISIBLE);
            } else {
                holder.photoCountTextView.setVisibility(View.GONE);
            }
        } else {
            // Covers the case of data not being ready yet.
            holder.blockTextView.setText("No block");
        }

        holder.editButton.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onEditClick(mRecords.get(position));
            }
        });

        holder.emailButton.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onEmailClick(mRecords.get(position));
            }
        });

        holder.addPhotoImageView.setOnClickListener(view -> {
            if (mCallback != null) {
                mCallback.onAddPhotoClicked(mRecords.get(position));
            }
        });
    }

    private String buildAdditionalInfo(Record record) {
        StringBuilder addInfo = new StringBuilder("");
        if(!record.getSize().isEmpty()){
            addInfo.append(record.getSize()).append(", ");
        }
        if(!record.getAdditionalInfo().isEmpty()){
            addInfo.append(record.getAdditionalInfo());
        }

        return addInfo.toString();
    }

    @Override
    public int getItemCount() {
        return mRecords != null ? mRecords.size() : 0;
    }

    public void setRecords(List<Record> records){
        this.mRecords = records;
        notifyDataSetChanged();
    }

//    @Override
//    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
//    }

    public class RecordViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout viewBackground;
        public ConstraintLayout viewForeground;

        private final TextView tagTextView;
        private final TextView blockTextView;
        private final TextView levelTextView;
        private final TextView typeTextView;
        private final TextView dateTextView;
        private final ImageView photoImageView;
        private final TextView additionalInfoTextView;
        private final TextView photoCountTextView;
        private final Button editButton;
        private final Button emailButton;
        private ImageView addPhotoImageView;

        public RecordViewHolder(View itemView) {
            super(itemView);

            this.viewBackground = itemView.findViewById(R.id.view_background);
            this.viewForeground = itemView.findViewById(R.id.view_foreground);

            this.tagTextView = itemView.findViewById(R.id.tv_tag);
            this.blockTextView = itemView.findViewById(R.id.tv_block);
            this.levelTextView = itemView.findViewById(R.id.tv_level);
            this.typeTextView = itemView.findViewById(R.id.tv_type);
            this.dateTextView = itemView.findViewById(R.id.tv_date);
            this.photoImageView = itemView.findViewById(R.id.iv_photo);
            this.additionalInfoTextView = itemView.findViewById(R.id.tv_additional_info);
            this.photoCountTextView = itemView.findViewById(R.id.tv_photo_count);
            this.editButton = itemView.findViewById(R.id.btn_edit);
            this.emailButton = itemView.findViewById(R.id.btn_email);
            this.addPhotoImageView = itemView.findViewById(R.id.iv_add_photo_icon);
        }
    }
}
