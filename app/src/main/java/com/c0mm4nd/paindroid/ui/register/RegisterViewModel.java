package com.c0mm4nd.paindroid.ui.register;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.c0mm4nd.paindroid.R;

public class RegisterViewModel extends ViewModel {
    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    public void registerDataChanged(String username, String password, String passwordRepeat) {
        if (!isUserNameValid(username)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_username, null, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_password, null));
        }else if (!password.equals(passwordRepeat)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.password_match));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return false;
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
