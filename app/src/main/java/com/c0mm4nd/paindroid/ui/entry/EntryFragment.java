package com.c0mm4nd.paindroid.ui.entry;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.c0mm4nd.paindroid.MainActivity;
import com.c0mm4nd.paindroid.databinding.FragmentEntryBinding;
import com.c0mm4nd.paindroid.entity.PainRecord;
import com.c0mm4nd.paindroid.model.pain.PainRecordViewModel;
import com.c0mm4nd.paindroid.model.weather.Weather;
import com.c0mm4nd.paindroid.utils.AlarmReceiver;
import com.c0mm4nd.paindroid.utils.DateUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

public class EntryFragment extends Fragment {
    private int entryID;
    private FragmentEntryBinding binding;
    private PainRecordViewModel painRecordViewModel;
    private EntryViewModel entryViewModel;
    private FirebaseAuth mAuth;

    public EntryFragment(int entryID) {
        this.entryID = entryID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEntryBinding.inflate(inflater, container, false);

        mAuth = ((MainActivity) requireActivity()).getAuth();
        painRecordViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(PainRecordViewModel.class);
        entryViewModel = new ViewModelProvider(requireActivity()).get(EntryViewModel.class);
        entryViewModel.getEntryFormState().observe(requireActivity(), new Observer<EntryFormState>() {
            @Override
            public void onChanged(EntryFormState entryFormState) {
                if (entryFormState == null) {
                    return;
                }
                // binding.saveEntryBtn.setEnabled(loginFormState.isDataValid());
                if (entryFormState.getGoalError() != null) {
                    binding.goalSteps.setError(getString(entryFormState.getGoalError()));
                }
                if (entryFormState.getPhysicalError() != null) {
                    binding.physicalSteps.setError(getString(entryFormState.getPhysicalError()));
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore, placeholder for TextWatcher
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore, placeholder for TextWatcher
            }

            @Override
            public void afterTextChanged(Editable s) {
                entryViewModel.stepsChanged(binding.goalSteps.getText().toString(),
                        binding.physicalSteps.getText().toString());
            }
        };
        binding.goalSteps.addTextChangedListener(afterTextChangedListener);
        binding.physicalSteps.addTextChangedListener(afterTextChangedListener);

        // init spinner
        List<String> list = new ArrayList<>(11);
        list.add("back");
        list.add("neck");
        list.add("head");
        list.add("knees");
        list.add("hips");
        list.add("abdomen");
        list.add("elbows");
        list.add("shoulders");
        list.add("shins");
        list.add("jaw");
        list.add("facial");

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, list);
        binding.painLocSpinner.setAdapter(spinnerAdapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // save - if pid=0 insert else update
        binding.saveEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (save()) {
                    Toast.makeText(getContext(), "record saved", Toast.LENGTH_LONG).show();
                    requireActivity().onBackPressed();
                }
            }
        });

        binding.painLevelSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.painLevelIndicator.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // ignore
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // ignore
            }
        });

        binding.editEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllEnabled();
            }
        });

        if (entryID == 0) {
            // insert mode
            binding.editEntryBtn.setEnabled(false);

            painRecordViewModel.findByDateFuture(DateUtil.getToday()).thenAccept(new Consumer<PainRecord>() {
                @Override
                public void accept(PainRecord painRecord) {
                    if (painRecord == null) {
                        Log.d("DELETEME", "cannot find today record ");
                    } else {
                        Log.d("DELETEME", "found today record, gone the timepicker ");
                        binding.painTimeLabel.setVisibility(View.GONE);
                        binding.painTimePicker.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            // edit mode
            binding.painTimeLabel.setVisibility(View.GONE);
            binding.painTimePicker.setVisibility(View.GONE);

            loadPainRecord();
            setAllDisabled();
        }
    }

    private void loadPainRecord() {
        painRecordViewModel.findByIDFuture(entryID).thenApply(new Function<PainRecord, Object>() {
            @Override
            public Object apply(PainRecord painRecord) {
                binding.painLevelSlider.setProgress(painRecord.painLevel);
                binding.painLocSpinner.setPrompt(painRecord.painLoc);
                binding.moodLevelSlider.setProgress(painRecord.moodLevel);
                binding.goalSteps.setText(String.format(Locale.getDefault(), "%d", painRecord.goalSteps));
                binding.physicalSteps.setText(String.format(Locale.getDefault(), "%d", painRecord.physicalSteps));

                return null;
            }
        });
    }

    private void setAllDisabled() {
        binding.painLevelSlider.setEnabled(false);
        binding.painLocSpinner.setEnabled(false);
        binding.moodLevelSlider.setEnabled(false);
        binding.goalSteps.setEnabled(false);
        binding.physicalSteps.setEnabled(false);
        binding.saveEntryBtn.setEnabled(false);
    }

    private void setAllEnabled() {
        binding.painLevelSlider.setEnabled(true);
        binding.painLocSpinner.setEnabled(true);
        binding.moodLevelSlider.setEnabled(true);
        binding.goalSteps.setEnabled(true);
        binding.physicalSteps.setEnabled(true);
        binding.saveEntryBtn.setEnabled(true);
        binding.editEntryBtn.setEnabled(false);
    }

    private boolean save() {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String email = user.getEmail();
        assert email != null;

        int painDate = DateUtil.dateToTimestamp(DateUtil.getToday());

        int painLevel = binding.painLevelSlider.getProgress();
        String painLoc = binding.painLocSpinner.getSelectedItem().toString();
        int moodLevel = binding.moodLevelSlider.getProgress();
        int goalSteps = Integer.parseInt(binding.goalSteps.getText().toString());
        int physicalSteps = Integer.parseInt(binding.physicalSteps.getText().toString());

        Weather weather = getWeatherFromStorage();
        if (weather == null) {
            Toast.makeText(getContext(), "cannot save the record: weather data is not ready", Toast.LENGTH_LONG).show();
            return false;
        }

        PainRecord painRecord = new PainRecord(email, painDate, painLevel, painLoc, moodLevel,
                goalSteps, physicalSteps, weather.temperature, weather.humidity, weather.pressure);
        if (entryID == 0) {
            painRecordViewModel.insert(painRecord);

            // if (painRecord.pid < 5) { // insert data for test
            // painRecord = new PainRecord(email, painDate-5*86400, painLevel, painLoc, moodLevel,
            //      goalSteps, physicalSteps, weather.temperature, weather.humidity, weather.pressure);
            // painRecordViewModel.insert(painRecord);
            // }
        } else {
            painRecord.pid = entryID;
            painRecordViewModel.update(painRecord);
        }

        if (binding.painTimePicker.getVisibility() == View.VISIBLE) {
            setAlarm(binding.painTimePicker.getHour(), binding.painTimePicker.getMinute());
        }

        return true;
    }

    private void setAlarm(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireActivity().getApplicationContext(), 0, intent, 0);

        Calendar c = Calendar.getInstance();
//        c.set(Calendar.SECOND, c.get(Calendar.SECOND) + 10); // for test
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.setTimeInMillis(c.getTimeInMillis() + (long) 86_400 * 1_000); // tomorrow
        alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        Log.d("DELETEME", "alarm set: " + DateUtil.dateToString(c.getTime()));
    }

    @Nullable
    private Weather getWeatherFromStorage() {
        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);

        double temperature = sharedPref.getFloat("weather_temp", (float) -280.0);
        if (temperature == -280.0) {
            return null;
        }

        int humid = sharedPref.getInt("weather_humid", -1);
        if (humid == -1) {
            return null;
        }

        int pressure = sharedPref.getInt("weather_pressure", -1);
        if (pressure == -1) {
            return null;
        }

        Weather weather = new Weather();
        weather.setTemperature(temperature);
        weather.setHumidity(humid);
        weather.setPressure(pressure);

        return weather;
    }
}
