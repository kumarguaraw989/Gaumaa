package com.ecom.agrisewa.views.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ecom.agrisewa.R;
import com.ecom.agrisewa.api.ApiClient;
import com.ecom.agrisewa.api.ServiceApi;
import com.ecom.agrisewa.model.DistrictRequest;
import com.ecom.agrisewa.model.DistrictResponse;
import com.ecom.agrisewa.model.LoginResponse;
import com.ecom.agrisewa.model.SaveAddress;
import com.ecom.agrisewa.model.StateRequest;
import com.ecom.agrisewa.model.StateResponse;
import com.ecom.agrisewa.utils.LocalStorage;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity {

    LoginResponse loginResponse;
    Spinner stateSpinner, districtSpinner;
    List<StateResponse> stateResponseList = new ArrayList<>();
    List<DistrictResponse> districtResponseList = new ArrayList<>();
    List<String> stateNames = new ArrayList<>();
    List<String> districtNames = new ArrayList<>();
    ArrayAdapter<String> stateAdapter, districtAdapter;
    String stateId, districtId;
    CheckBox checkOther, checkHome, checkOffice;
    EditText edtName, edtNumber, edtAddress, edtLandMark, edtPincode;
    boolean isAllFieldsChecked = false;
    MaterialButton btnSaveAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        LocalStorage localStorage = LocalStorage.getInstance(AddAddressActivity.this);
        loginResponse = new Gson().fromJson(localStorage.getLoginModel(), LoginResponse.class);
        MaterialToolbar toolBar = findViewById(R.id.toolBar);
        stateSpinner = findViewById(R.id.stateSpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        checkOther = findViewById(R.id.checkOther);
        checkHome = findViewById(R.id.checkHome);
        checkOffice = findViewById(R.id.checkOffice);
        edtName = findViewById(R.id.edtName);
        edtNumber = findViewById(R.id.edtNumber);
        edtAddress = findViewById(R.id.edtAddress);
        edtLandMark = findViewById(R.id.edtLandMark);
        edtPincode = findViewById(R.id.edtPincode);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateId = stateResponseList.get(position).getId();
                getDistrict(stateId);
                Log.e("STATE ID", stateId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                districtId = districtResponseList.get(position).getId();
                Log.e("DISTRICT ID", districtId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAllFieldsChecked = checkAllFields();
                if (isAllFieldsChecked) {
                    saveAddress(loginResponse.getToken()
                            , checkHome.isChecked() ? "Home" : checkOffice.isChecked() ? "Office" : "Others"
                            , edtName.getText().toString()
                            , edtNumber.getText().toString()
                            , edtAddress.getText().toString()
                            , stateId
                            , districtId
                            , edtLandMark.getText().toString()
                            , edtPincode.getText().toString());
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getState();
    }

    private boolean checkAllFields() {
        if (!checkOther.isChecked() && !checkHome.isChecked() && !checkOffice.isChecked()) {
            Toast.makeText(AddAddressActivity.this, "Please select address type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtName.length() == 0) {
            edtName.setError("Please enter name");
            edtName.requestFocus();
            return false;
        }
        if (edtNumber.length() == 0) {
            edtNumber.setError("Please enter mobile number");
            edtNumber.requestFocus();
            return false;
        }
        if (edtNumber.length() < 10) {
            edtNumber.setError("Please enter valid mobile number");
            edtNumber.requestFocus();
            return false;
        }
        if (stateId == null) {
            Toast.makeText(AddAddressActivity.this, "Please select state", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (districtId == null) {
            Toast.makeText(AddAddressActivity.this, "Please select district", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtAddress.length() == 0) {
            edtAddress.setError("Please enter address");
            edtAddress.requestFocus();
            return false;
        }
        if (edtLandMark.length() == 0) {
            edtLandMark.setError("Please enter landmark");
            edtLandMark.requestFocus();
            return false;
        }
        if (edtPincode.length() == 0) {
            edtPincode.setError("Please enter pincode");
            edtPincode.requestFocus();
            return false;
        }
        if (edtPincode.length() != 6) {
            edtPincode.setError("Please enter valid pincode");
            edtPincode.requestFocus();
            return false;
        }
        return true;
    }

    public void getState() {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<StateRequest> call = api.getState();
        call.enqueue(new Callback<StateRequest>() {
            @Override
            public void onResponse(Call<StateRequest> call, Response<StateRequest> response) {
                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        stateResponseList = response.body().getStates();
                        for (StateResponse stateResponse : stateResponseList) {
                            stateNames.add(stateResponse.getName());
                            stateAdapter = new ArrayAdapter<>(AddAddressActivity.this
                                    , R.layout.custom_spinner_item_layout
                                    , R.id.txtSpinnerItem
                                    , stateNames);
                            stateSpinner.setAdapter(stateAdapter);
                        }
                    } else {
                        Toast.makeText(AddAddressActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<StateRequest> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

    public void getDistrict(String stateId) {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<DistrictRequest> call = api.getDistrict(stateId);
        call.enqueue(new Callback<DistrictRequest>() {
            @Override
            public void onResponse(Call<DistrictRequest> call, Response<DistrictRequest> response) {
                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        districtResponseList.clear();
                        districtNames.clear();
                        districtResponseList = response.body().getDistricts();
                        for (DistrictResponse districtResponse : districtResponseList) {
                            districtNames.add(districtResponse.getName());
                            districtAdapter = new ArrayAdapter<>(AddAddressActivity.this
                                    , R.layout.custom_spinner_item_layout
                                    , R.id.txtSpinnerItem
                                    , districtNames);
                            districtSpinner.setAdapter(districtAdapter);
                        }
                        districtAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AddAddressActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DistrictRequest> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

    public void saveAddress(String token, String type, String name, String mobile, String address, String state_id, String district_id, String landmark, String pincode) {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<SaveAddress> call = api.saveAddress(token, type, name, mobile, address, state_id, district_id, landmark, pincode);
        call.enqueue(new Callback<SaveAddress>() {
            @Override
            public void onResponse(Call<SaveAddress> call, Response<SaveAddress> response) {
                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        Toast.makeText(AddAddressActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(AddAddressActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SaveAddress> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

}