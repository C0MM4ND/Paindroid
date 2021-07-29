package com.c0mm4nd.paindroid.ui.entry;

import androidx.annotation.Nullable;

class EntryFormState {
    @Nullable
    private Integer goalError;
    @Nullable
    private Integer physicalError;

    EntryFormState(@Nullable Integer goalError, @Nullable Integer physicalError) {
        this.goalError = goalError;
        this.physicalError = physicalError;
    }

    @Nullable
    public Integer getGoalError() {
        return goalError;
    }

    @Nullable
    public Integer getPhysicalError() {
        return physicalError;
    }
}