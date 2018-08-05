package com.ahmedmatem.android.lddrecords.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.ahmedmatem.android.lddrecords.database.entities.Record;
import com.ahmedmatem.android.lddrecords.repositories.RecordRepository;

import java.util.List;

public class RecordViewModel extends AndroidViewModel {

    private RecordRepository mRecordRepository;

    private LiveData<List<Record>> mAllRecordLiveData;

    private LiveData<List<Record>> mArchiveLiveData;

    public RecordViewModel(@NonNull Application application) {
        super(application);
        mRecordRepository = new RecordRepository(application);
        mAllRecordLiveData = mRecordRepository.getAllRecords();
        mArchiveLiveData = mRecordRepository.getArchive();
    }

    public LiveData<List<Record>> getAllRecordLiveData() {
        return mAllRecordLiveData;
    }

    public LiveData<List<Record>> getArchiveLiveData() {
        return mArchiveLiveData;
    }

    public void insert(Record record){
        mRecordRepository.insert(record);
    }

    public void update(Record record){
        mRecordRepository.update(record);
    }

    public void delete(Record record) {
        mRecordRepository.delete(record);
    }
}
