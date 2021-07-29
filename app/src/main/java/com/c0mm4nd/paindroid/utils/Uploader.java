package com.c0mm4nd.paindroid.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.c0mm4nd.paindroid.entity.PainRecord;
import com.c0mm4nd.paindroid.repository.PainRecordRepository;
import com.c0mm4nd.paindroid.utils.DateUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class Uploader extends Worker {
    private final PainRecordRepository repo;

    public Uploader(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        repo = new PainRecordRepository(context);
    }


    @NotNull
    @Override
    public Result doWork() {
        return uploadRecords();
    }

    private Result uploadRecords() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Log.e("DELETEME", "user is null on uploading records");
            return Result.failure();
        }

        String uid = user.getUid();
        DatabaseReference userDB = database.child("user").child(uid);
        DatabaseReference todayDB = userDB.child("date").child(DateUtil.dateToString(DateUtil.getToday()));

        try {
            repo.findAllByDateFuture(DateUtil.dateToTimestamp(DateUtil.getToday())).thenAccept(new Consumer<List<PainRecord>>() {
                @Override
                public void accept(List<PainRecord> painRecords) {
                    todayDB.setValue(painRecords);

                    todayDB.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("DELETEME", "failed getting data: " + task.getException().getLocalizedMessage());
                            } else {
                                Log.d("DELETEME", "Saved into firebase today: " + task.getResult().getValue());
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.e("DELETEME", e.getLocalizedMessage());
            return Result.retry();
        }


        return Result.success();
    }
}
