package com.c0mm4nd.paindroid.model.pain;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.c0mm4nd.paindroid.entity.PainRecord;
import com.c0mm4nd.paindroid.repository.PainRecordRepository;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PainRecordViewModel extends AndroidViewModel {
    private final PainRecordRepository repo;
    private LiveData<List<PainRecord>> allPainRecords;

    public PainRecordViewModel(Application application) {
        super(application);
        repo = new PainRecordRepository(application);
        allPainRecords = repo.getAllLiveList();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByIDFuture(final int pid) {
        return repo.findByIDFuture(pid);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByDateFuture(final Date date) {
        int dateTS = (int) (date.getTime() / 1000);
        return repo.findByDateFuture(dateTS);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecord>> findByDateRangeFuture(final Date startDate, final Date endDate) {
        int startTS = (int) (startDate.getTime() / 1000);
        int endTS = (int) (endDate.getTime() / 1000);
        return repo.findAllInDateRange(startTS, endTS);
    }

    public LiveData<List<PainRecord>> getAllLiveList() {
        return allPainRecords;
    }

    public void insert(PainRecord painRecord) {
        repo.insert(painRecord);
    }

    public void deleteAll() {
        repo.deleteAll();
    }

    public void delete(PainRecord painRecord) {
        repo.delete(painRecord);
    }

    public void update(PainRecord painRecord) {
        repo.update(painRecord);
    }
}
