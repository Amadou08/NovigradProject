package com.example.novigradproject.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.novigradproject.MainActivity;
import com.example.novigradproject.databinding.ActivitySignUpBinding;
import com.example.novigradproject.repository.AuthRepository;
import com.example.novigradproject.repository.AuthRepositoryImpl;
import com.example.novigradproject.runtime.GlobalData;
import com.example.novigradproject.utils.Constants;
import com.example.novigradproject.utils.EmailUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private final String TAG = "##@@@SignUpAct";

    private ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private Boolean isEmployee = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
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
            String name = binding.signUpNameEntry.getText().toString().trim();
            String userName = binding.signUpUsernameEntry.getText().toString().trim();
            String password = binding.signUpPasswordEntry.getText().toString();
            String confirmPassword = binding.signUpPasswordConfirm.getText().toString();

            if (name.isEmpty() || userName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showAppropriateFieldEmptyErrorToast(name, userName, password, confirmPassword);
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (!EmailUtils.isValidUserName(userName)) {
                Toast.makeText(this, "Username must contain only English alphabets and numbers", Toast.LENGTH_SHORT).show();
            } else {
                signupUser(userName, password, name);
            }
        });
    }

    private void signupUser(String userName, String password, String name) {
        String email = EmailUtils.getDomainSuffixedEmailAddress(userName);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //Get the date and time that the account is created
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
                        String currentDateAndTime = sdf.format(new Date());

                        //Create a map with the data to write to cloud firestore
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("name", name);
                        userInfo.put("email", email);
                        String role = isEmployee ? Constants.EMPLOYEE_ROLE : Constants.CUSTOMER_ROLE;
                        userInfo.put("role", role);
                        userInfo.put("timeCreated", currentDateAndTime);
                        firestore.collection(Constants.USERS_COLLECTION).document(auth.getUid()).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            //Only when firestore succeeds in writing user data to database does the app log the user in.
                            @Override
                            public void onSuccess(Void aVoid) {
                                getSharedPreferences(Constants.ACCOUNT_PREF, Context.MODE_PRIVATE).edit().putString(Constants.FIELD_NAME, name).putString(Constants.FIELD_USERNAME, userName).putString(Constants.FIELD_EMAIL, email).putString(Constants.FIELD_ROLE, role).putString(Constants.FIELD_ID, auth.getUid()).apply();
                                GlobalData.user.name = name;
                                GlobalData.user.userName = userName;
                                GlobalData.user.email = email;
                                GlobalData.user.role = role;
                                GlobalData.user.id = auth.getUid();
                                goToHomeScreen();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this, "Failed to write user data to DB: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                auth.getCurrentUser().delete();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "signUpUser(): ", e);
                        Toast.makeText(SignUpActivity.this, "Error creating account: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

    private void showAppropriateFieldEmptyErrorToast(String name, String userName, String password, String confirmPassword) {
        String msgPrefix = "";
        if (name.isEmpty()) msgPrefix = "Name";
        else if (userName.isEmpty()) msgPrefix = "UserName";
        else if (password.isEmpty()) msgPrefix = "Password";
        else if (confirmPassword.isEmpty()) msgPrefix = "Confirm Password";

        Toast.makeText(SignUpActivity.this, msgPrefix + " Field Cannot be empty", Toast.LENGTH_SHORT).show();
    }
}