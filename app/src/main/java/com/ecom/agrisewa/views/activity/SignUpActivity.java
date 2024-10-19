package com.ecom.agrisewa.views.activity;

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
import com.ecom.agrisewa.model.RegisterRequest;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    EditText edtName, edtMobile, edtEmail, edtPassword;
    boolean isAllFieldsChecked = false;
    ProgressBar signUpProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        TextView txtLogin = findViewById(R.id.txtLogin);
        MaterialButton btnSignUp = findViewById(R.id.btnSignUp);
        edtName = findViewById(R.id.edtName);
        edtMobile = findViewById(R.id.edtMobile);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        signUpProgress = findViewById(R.id.signUpProgress);

        txtLogin.setOnClickListener(v -> {
            onBackPressed();
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAllFieldsChecked = checkAllFields();
                if (isAllFieldsChecked) {
                    doSignUp(edtName.getText().toString().trim()
                            , edtMobile.getText().toString().trim()
                            , edtEmail.getText().toString().trim()
                            , edtPassword.getText().toString().trim());
                }
            }
        });

    }

    public boolean checkAllFields() {
        if (edtName.length() == 0) {
            edtName.setError("Please enter name");
            edtName.requestFocus();
            return false;
        }
        if (edtMobile.length() == 0) {
            edtMobile.setError("Please enter mobile");
            edtMobile.requestFocus();
            return false;
        }
        if (edtMobile.length() < 10) {
            edtMobile.setError("Please enter valid mobile");
            edtMobile.requestFocus();
            return false;
        }
        if (edtEmail.length() == 0) {
            edtEmail.setError("Please enter email");
            edtEmail.requestFocus();
            return false;
        }
        if (edtPassword.length() == 0) {
            edtPassword.setError("Please enter password");
            edtPassword.requestFocus();
            return false;
        }
        return true;
    }

    public void doSignUp(String name, String mobile, String email, String password) {
        signUpProgress.setVisibility(View.VISIBLE);
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<RegisterRequest> call = api.doRegister(name
                , mobile
                , email
                , password
                , Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
                , android.os.Build.MODEL
                , "regid");
        call.enqueue(new Callback<RegisterRequest>() {
            @Override
            public void onResponse(Call<RegisterRequest> call, Response<RegisterRequest> response) {
                signUpProgress.setVisibility(View.GONE);
                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        Toast.makeText(SignUpActivity.this, "Register Successfully...!!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, OtpActivity.class);
                        intent.putExtra("number", response.body().getResponse().getMobile());
                        intent.putExtra("token", response.body().getResponse().getToken());
                        intent.putExtra("otp", response.body().getResponse().getOtp());
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignUpActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterRequest> call, Throwable t) {
                signUpProgress.setVisibility(View.GONE);
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

}