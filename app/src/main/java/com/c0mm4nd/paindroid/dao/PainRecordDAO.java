package com.c0mm4nd.paindroid.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.c0mm4nd.paindroid.entity.PainRecord;

import java.util.List;

@Dao
public interface PainRecordDAO {
    @Query("SELECT * FROM PainRecord ORDER BY email, pain_date ASC")
    LiveData<List<PainRecord>> getAllLiveList();

    @Query("SELECT * FROM PainRecord WHERE pid = :pid LIMIT 1")
    PainRecord findByID(int pid);

    @Query("SELECT * FROM PainRecord WHERE pain_date = :dateTimestamp ORDER BY pid DESC LIMIT 1")
    PainRecord findByDate(int dateTimestamp); // get max pid one today

    @Query("SELECT * FROM PainRecord WHERE pain_date = :dateTimestamp")
    List<PainRecord> findAllByDate(int dateTimestamp); // get all today

    @Query("SELECT * FROM PainRecord WHERE pain_date >= :startTimestamp and pain_date <= :endTimestamp ORDER BY email, pain_date ASC")
    List<PainRecord> findAllInDateRange(int startTimestamp, int endTimestamp);

    @Insert
    void insert(PainRecord painRecord);

    @Delete
    void delete(PainRecord painRecord);

    @Update
    void update(PainRecord painRecord);

    @Query("DELETE FROM PainRecord")
    void deleteAll();
}
