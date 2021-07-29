package com.c0mm4nd.paindroid.ui.register;

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
import com.c0mm4nd.paindroid.databinding.FragmentRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private RegisterViewModel registerViewModel;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mAuth = ((LoginActivity) requireActivity()).getAuth();
        registerViewModel = new ViewModelProvider(requireActivity()).get(RegisterViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerViewModel.getRegisterFormState().observe(requireActivity(), new Observer<RegisterFormState>() {
            @Override
            public void onChanged(@Nullable RegisterFormState registerFormState) {
                if (registerFormState == null) {
                    return;
                }
                binding.register.setEnabled(registerFormState.isDataValid());
                if (registerFormState.getUsernameFormatError() != null) {
                    binding.username.setError(getString(registerFormState.getUsernameFormatError()));
                }
                if (registerFormState.getPasswordFormatError() != null) {
                    binding.password.setError(getString(registerFormState.getPasswordFormatError()));
                }
                if (registerFormState.getPasswordsMatchError() != null) {
                    binding.passwordRepeat.setError(getString(registerFormState.getPasswordsMatchError()));
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                registerViewModel.registerDataChanged(binding.username.getText().toString(),
                        binding.password.getText().toString(), binding.passwordRepeat.getText().toString());
            }
        };

        binding.username.addTextChangedListener(afterTextChangedListener);
        binding.password.addTextChangedListener(afterTextChangedListener);
        binding.passwordRepeat.addTextChangedListener(afterTextChangedListener);

        binding.password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // when press enter
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.loading.setVisibility(View.VISIBLE);

                    Task<AuthResult> result = mAuth.createUserWithEmailAndPassword(binding.username.getText().toString(),
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

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.loading.setVisibility(View.VISIBLE);

                String email = binding.username.getText().toString();
                String password = binding.password.getText().toString();
                Task<AuthResult> result = mAuth.createUserWithEmailAndPassword(email, password);
                result.addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleResult(task);
                    }
                });
            }
        });

        binding.registerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }

    private void handleResult(Task<AuthResult> taskResult) {
        if (taskResult.isSuccessful()) {
            // Sign in success, update UI with the signed-in user's information
            Log.d("ok", "signInWithEmail:success");
            Toast.makeText(getActivity(), "successfully registered", Toast.LENGTH_LONG).show();
            requireActivity().onBackPressed();
        } else {
            Log.w("err", "signInWithEmail:failure", taskResult.getException());
            Toast.makeText(getActivity(), "register failed: " + taskResult.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

        binding.loading.setVisibility(View.INVISIBLE);
    }
}
