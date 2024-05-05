package com.example.budgetplanner.util;


import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;

public class Util {

    public static boolean checkEmail(String email) {
        return !(email == null || TextUtils.isEmpty(email)) && Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    public static void showSnackBar(@NonNull View view, String snackText) {
        Snackbar snackbar = Snackbar.make(view, snackText, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
