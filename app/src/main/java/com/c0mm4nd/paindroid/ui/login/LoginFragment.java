package com.c0mm4nd.paindroid.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.c0mm4nd.paindroid.LoginActivity;
import com.c0mm4nd.paindroid.databinding.FragmentLoginBinding;
import com.c0mm4nd.paindroid.ui.register.RegisterFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mAuth = ((LoginActivity) requireActivity()).getAuth();
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginViewModel.getLoginFormState().observe(requireActivity(), new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                binding.login.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    binding.username.setError(getString(loginFormState.getUsernameError()));
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
                loginViewModel.loginDataChanged(binding.username.getText().toString(),
                        binding.password.getText().toString());
            }
        };

        binding.username.addTextChangedListener(afterTextChangedListener);
        binding.password.addTextChangedListener(afterTextChangedListener);

        binding.password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // when press enter
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.loading.setVisibility(View.VISIBLE);
                    Task<AuthResult> result = mAuth.signInWithEmailAndPassword(binding.username.getText().toString(),
                            binding.password.getText().toString());

                    result.addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            handleResult(task);
                        }
                    });
                }
                return false;
            }
        });

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.loading.setVisibility(View.VISIBLE);
                Task<AuthResult> result = mAuth.signInWithEmailAndPassword(binding.username.getText().toString(),
                        binding.password.getText().toString());

                result.addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleResult(task);
                    }
                });
            }
        });

        binding.registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) requireActivity()).popupFragment(new RegisterFragment());
            }
        });
    }

    private void handleResult(Task<AuthResult> taskResult) {
        if (taskResult.isSuccessful()) {
            // Sign in success, update UI with the signed-in user's information
            Log.d("ok", "signInWithEmail:success");
            Toast.makeText(getActivity(), "successfully logged in", Toast.LENGTH_LONG).show();
            ((LoginActivity) getActivity()).loginToMain();
        } else {
            Log.w("err", "signInWithEmail:failure", taskResult.getException());
            Toast.makeText(getActivity(), "login failed: " + taskResult.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

        binding.loading.setVisibility(View.INVISIBLE);
    }
}
