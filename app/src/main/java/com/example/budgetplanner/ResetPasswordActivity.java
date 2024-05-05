package com.example.budgetplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.budgetplanner.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class ResetPasswordActivity extends AppCompatActivity {

    private EditText et_email;
    private Button reset_btn,back_to_login;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private LinearLayout ll_forgot_email;
    private ImageView iv_back_toolbar;
    private String TAG = ResetPasswordActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        init();
       // iv_back_toolbar.setOnClickListener(v -> onBackPressed());
        reset_btn.setOnClickListener(v -> {
            String email = et_email.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Util.showSnackBar(et_email,"Enter Registered Email");
                return;
            }
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        ll_forgot_email.setVisibility(View.GONE);
                        findViewById(R.id.tv_forgot_email_description).setVisibility(View.VISIBLE);

                    } else {
                        Util.showSnackBar(et_email,"Reset Email Failed");
                        Log.e(TAG, "Reset Email Failed" + " : " + task.getException().getLocalizedMessage());
                    }
              }
            });
      });

        back_to_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        et_email = findViewById(R.id.et_email);
        ll_forgot_email = findViewById(R.id.ll_forgot_email);
       // iv_back_toolbar = findViewById(R.id.iv_back_toolbar);
        reset_btn = findViewById(R.id.reset_btn);
        progressBar = findViewById(R.id.progressBar);
        back_to_login=findViewById(R.id.back_to_login);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
