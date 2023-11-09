package com.example.novigradproject.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.novigradproject.MainActivity;
import com.example.novigradproject.databinding.ActivitySignUpBinding;
import com.example.novigradproject.repository.AuthRepository;
import com.example.novigradproject.repository.AuthRepositoryImpl;
import com.example.novigradproject.utils.EmailUtils;

public class SignUpActivity extends AppCompatActivity {
    private final AuthRepository authRepository = new AuthRepositoryImpl();
    private ActivitySignUpBinding binding;

    private Boolean isEmployee = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
    }

    private void setupUI() {
        binding.signUpLogin.setOnClickListener(v -> {
            goToLoginScreen();
        });

        binding.signUpAsEmployee.setOnClickListener(v -> {
            isEmployee = !isEmployee;
            if (isEmployee) {
                //Create confirmation AlertDialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUpActivity.this);
                alertDialogBuilder
                        .setTitle("Sign Up as Employee?")
                        .setMessage("Confirm Sign Up as Employee. You might need to verify your credentials in future")
                        .setCancelable(true)
                        .setPositiveButton(
                                "YES",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }
                        )
                        .setNegativeButton(
                                "NO",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        binding.signUpAsEmployee.toggle();
                                        isEmployee = false;
                                    }
                                }
                        );
                //Show AlertDialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        binding.signUpButton.setOnClickListener(v -> {
            String name = binding.signUpNameEntry.getText().toString();
            String userName = binding.signUpUsernameEntry.getText().toString();
            String password = binding.signUpPasswordEntry.getText().toString();
            String confirmPassword = binding.signUpPasswordConfirm.getText().toString();

            // Check if any field is empty
            if (name.isEmpty() || userName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (!EmailUtils.isValidUserName(name)) {
                Toast.makeText(this, "Username must contain only English alphabets and numbers", Toast.LENGTH_SHORT).show();
            } else {
                authRepository.signUpUser(userName, password);
            }
        });
    }

    private void goToLoginScreen() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goToHomeScreen() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}