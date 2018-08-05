package com.ahmedmatem.android.lddrecords.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.ahmedmatem.android.lddrecords.database.converters.DateConverter;
import com.ahmedmatem.android.lddrecords.database.converters.PictureConverter;
import com.ahmedmatem.android.lddrecords.database.dao.RecordDao;
import com.ahmedmatem.android.lddrecords.database.entities.Record;

import java.util.Date;

@Database(entities = {Record.class}, version = 1, exportSchema = false)
@TypeConverters(value = {DateConverter.class, PictureConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "lddDb";
    private static AppDatabase sInstance;

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(sInstance);
                }
            };

    public static AppDatabase getInstance(final Context context){
        if(sInstance == null){
            synchronized (AppDatabase.class){
                sInstance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        DATABASE_NAME)
//                        .addCallback(sRoomDatabaseCallback)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract RecordDao recordDao();

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void>{
        private final RecordDao mRecordDao;

        public PopulateDbAsync(AppDatabase db) {
            mRecordDao = db.recordDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mRecordDao.deleteAll();
            Record record = new Record("105", "Level 5", "L5/54", "Riser", "1200 x x250 x 2", "no add info", "IM.AA", new Date());
            mRecordDao.insertRecord(record);
            record = new Record("105", "Level 5", "L5/54", "Riser", "1200 x x250 x 2", "no add info", "IM.AA", new Date());
            mRecordDao.insertRecord(record);
            return null;
        }
    }
}
