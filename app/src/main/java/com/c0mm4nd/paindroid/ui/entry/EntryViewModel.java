package com.c0mm4nd.paindroid.ui.entry;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.c0mm4nd.paindroid.R;

public class EntryViewModel extends ViewModel {
    private final MutableLiveData<EntryFormState> entryFormState = new MutableLiveData<>();

    public LiveData<EntryFormState> getEntryFormState() {
        return entryFormState;
    }

    public void stepsChanged(String goal, String phy) {
        if (isStepsInvalid(goal)) {
            entryFormState.setValue(new EntryFormState(R.string.invalid_goal_steps, null));
            return;
        }

        if (isStepsInvalid(phy)) {
            entryFormState.setValue(new EntryFormState(null, R.string.invalid_physical_steps));
            return;
        }

        if (isGoalInvalid(goal, phy)) {
            entryFormState.setValue(new EntryFormState(R.string.goal_less_than_phy, R.string.goal_less_than_phy));
        }
    }

    private boolean isStepsInvalid(String steps) {
        try {
            Integer.parseInt(steps);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private boolean isGoalInvalid(String goal, String phy) {
        return Integer.parseInt(goal) < Integer.parseInt(phy);
    }
}
