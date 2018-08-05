package com.ahmedmatem.android.lddrecords;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ahmedmatem.android.lddrecords.database.entities.Record;
import com.ahmedmatem.android.lddrecords.utils.PictureUtil;

import java.io.File;
import java.io.IOException;

import static com.ahmedmatem.android.lddrecords.MainActivity.ACTION_ADD;
import static com.ahmedmatem.android.lddrecords.MainActivity.ACTION_EDIT;
import static com.ahmedmatem.android.lddrecords.MainActivity.ACTION_EXTRA;
import static com.ahmedmatem.android.lddrecords.MainActivity.RECORD_EXTRA;

public class AddRecordActivity extends AppCompatActivity
        implements RecordFormFragment.OnFragmentInteractionListener {
    public static final String EXTRA_REPLY = "com.ahmedmatem.android.lddrecords.REPLY";
    public static final String NEW_RECORD_TITLE = "New Record";
    public static final String EDIT_TITLE = "Edit Record";
    static final int REQUEST_TAKE_PHOTO = 1;
    static final String RECORD_KEY = "com.ahmedmatem.android.lddrecords.RECORD";

    private RecordFormFragment mRecordFormFragment;

    private Record mRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        setActivityTitle();

        if(findViewById(R.id.record_form_container) != null){
            if(savedInstanceState != null){
                mRecord = savedInstanceState.getParcelable(RECORD_KEY);
                mRecordFormFragment = RecordFormFragment.newInstance(mRecord);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.record_form_container, mRecordFormFragment)
                        .commit();
                return;
            }

            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            if (extras != null && extras.containsKey(ACTION_EXTRA)) {
                // in case of edit
                if (extras.get(ACTION_EXTRA).equals(ACTION_EDIT)) {
                    mRecord = extras.getParcelable(RECORD_EXTRA);
                }
            }

            mRecordFormFragment = RecordFormFragment.newInstance(mRecord);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.record_form_container, mRecordFormFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RECORD_KEY, mRecordFormFragment.getRecord());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.action_save:
                mRecord = mRecordFormFragment.getRecord();
                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, mRecord);
                setResult(RESULT_OK, replyIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            galleryAddPicture();
            mRecordFormFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void galleryAddPicture() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(new PictureUtil(this).getCurrentPicturePath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onAddImage() {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = new PictureUtil(this).createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

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

    private void setActivityTitle() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey(ACTION_EXTRA)) {
            switch (extras.getString(ACTION_EXTRA)) {
                case ACTION_ADD:
                    setTitle(NEW_RECORD_TITLE);
                    break;
                case ACTION_EDIT:
                    setTitle(EDIT_TITLE);
                    break;
                default:
            }
        }
    }
}
