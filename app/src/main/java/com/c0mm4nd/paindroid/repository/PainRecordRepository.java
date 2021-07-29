package com.c0mm4nd.paindroid.repository;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.c0mm4nd.paindroid.dao.PainRecordDAO;
import com.c0mm4nd.paindroid.database.PainRecordDatabase;
import com.c0mm4nd.paindroid.entity.PainRecord;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class PainRecordRepository {
    private final PainRecordDAO painRecordDAO;
    private final LiveData<List<PainRecord>> allPainRecords;

    public PainRecordRepository(Application application) {
        PainRecordDatabase db = PainRecordDatabase.getInstance(application);
        painRecordDAO = db.painDao();
        allPainRecords = painRecordDAO.getAllLiveList();
    }

    public PainRecordRepository(Context context) {
        PainRecordDatabase db = PainRecordDatabase.getInstance(context);
        painRecordDAO = db.painDao();
        allPainRecords = painRecordDAO.getAllLiveList();
    }


    public LiveData<List<PainRecord>> getAllLiveList() {
        return allPainRecords;
    }

    public void insert(final PainRecord painRecord) {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.insert(painRecord);
            }
        });
    }

    public void deleteAll() {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.deleteAll();
            }
        });
    }

    public void delete(final PainRecord painRecord) {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.delete(painRecord);
            }
        });
    }

    public void update(final PainRecord painRecord) {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.update(painRecord);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByIDFuture(final int pid) {
        return CompletableFuture.supplyAsync(new Supplier<PainRecord>() {
            @Override
            public PainRecord get() {
                return painRecordDAO.findByID(pid);
            }
        }, PainRecordDatabase.databaseWriteExecutor);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByDateFuture(final int dateTimestamp) {
        return CompletableFuture.supplyAsync(new Supplier<PainRecord>() {
            @Override
            public PainRecord get() {
                return painRecordDAO.findByDate(dateTimestamp);
            }
        }, PainRecordDatabase.databaseWriteExecutor);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecord>> findAllByDateFuture(final int dateTimestamp) {
        return CompletableFuture.supplyAsync(new Supplier<List<PainRecord>>() {
            @Override
            public List<PainRecord> get() {
                return painRecordDAO.findAllByDate(dateTimestamp);
            }
        }, PainRecordDatabase.databaseWriteExecutor);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecord>> findAllInDateRange(final int start, final int end) {
        return CompletableFuture.supplyAsync(new Supplier<List<PainRecord>>() {
            @Override
            public List<PainRecord> get() {
                return painRecordDAO.findAllInDateRange(start, end);
            }
        }, PainRecordDatabase.databaseWriteExecutor);
    }

}
