package com.c0mm4nd.paindroid.ui.record;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.c0mm4nd.paindroid.MainActivity;
import com.c0mm4nd.paindroid.databinding.LayoutRecordBinding;
import com.c0mm4nd.paindroid.entity.PainRecord;
import com.c0mm4nd.paindroid.model.pain.PainRecordViewModel;
import com.c0mm4nd.paindroid.ui.entry.EntryFragment;
import com.c0mm4nd.paindroid.utils.DateUtil;

import java.util.List;
import java.util.Locale;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final Context context;
    private final MainActivity activity;
    private final PainRecordViewModel painRecordViewModel;
    private List<PainRecord> painRecords;

    public RecyclerViewAdapter(@NonNull Context context, PainRecordViewModel painRecordViewModel, List<PainRecord> painRecords) {
        this.context = context;
        activity = (MainActivity) context;
        this.painRecordViewModel = painRecordViewModel;
        this.painRecords = painRecords;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutRecordBinding binding = LayoutRecordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int pos) {
        final PainRecord painRecord = painRecords.get(pos);

        viewHolder.binding.recordPid.setText(String.format(Locale.getDefault(), "ID: %d ", painRecord.pid));
        viewHolder.binding.recordDate.setText(DateUtil.timestampToString(painRecord.painDate));
        viewHolder.binding.recordPainLevel.setText(String.format(Locale.getDefault(), "Pain Level: %d ", painRecord.painLevel));

        viewHolder.binding.recordEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EntryFragment nextFragment = new EntryFragment(painRecord.pid);
                activity.popupFragment(nextFragment);
            }
        });

        viewHolder.binding.recordDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: add a make sure dialog
                painRecords.remove(painRecord);
                painRecordViewModel.delete(painRecord);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return painRecords.size();
    }

    public void addList(List<PainRecord> painRecords) {
        this.painRecords = painRecords;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LayoutRecordBinding binding;

        public ViewHolder(LayoutRecordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}