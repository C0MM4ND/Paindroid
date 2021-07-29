package com.c0mm4nd.paindroid.ui.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.c0mm4nd.paindroid.MainActivity;
import com.c0mm4nd.paindroid.entity.PainRecord;
import com.c0mm4nd.paindroid.model.pain.PainRecordViewModel;
import com.c0mm4nd.paindroid.ui.entry.EntryFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class RecordFragment extends Fragment {
    private com.c0mm4nd.paindroid.databinding.FragmentRecordBinding binding;
    private PainRecordViewModel painRecordViewModel;
    private FirebaseAuth mAuth;
    private RecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mAuth = ((MainActivity) requireActivity()).getAuth();
        painRecordViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
                .create(PainRecordViewModel.class);

        binding = com.c0mm4nd.paindroid.databinding.FragmentRecordBinding.inflate(inflater, container, false);

        painRecordViewModel.getAllLiveList().observe(requireActivity(), new Observer<List<PainRecord>>() {
            @Override
            public void onChanged(@Nullable final List<PainRecord> painRecords) {
                // avoid sync when fragment not activated (no ctx)
                if (getContext() != null) {
                    adapter = new RecyclerViewAdapter(requireContext(), painRecordViewModel, painRecords);
                    binding.recyclerView.setAdapter(adapter);
                    layoutManager = new LinearLayoutManager(getContext());
                    binding.recyclerView.setLayoutManager(layoutManager);
                }
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).popupFragment(new EntryFragment(0));
            }
        });

        return binding.getRoot();
    }
}
