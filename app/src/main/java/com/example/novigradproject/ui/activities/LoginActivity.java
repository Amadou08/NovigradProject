package com.example.novigradproject.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.novigradproject.MainActivity;
import com.example.novigradproject.R;
import com.example.novigradproject.databinding.ActivityLoginBinding;
import com.example.novigradproject.model.user.Person;
import com.example.novigradproject.runtime.GlobalData;
import com.example.novigradproject.utils.Constants;
import com.example.novigradproject.utils.EmailUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "##@@@LoginAct";
    private ActivityLoginBinding binding;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        setupUI();
    }

    private void setupUI() {
        binding.loginCreateNewAccount.setOnClickListener(v -> {
            goToSignUpScreen();
        });

        binding.loginButton.setOnClickListener(v -> {
            String userName = binding.loginUserNameEntry.getText().toString().trim();
            String password = binding.loginPasswordEntry.getText().toString();

            if (userName.isEmpty() || password.isEmpty()) {
                showAppropriateFieldEmptyErrorToast(userName, password);
            } else if (!EmailUtils.isValidUserName(userName)) {
                Toast.makeText(this, "Username must contain only English alphabets and numbers", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(userName, password);
            }
        });
    }

    private void loginUser(String userName, String password) {
        String email = EmailUtils.getDomainSuffixedEmailAddress(userName);
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.i(TAG, "loginUser(): success uid= " + auth.getUid());
                GlobalData.user.id = auth.getUid();
                firestore.collection(Constants.USERS_COLLECTION).document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Person temp = documentSnapshot.toObject(Person.class);
                        Log.v(TAG, "loginUser(): documentSnapshot= " + temp.email);
                        String name = (String) documentSnapshot.getString("name");
                        String role = (String) documentSnapshot.getString("role");
                        String email = (String) documentSnapshot.getString("email");
                        String firstName = (String) documentSnapshot.getString("firstName");
                        String lastName = (String) documentSnapshot.getString("lastName");
                        String userName = (String) documentSnapshot.getString("userName");
                        Log.v(TAG, "loginUser(): data=> " + name + "  " + role + " " + email + "  " + firstName + "  " + lastName + "  " + userName);

                        getSharedPreferences(Constants.ACCOUNT_PREF, Context.MODE_PRIVATE).edit().putString(Constants.FIELD_NAME, name).putString(Constants.FIELD_USERNAME, userName).putString(Constants.FIELD_EMAIL, email).putString(Constants.FIELD_ROLE, role).putString(Constants.FIELD_ID, auth.getUid()).putString(Constants.FIELD_FIRST_NAME, firstName).putString(Constants.FIELD_LAST_NAME, lastName).apply();

                        GlobalData.user.id = auth.getUid();
                        GlobalData.user.userName = userName;
                        GlobalData.user.email = email;
                        GlobalData.user.name = name;
                        GlobalData.user.firstName = firstName;
                        GlobalData.user.lastName = lastName;
                        GlobalData.user.role = role;

                        Log.i(TAG, "loginUser(): fetching user Data");
                        goToHomeScreen();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    //Failed to get data from database
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "loginUser(): fetch user Data failed");
                        Toast.makeText(LoginActivity.this, "Failed to fetch UserDetails from DB", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Login Failed: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToSignUpScreen() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goToHomeScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showAppropriateFieldEmptyErrorToast(String userName, String password) {
        String msgPrefix = "";
        if (userName.isEmpty()) msgPrefix = "UserName";
        else if (password.isEmpty()) msgPrefix = "Password";

        Toast.makeText(LoginActivity.this, msgPrefix + " Field Cannot be empty", Toast.LENGTH_SHORT).show();
    }
}