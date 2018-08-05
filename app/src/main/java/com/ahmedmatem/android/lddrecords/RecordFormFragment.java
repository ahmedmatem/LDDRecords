package com.ahmedmatem.android.lddrecords;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ahmedmatem.android.lddrecords.adapters.ImageAdapter;
import com.ahmedmatem.android.lddrecords.database.entities.Record;
import com.ahmedmatem.android.lddrecords.models.Picture;
import com.ahmedmatem.android.lddrecords.utils.PictureUtil;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.ahmedmatem.android.lddrecords.AddRecordActivity.REQUEST_TAKE_PHOTO;
import static com.ahmedmatem.android.lddrecords.MainActivity.RECORD_EXTRA;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordFormFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecordFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFormFragment extends Fragment implements ImageAdapter.OnPhotoClickListener{

    private static final String ARG_RECORD = "record";

    // ui elements
    private Spinner mBlockSpinner;
    private Spinner mLevelSpinner;
    private Spinner mTypeSpinner;
    private TextView mTagNumberInput;
    private TextView mSizeInput;
    private EditText mAdditionalInformationInput;
    private Button mAddImageButton;
    private GridView mGridView;

    private Record mRecord;

    private ImageAdapter mImageAdapter;

    private OnFragmentInteractionListener mListener;

    public RecordFormFragment() {
        // Required empty public constructor
    }


    public static RecordFormFragment newInstance(Record record) {
        RecordFormFragment fragment = new RecordFormFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RECORD, record);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecord = getArguments().getParcelable(ARG_RECORD);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record_form, container, false);

        mBlockSpinner = view.findViewById(R.id.block_spinner);
        setSpinnerAdapter( mBlockSpinner, R.array.block_array);
        mLevelSpinner = view.findViewById(R.id.level_spinner);
        setSpinnerAdapter( mLevelSpinner, R.array.level_array);
        mTypeSpinner = view.findViewById(R.id.record_type_spinner);
        setSpinnerAdapter( mTypeSpinner, R.array.record_type_array);
        mTagNumberInput = view.findViewById(R.id.et_tag);
        mSizeInput = view.findViewById(R.id.et_size);
        mAdditionalInformationInput = view.findViewById(R.id.et_additional_info);

        if (mRecord != null) {
            setRecordFormValues();
        }

        mAddImageButton = view.findViewById(R.id.btn_add_image);
        mAddImageButton.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onAddImage();
            }
        });

        mGridView = view.findViewById(R.id.image_gird_view);
        List<Picture> pictures = null;
        if(mRecord != null) {
            pictures = mRecord.getPictures();
        }
        mImageAdapter = new ImageAdapter(getContext(), this, pictures);
        mGridView.setAdapter(mImageAdapter);

        return view;
    }

    private void setRecordFormValues() {
        int position = findSelectionItemPosition(mRecord.getBlock(), R.array.block_array);
        if (position != -1) {
            mBlockSpinner.setSelection(position);
        }
        position = findSelectionItemPosition(mRecord.getLevel(), R.array.level_array);
        if (position != -1) {
            mLevelSpinner.setSelection(position);
        }
        position = findSelectionItemPosition(mRecord.getType(), R.array.record_type_array);
        if (position != -1) {
            mTypeSpinner.setSelection(position);
        }
        if(!mRecord.getTag().isEmpty()) {
            mTagNumberInput.setText(mRecord.getTag());
        }
        if(!mRecord.getSize().isEmpty()) {
            mSizeInput.setText(mRecord.getSize());
        }
        if(!mRecord.getAdditionalInfo().isEmpty()) {
            mAdditionalInformationInput.setText(mRecord.getAdditionalInfo());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Picture pic = new Picture(new PictureUtil(getContext()).getCurrentPicturePath());
            if(mRecord == null){
                mRecord = new Record();
            }
            mRecord.addPicture(pic);
            // update image GridView
            mImageAdapter.setPictures(mRecord.getPictures());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPhotoClick() {
        if (mRecord == null) {
            return;
        }
        Intent intent = new Intent(getContext(), PhotosFullscreenActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(RECORD_EXTRA, mRecord);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onDeletePhotoClick(int position) {
        Picture picture =mRecord.getPictures().get(position);
        picture.setDeleted(!picture.isDeleted());
        mImageAdapter.setPictures(mRecord.getPictures()); // update UI
    }

    public interface OnFragmentInteractionListener {
        void onAddImage();
    }

    public Record getRecord() {
        String block = mBlockSpinner.getSelectedItem().toString();
        String level = mLevelSpinner.getSelectedItem().toString();
        String type = mTypeSpinner.getSelectedItem().toString();
        String tag = mTagNumberInput.getText().toString();
        String size = mSizeInput.getText().toString();
        String additionalInformation = mAdditionalInformationInput.getText().toString();

        if(mRecord == null){
            mRecord = new Record(
                    block,
                    level,
                    tag,
                    type,
                    size,
                    additionalInformation,
                    "",
                    null);
        } else {
            mRecord.setBlock(block);
            mRecord.setLevel(level);
            mRecord.setTag(tag);
            mRecord.setType(type);
            mRecord.setSize(size);
            mRecord.setAdditionalInfo(additionalInformation);
            mRecord.setFireStoppers("");
            mRecord.setUpdatedAt(null);
        }

        return mRecord;
    }

    private void setSpinnerAdapter(Spinner spinner, int arrayResId){
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getContext(), arrayResId,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private int findSelectionItemPosition(String selectionItem, int arrayResourceId) {
        String[] strArray = getContext().getResources().getStringArray(arrayResourceId);
        for(int i = 0; i < strArray.length; i++) {
            if (selectionItem.equals(strArray[i])) {
                return i;
            }
        }
        return -1;
    }
}
