package com.ecom.agrisewa.views.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ecom.agrisewa.R;
import com.ecom.agrisewa.utils.GpsLocation;
import com.ecom.agrisewa.utils.LocalStorage;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        localStorage = LocalStorage.getInstance(MainActivity.this);
        askPermission();
    }

    public void askPermission() {
        Log.e("CITY", "check1" + "");

        Dexter.withContext(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        Log.e("CITY", "check2" + "");
                        if (report.areAllPermissionsGranted()) {
                            Log.e("CITY", "check3" + "");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("CITY", "check4" + "");
                                    String location = GpsLocation.getGPSLocation(MainActivity.this);
                                    if (!location.isEmpty()) {
                                        Log.e("CITY", "check5" + "");
                                        String[] address = location.split("-");
                                        Log.e("CITY", address[0] + "");
                                        Log.e("LOCATION", address[1] + "");
                                        if (localStorage.getLoginModel().isEmpty()) {
                                            Log.e("CITY", "check6" + "");
                                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                            finishAffinity();
                                        } else {
                                            Log.e("CITY", "check7" + "");
                                            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                                            finishAffinity();
                                        }
                                    }
                                }
                            }, 3000);
                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("Open Setting", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });
        builder.show();
    }

}