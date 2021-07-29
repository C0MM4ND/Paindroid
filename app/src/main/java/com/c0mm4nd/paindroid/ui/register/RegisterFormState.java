package com.c0mm4nd.paindroid.ui.register;

import androidx.annotation.Nullable;

/**
 * Data validation state of the register form.
 */
class RegisterFormState {
    @Nullable
    private Integer usernameFormatError;
    @Nullable
    private Integer passwordFormatError;
    @Nullable
    private Integer passwordsMatchError;

    private boolean isDataValid;

    RegisterFormState(@Nullable Integer usernameFormatError, @Nullable Integer passwordFormatError, @Nullable Integer passwordsMatchError) {
        this.usernameFormatError = usernameFormatError;
        this.passwordFormatError = passwordFormatError;
        this.passwordsMatchError = passwordsMatchError;
        this.isDataValid = false;
    }

    RegisterFormState(boolean isDataValid) {
        this.usernameFormatError = null;
        this.passwordFormatError = null;
        this.passwordsMatchError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getUsernameFormatError() {
        return usernameFormatError;
    }

    @Nullable
    Integer getPasswordFormatError() {
        return passwordFormatError;
    }

    @Nullable
    public Integer getPasswordsMatchError() {
        return passwordsMatchError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}