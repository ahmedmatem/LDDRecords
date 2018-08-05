package com.ahmedmatem.android.lddrecords;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ahmedmatem.android.lddrecords.adapters.RecordListAdapter;
import com.ahmedmatem.android.lddrecords.database.entities.Record;
import com.ahmedmatem.android.lddrecords.helpers.RecyclerItemTouchHelper;
import com.ahmedmatem.android.lddrecords.models.Picture;
import com.ahmedmatem.android.lddrecords.utils.DateUtil;
import com.ahmedmatem.android.lddrecords.utils.PictureUtil;
import com.ahmedmatem.android.lddrecords.viewmodels.RecordViewModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.ahmedmatem.android.lddrecords.AddRecordActivity.EXTRA_REPLY;

public class MainActivity extends AppCompatActivity implements RecordListAdapter.OnRecordClickListener,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
    private static final String TAG = "MainActivity";

    static final int REQUEST_TAKE_PHOTO = 1;

    public static final int ADD_RECORD_ACTIVITY_REQUEST_CODE = 2;
    public static final int EDIT_RECORD_ACTIVITY_REQUEST_CODE = 4;

    public static final String ACTION_EXTRA = "com.ahmedmatem.android,lddrecords.ACTION";
    public static final String ACTION_ADD = "com.ahmedmatem.android,lddrecords.ADD";
    public static final String ACTION_EDIT = "com.ahmedmatem.android,lddrecords.EDIT";
    public static final String RECORD_EXTRA = "com.ahmedmatem.android,lddrecords.RECORD";

    private CoordinatorLayout mCoordinatorLayout;

    private RecordViewModel mRecordViewModel;

    private RecordListAdapter adapter;
    private Record mCurrentRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        mCoordinatorLayout = findViewById(R.id.coordinator_layout);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecordListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        mRecordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);

        mRecordViewModel.getAllRecordLiveData().observe(this, records -> {
            // Update the cached copy of the words in the adapter.
            adapter.setRecords(records);
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
            intent.putExtra(ACTION_EXTRA, ACTION_ADD);
            startActivityForResult(intent, ADD_RECORD_ACTIVITY_REQUEST_CODE);
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_RECORD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Record record = data.getParcelableExtra(EXTRA_REPLY);
            record.setUpdatedAt(DateUtil.getCurrentDate());
            record = clearDeletedPhotosFromRecord(record);
            mRecordViewModel.insert(record);
        } else if(requestCode == EDIT_RECORD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Record record = data.getParcelableExtra(EXTRA_REPLY);
            record.setUpdatedAt(DateUtil.getCurrentDate());
            record = clearDeletedPhotosFromRecord(record);
            mRecordViewModel.update(record);
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            galleryAddPicture();
            Picture pic = new Picture(new PictureUtil(this).getCurrentPicturePath());
            mCurrentRecord.addPicture(pic);
            mRecordViewModel.update(mCurrentRecord);

        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof RecordListAdapter.RecordViewHolder) {
            Record record = adapter.getRecords().get(viewHolder.getAdapterPosition());

            // remove record from recycler view
            record.setDeleted(true);
            mRecordViewModel.update(record);

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(mCoordinatorLayout, "Tag: " + record.getTag() + " removed from list to archive!",
                            Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", view -> {
                record.setDeleted(false);
                mRecordViewModel.update(record);
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private Record clearDeletedPhotosFromRecord(Record record) {
        if(record.getPictures() == null){
            return record;
        }
        int photosCount = record.getPictures().size();
        for(int i = photosCount - 1; i >= 0; i--) {
            if (record.getPictures().get(i).isDeleted()) {
                File photo = new File(record.getPictures().get(i).getPath());
                photo.delete();
                record.getPictures().remove(i);
            }
        }
        record.setPictures(record.getPictures());
        return record;
    }

    @Override
    public void onEditClick(Record record) {
        Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ACTION_EXTRA, ACTION_EDIT);
        bundle.putParcelable(RECORD_EXTRA, record);
        intent.putExtras(bundle);
        startActivityForResult(intent, EDIT_RECORD_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onEmailClick(Record record) {
        composeEmail(record);
    }

    @Override
    public void onAddPhotoClicked(Record record) {
        mCurrentRecord = record;
        dispatchTakePictureIntent();
    }

    @Override
    public void onPhotoClick(Record record) {
        Intent intent = new Intent(this, PhotosFullscreenActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(RECORD_EXTRA, record);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = new PictureUtil(this).createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.ahmedmatem.android.lddrecords.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_archive:
                showArchive();
                return true;
            case R.id.action_settings:
                loadSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showArchive() {
        Intent intent = new Intent(this, ArchiveActivity.class);
        startActivity(intent);
    }

    private void loadSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void composeEmail(Record record) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String to = sharedPreferences.getString("pref_key_email_to",
                getString(R.string.email_to_default));
        String subject = sharedPreferences.getString("pref_key_email_subject",
                getString(R.string.email_subject_default));

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//        intent.setType("*/*");
        intent.setType("message/rfc822"); // only email apps
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, record.toEmailBody());
        if(record.getPictures() != null && record.getPictures().size() > 0) {
            intent.putExtra(Intent.EXTRA_STREAM, record.getPictureUris(this));
        }
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }
}
