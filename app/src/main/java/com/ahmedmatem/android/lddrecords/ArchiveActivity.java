package com.ahmedmatem.android.lddrecords;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ahmedmatem.android.lddrecords.adapters.RecordListAdapter;
import com.ahmedmatem.android.lddrecords.database.entities.Record;
import com.ahmedmatem.android.lddrecords.viewmodels.RecordViewModel;

public class ArchiveActivity extends AppCompatActivity implements RecordListAdapter.OnRecordClickListener {

    private RecordViewModel mRecordViewModel;

    private RecordListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecordListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        mRecordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);

        mRecordViewModel.getArchiveLiveData().observe(this, records -> {
            // Update the cached copy of the words in the adapter.
            adapter.setRecords(records);
        });
    }

    @Override
    public void onEditClick(Record record) {

    }

    @Override
    public void onEmailClick(Record record) {

    }

    @Override
    public void onAddPhotoClicked(Record record) {

    }

    @Override
    public void onPhotoClick(Record record) {

    }
}
