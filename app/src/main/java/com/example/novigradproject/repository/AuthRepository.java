package com.example.novigradproject.repository;

import android.content.Context;

import com.example.novigradproject.utils.Lambda;

public interface AuthRepository {
    void signUpUser(String userName, String password, Lambda onSuccess, Lambda onFailure, Context context);

    void logOutUser(Lambda onSuccess, Lambda onFailure);

    void loginUser(String userName, String password, Lambda onSuccess, Lambda onFailure, Context context);
}
