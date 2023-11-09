package com.example.novigradproject.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.novigradproject.MainActivity;
import com.example.novigradproject.R;
import com.example.novigradproject.databinding.ActivitySignUpBinding;
import com.example.novigradproject.databinding.ActivitySplashScreenBinding;
import com.example.novigradproject.runtime.GlobalData;
import com.example.novigradproject.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "##@@@SplashScrAct";
    private ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Handler().postDelayed(this::setupUI, 1000L);
    }

    private void setupUI() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.i(TAG, "UserId: " + user.getUid());
            SharedPreferences accountPref = getSharedPreferences(Constants.ACCOUNT_PREF, Context.MODE_PRIVATE);
            GlobalData.user.id = user.getUid();
            GlobalData.user.userName = accountPref.getString(Constants.FIELD_USERNAME, "");
            GlobalData.user.email = accountPref.getString(Constants.FIELD_EMAIL, "");
            GlobalData.user.name = accountPref.getString(Constants.FIELD_NAME, "");
            GlobalData.user.role = accountPref.getString(Constants.FIELD_ROLE, "Customer");
            GlobalData.user.firstName = accountPref.getString(Constants.FIELD_FIRST_NAME, "");
            GlobalData.user.lastName = accountPref.getString(Constants.FIELD_LAST_NAME, "");
            goToHomeScreen();
        } else {
            Log.i(TAG, "User was null. Going to LoginAct");
            goToLoginScreen();
        }
    }

    private void goToLoginScreen() {
        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goToHomeScreen() {
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}