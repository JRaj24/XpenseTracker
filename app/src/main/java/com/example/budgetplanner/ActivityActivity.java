package com.example.budgetplanner;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetplanner.util.Constants;
import com.example.budgetplanner.util.Util;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressBar progressBar;
    private Context context;
    private TextView tv_user_name, tv_user_email;
    private Button btn_sign_out, btn_change_email, btn_change_password, btn_remove_user;
    private FirebaseUser currentUser;
    private Dialog dialog;

    ImageButton backButton;

    public int getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(int authProvider) {
        this.authProvider = authProvider;
    }

    private int authProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);

        init();
        setClickListeners();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        backButton = findViewById(R.id.backButton);

        setUserData();

        getAuthenticationProvider();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityActivity.this,DashboardActivity.class);
                startActivity(intent);
            }
        });


    }

    private void getAuthenticationProvider() {
        List<? extends UserInfo> providerData = null;
        if (currentUser != null) {
            providerData = currentUser.getProviderData();
        }
        if (providerData != null) {
            for (UserInfo userInfo : providerData) {
                switch (userInfo.getProviderId()) {
                    case Constants.GOOGLE_PROVIDER:
                        Log.e(TAG, "User is signed in with Google");
                        disableAccountSettings();
                        setAuthProvider(2);
                        break;
                    case Constants.PASSWORD_PROVIDER:
                        Log.e(TAG, "User is signed in with Password");
                        setAuthProvider(1);
                        break;
                }

            }
        }
    }

    private void disableAccountSettings() {
        btn_change_email.setEnabled(false);
        btn_change_email.setAlpha(0.3f);

        btn_change_password.setEnabled(false);
        btn_change_password.setAlpha(0.3f);


    }

    private void setUserData() {
        String userEmail = getIntent().getStringExtra(Constants.KEY_USER_EMAIL);
        String userName = getIntent().getStringExtra(Constants.KEY_USER_DISPLAY_NAME);
        tv_user_name.setText(userName);
        tv_user_email.setText(userEmail);
    }

    private void setClickListeners() {
        btn_sign_out.setOnClickListener(this);
        btn_change_email.setOnClickListener(this);
        btn_change_password.setOnClickListener(this);
        btn_remove_user.setOnClickListener(this);

    }

    private void signOut() {
        progressBar.setVisibility(View.VISIBLE);

        int authProvider = getAuthProvider();
        if (authProvider == 1) {          // password
            FirebaseAuth.getInstance().signOut();
        } else if (authProvider == 2) {     // google
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(context, "Log out successfully", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(context, "Log out failed :" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        startActivity(new Intent(context, LoginActivity.class));
        finish();
    }


    private void init() {
        context = this;
        progressBar = findViewById(R.id.progressBar);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_user_email = findViewById(R.id.tv_user_email);
        btn_sign_out = findViewById(R.id.btn_sign_out);
        btn_change_email = findViewById(R.id.btn_change_email);
        btn_change_password = findViewById(R.id.btn_change_password);
        btn_remove_user = findViewById(R.id.btn_remove_user);


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_out:
                signOut();
                break;
            case R.id.btn_change_email:
                showDialog(true);
                break;
            case R.id.btn_change_password:
                showDialog(false);
                break;
            case R.id.btn_remove_user:
                removeUser();
                break;

        }
    }

    // to delete user from firebase account
    private void removeUser() {
        progressBar.setVisibility(View.VISIBLE);
        if (currentUser == null)
            return;
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(context, "profile Deleted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, LoginActivity.class));
                    finish();
                } else
//                    Toast.makeText(context, getString(R.string.failed_delete_account), Toast.LENGTH_SHORT).show();
                    Util.showSnackBar(btn_remove_user, "Failed to delete account");

            }
        });
    }

    // for change email
    private void changeEmail(EditText et_new_email) {

        String newEmail = et_new_email.getText().toString().trim();
        if (TextUtils.isEmpty(newEmail)) {
            Toast.makeText(context, "Enter new Email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Util.checkEmail(newEmail)) {
            Toast.makeText(context, getString(R.string.valid_email), Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentUser == null)
            return;
        progressBar.setVisibility(View.VISIBLE);
        currentUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                dialog.dismiss();
                if (task.isSuccessful()) {
                    signOut();
//                    Util.showSnackBar(et_new_email,getString(R.string.success_updated_email));
                    Toast.makeText(context,"Successfully update Email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to update Email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changePassword(EditText et_new_password) {
        String newPassword = et_new_password.getText().toString().trim();
        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(context, "Enter New password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPassword.length() < 8) {
            Toast.makeText(context, "Password length should be more than 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentUser == null)
            return;
        progressBar.setVisibility(View.VISIBLE);
        currentUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    signOut();
                    Toast.makeText(context, "Successfully updated password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Password update failed", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void showDialog(boolean isEmailDialog) {
        dialog = new Dialog(context);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.common_dialog);
        EditText et_new_email = dialog.findViewById(R.id.et_new_email);
        EditText et_new_password = dialog.findViewById(R.id.et_new_password);

        if (isEmailDialog)
            et_new_password.setVisibility(View.GONE);
        else et_new_email.setVisibility(View.GONE);


        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btn_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmailDialog)
                    changeEmail(et_new_email);
                else changePassword(et_new_password);
            }
        });
        dialog.show();

    }


}

