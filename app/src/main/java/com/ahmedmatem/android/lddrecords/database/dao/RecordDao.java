package com.ahmedmatem.android.lddrecords.database.dao;

import java.util.List;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ahmedmatem.android.lddrecords.database.entities.Record;


@Dao
public interface RecordDao {

    @Query("SELECT * FROM records WHERE NOT(deleted) ORDER BY updatedAt DESC")
    LiveData<List<Record>> getAllRecords();

    @Query("SELECT * FROM records WHERE deleted ORDER BY updatedAt DESC")
    LiveData<List<Record>> getArchive();

//    @Query("SELECT * FROM records WHERE id = :id")
//    LiveData<Record> getRecordById(int id);

    @Query("DELETE FROM records")
    void deleteAll();

    @Insert
    void insertRecord(Record record);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRecord(Record record);

    @Delete
    void deleteRecord(Record record);
}
