package com.ecom.agrisewa.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chaos.view.PinView;
import com.ecom.agrisewa.R;
import com.ecom.agrisewa.api.ApiClient;
import com.ecom.agrisewa.api.ServiceApi;
import com.ecom.agrisewa.model.LoginResponse;
import com.ecom.agrisewa.model.OtpRequest;
import com.ecom.agrisewa.model.OtpResponse;
import com.ecom.agrisewa.utils.LocalStorage;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {

    String number, token, otp;
    PinView otpView;
    ProgressBar otpProgress;
    LocalStorage localStorage;
    boolean isAllFieldsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        localStorage = LocalStorage.getInstance(OtpActivity.this);
        otpView = findViewById(R.id.otpView);
        otpProgress = findViewById(R.id.otpProgress);
        MaterialToolbar toolBar = findViewById(R.id.toolBar);
        MaterialButton btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            number = bundle.getString("number");
            token = bundle.getString("token");
            otp = bundle.getString("otp");
            toolBar.setSubtitle("Otp sent to your number +91" + number);
            otpView.setText(otp);
        }

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAllFieldsChecked = checkALlFields();
                if (isAllFieldsChecked) {
                    verifyOtp(otp, token);
                }
            }
        });
    }

    public boolean checkALlFields() {
        if (otpView.length() == 0) {
            Toast.makeText(this, "Please enter otp", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (otpView.length() < 6) {
            Toast.makeText(this, "Please enter valid otp", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void verifyOtp(String otp, String token) {
        otpProgress.setVisibility(View.VISIBLE);
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<OtpRequest> call = api.verifyOtp(token, otp);
        call.enqueue(new Callback<OtpRequest>() {
            @Override
            public void onResponse(Call<OtpRequest> call, Response<OtpRequest> response) {
                otpProgress.setVisibility(View.GONE);
                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        Toast.makeText(OtpActivity.this, "OTP Verified Successfully...!!", Toast.LENGTH_SHORT).show();
                        OtpResponse otpResponse = response.body().getResult();
                        LoginResponse loginResponse = new LoginResponse();
                        loginResponse.setName(otpResponse.getName());
                        loginResponse.setMobile(otpResponse.getMobile());
                        loginResponse.setEmail(otpResponse.getEmail());
                        loginResponse.setToken(otpResponse.getToken());
                        localStorage.saveLoginModel(new Gson().toJson(loginResponse));
                        startActivity(new Intent(OtpActivity.this, DashboardActivity.class));
                        finishAffinity();
                    } else {
                        Toast.makeText(OtpActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<OtpRequest> call, Throwable t) {
                otpProgress.setVisibility(View.GONE);
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

}