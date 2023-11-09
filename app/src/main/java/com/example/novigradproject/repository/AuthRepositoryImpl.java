package com.example.novigradproject.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.novigradproject.utils.EmailUtils;
import com.example.novigradproject.utils.Lambda;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthRepositoryImpl implements AuthRepository {
    private final String TAG = "##@@@AuthRepoImpl";
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public void signUpUser(final String userName, final String password, final Lambda onSuccess, final Lambda onFailure, final Context context) {
        String email = EmailUtils.getDomainSuffixedEmailAddress(userName);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        onSuccess.f();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "signUpUser(): ", e);
                        onFailure.f(e);
                    }
                });
    }

    @Override
    public void logOutUser(final Lambda onSuccess, final Lambda onFailure) {

    }

    @Override
    public void loginUser(final String userName, final String password, final Lambda onSuccess, final Lambda onFailure, final Context context) {

    }
}
