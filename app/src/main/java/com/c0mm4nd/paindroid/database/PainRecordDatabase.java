package com.c0mm4nd.paindroid.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.c0mm4nd.paindroid.dao.PainRecordDAO;
import com.c0mm4nd.paindroid.entity.PainRecord;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {PainRecord.class}, version = 1, exportSchema = false)
public abstract class PainRecordDatabase extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static PainRecordDatabase INSTANCE;

    public static synchronized PainRecordDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    PainRecordDatabase.class, "PainRecordDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return INSTANCE;
    }

    public abstract PainRecordDAO painDao();
}
