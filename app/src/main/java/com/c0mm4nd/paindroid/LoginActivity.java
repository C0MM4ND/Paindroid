package com.c0mm4nd.paindroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.c0mm4nd.paindroid.databinding.ActivityLoginBinding;
import com.c0mm4nd.paindroid.ui.login.LoginFragment;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    public ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        // init auth
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Log.d("DELETEME", "onStart: ");
            replaceFragment(new LoginFragment());
            return;
        }

        loginToMain();
    }

    public boolean replaceFragment(Fragment nextFragment) {
        if (nextFragment == null) {
            return false;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, nextFragment)
                .commit();
        return true;
    }

    public boolean popupFragment(Fragment nextFragment) {
        if (nextFragment == null) {
            return false;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, nextFragment)
                .addToBackStack(null)
                .commit();
        return true;
    }

    public void loginToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @NonNull
    public FirebaseAuth getAuth() {
        return mAuth;
    }
}
