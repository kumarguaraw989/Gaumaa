package com.ecom.agrisewa.views.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ecom.agrisewa.R;
import com.ecom.agrisewa.api.ApiClient;
import com.ecom.agrisewa.api.ServiceApi;
import com.ecom.agrisewa.model.LoginRequest;
import com.ecom.agrisewa.utils.LocalStorage;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edtNumber, edtPassword;
    ProgressBar loginProgress;
    boolean isAllFieldsChecked = false;
    LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        localStorage = LocalStorage.getInstance(LoginActivity.this);
        TextView txtSignUp = findViewById(R.id.txtSignUp);
        loginProgress = findViewById(R.id.loginProgress);
        edtNumber = findViewById(R.id.edtNumber);
        edtPassword = findViewById(R.id.edtPassword);
        MaterialButton btnSignIn = findViewById(R.id.btnSignIn);

        txtSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HardwareIds")
            @Override
            public void onClick(View v) {
                isAllFieldsChecked = checkAllFields();
                if (isAllFieldsChecked) {
                    doLogin(edtNumber.getText().toString().trim()
                            , edtPassword.getText().toString().trim()
                            , Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
                            , android.os.Build.MODEL
                            , "regid");
                }
            }
        });

    }

    public boolean checkAllFields() {
        if (edtNumber.length() == 0) {
            edtNumber.setError("Please enter number");
            edtNumber.requestFocus();
            return false;
        }
        if (edtNumber.length() < 10) {
            edtNumber.setError("Please enter valid number");
            edtNumber.requestFocus();
            return false;
        }
        if (edtPassword.length() == 0) {
            edtPassword.setError("Please enter password");
            edtPassword.requestFocus();
            return false;
        }
        return true;
    }

    public void doLogin(String mobile, String password, String deviceId, String deviceName, String regId) {
        loginProgress.setVisibility(View.VISIBLE);
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<LoginRequest> call = api.doLogin(mobile, password, deviceId, deviceName, regId);
        call.enqueue(new Callback<LoginRequest>() {
            @Override
            public void onResponse(Call<LoginRequest> call, Response<LoginRequest> response) {
                loginProgress.setVisibility(View.GONE);
                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        Toast.makeText(LoginActivity.this, "Welcome Back " + response.body().getResponse().getName(), Toast.LENGTH_SHORT).show();
                        localStorage.saveLoginModel(new Gson().toJson(response.body().getResponse()));
                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        finishAffinity();
                    } else {
                        Toast.makeText(LoginActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginRequest> call, Throwable t) {
                loginProgress.setVisibility(View.GONE);
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

}