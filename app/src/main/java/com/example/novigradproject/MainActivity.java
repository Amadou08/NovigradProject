package com.example.novigradproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.novigradproject.databinding.ActivityMainBinding;
import com.example.novigradproject.model.user.Person;
import com.example.novigradproject.runtime.GlobalData;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = GlobalData.user;
        setupUI();
    }

    private void setupUI() {
        String welcomeMsg = "Welcome " + user.extractFirstName() + "! You are logged in as a " + user.role;
        binding.welcomeTv.setText(welcomeMsg);
    }
}