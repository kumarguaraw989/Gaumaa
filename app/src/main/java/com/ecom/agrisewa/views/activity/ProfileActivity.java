package com.ecom.agrisewa.views.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.ecom.agrisewa.R;
import com.ecom.agrisewa.api.ApiClient;
import com.ecom.agrisewa.api.ServiceApi;
import com.ecom.agrisewa.model.LoginResponse;
import com.ecom.agrisewa.model.ProfileRequest;
import com.ecom.agrisewa.model.ProfileResponse;
import com.ecom.agrisewa.model.UpdateProfileRequest;
import com.ecom.agrisewa.model.UpdateProfileResponse;
import com.ecom.agrisewa.utils.FileUtils;
import com.ecom.agrisewa.utils.LocalStorage;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.io.File;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    LoginResponse loginResponse;
    EditText edtName, edtEmail, edtMobile;
    CircleImageView imgProfile;
    byte[] bytes;
    ProgressBar profileProgress;
    LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        localStorage = LocalStorage.getInstance(ProfileActivity.this);
        loginResponse = new Gson().fromJson(localStorage.getLoginModel(), LoginResponse.class);
        MaterialButton btnUpdate = findViewById(R.id.btnUpdate);
        TextView txtLogout = findViewById(R.id.txtLogout);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtMobile = findViewById(R.id.edtMobile);
        imgProfile = findViewById(R.id.imgProfile);
        profileProgress = findViewById(R.id.profileProgress);
        getProfile(loginResponse.getToken());

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhotoFromGallery();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(bytes);
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(ProfileActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout this session?")
                        .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                localStorage.logout();
                                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                                finishAffinity();
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });

    }

    public void selectPhotoFromGallery() {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R && android.os.ext.SdkExtensions.getExtensionVersion(android.os.Build.VERSION_CODES.R) >= 2) {
            intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                if (data != null && data.getData() != null) {
                    imgProfile.setImageURI(data.getData());
                    Uri uri = data.getData();
                    bytes = FileUtils.getBytes(uri, ProfileActivity.this);
                    if (bytes != null) {
                        System.out.println("PROFILE BYTES:" + bytes);
                    }
                }
            }
        }
    }

    public void getProfile(String token) {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<ProfileRequest> call = api.getProfile(token);
        call.enqueue(new Callback<ProfileRequest>() {
            @Override
            public void onResponse(Call<ProfileRequest> call, Response<ProfileRequest> response) {
                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        ProfileResponse profileResponse = response.body().getProfile();
                        if (profileResponse != null) {
                            edtName.setText(profileResponse.getName());
                            edtEmail.setText(profileResponse.getEmail());
                            edtMobile.setText(profileResponse.getMobile());
                            Glide.with(ProfileActivity.this).load(profileResponse.getPhoto()).error(R.drawable.logo).into(imgProfile);
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileRequest> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

    public void updateProfile(byte[] bytes) {
        profileProgress.setVisibility(View.VISIBLE);
        MultipartBody.Part imagePart;
        if (bytes != null) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), bytes);
            imagePart = MultipartBody.Part.createFormData("photo", "photo.png", requestBody);
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            imagePart = MultipartBody.Part.createFormData("photo", "", attachmentEmpty);
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
            Call<UpdateProfileRequest> call = api.updateProfile(RequestBody.create(MediaType.parse("multipart/form-data"), loginResponse.getToken())
                    , RequestBody.create(MediaType.parse("multipart/form-data"), edtName.getText().toString().trim())
                    , RequestBody.create(MediaType.parse("multipart/form-data"), edtEmail.getText().toString().trim())
                    , RequestBody.create(MediaType.parse("multipart/form-data"), edtMobile.getText().toString().trim())
                    , imagePart);
            call.enqueue(new Callback<UpdateProfileRequest>() {
                @Override
                public void onResponse(Call<UpdateProfileRequest> call, Response<UpdateProfileRequest> response) {
                    profileProgress.setVisibility(View.GONE);
                    if (response.body() != null) {
                        if (response.body().getStatus()) {
                            Toast.makeText(ProfileActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            UpdateProfileResponse profileResponse = response.body().getProfile();
                            if (profileResponse != null) {
                                edtName.setText(profileResponse.getName());
                                edtEmail.setText(profileResponse.getEmail());
                                edtMobile.setText(profileResponse.getMobile());
                                Glide.with(ProfileActivity.this).load(profileResponse.getPhoto()).error(R.drawable.logo).into(imgProfile);
                                LoginResponse newLoginResponse = new LoginResponse();
                                newLoginResponse.setName(profileResponse.getName());
                                newLoginResponse.setMobile(profileResponse.getMobile());
                                newLoginResponse.setEmail(profileResponse.getEmail());
                                newLoginResponse.setToken(loginResponse.getToken());
                                localStorage.saveLoginModel(new Gson().toJson(newLoginResponse));
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UpdateProfileRequest> call, Throwable t) {
                    profileProgress.setVisibility(View.GONE);
                    Log.e("EXCEPTION", t.getLocalizedMessage());
                }
            });
        });
    }

}