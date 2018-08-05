package com.ahmedmatem.android.lddrecords.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.ahmedmatem.android.lddrecords.database.AppDatabase;
import com.ahmedmatem.android.lddrecords.database.dao.RecordDao;
import com.ahmedmatem.android.lddrecords.database.entities.Record;

import java.util.List;

public class RecordRepository {
    private RecordDao mRecordDao;
    private LiveData<List<Record>> mAllRecords;
    private LiveData<List<Record>> mArchive;

    public RecordRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        mRecordDao = db.recordDao();
        mAllRecords = mRecordDao.getAllRecords();
        mArchive = mRecordDao.getArchive();
    }

    public void insert(Record record){
        new InsertAsyncTask(mRecordDao).execute(record);
    }

    public void update(Record record){
        new UpdateAsyncTask(mRecordDao).execute(record);
    }

    public void delete(Record record) {
        new DeleteAsyncTask(mRecordDao).execute(record);
    }

    public LiveData<List<Record>> getAllRecords() {
        return mAllRecords;
    }

    public LiveData<List<Record>> getArchive() {
        return mArchive;
    }

    private class InsertAsyncTask extends AsyncTask<Record, Void, Void>{
        private RecordDao mAsyncRecordDao;

        public InsertAsyncTask(RecordDao recordDao) {
            mAsyncRecordDao = recordDao;
        }

        @Override
        protected Void doInBackground(Record... records) {
            Record record = records[0];
            mAsyncRecordDao.insertRecord(record);
            return null;
        }
    }

    private class UpdateAsyncTask extends AsyncTask<Record, Void, Void>{
        private RecordDao mAsyncRecordDao;

        public UpdateAsyncTask(RecordDao recordDao){
            this.mAsyncRecordDao = recordDao;
        }

        @Override
        protected Void doInBackground(Record... records) {
            Record record = records[0];
            mAsyncRecordDao.updateRecord(record);
            return null;
        }
    }

    private class DeleteAsyncTask extends AsyncTask<Record, Void, Void>{
        private RecordDao mAsyncRecordDao;

        public DeleteAsyncTask(RecordDao recordDao){
            this.mAsyncRecordDao = recordDao;
        }

        @Override
        protected Void doInBackground(Record... records) {
            Record record = records[0];
            mAsyncRecordDao.deleteRecord(record);
            return null;
        }
    }
}
