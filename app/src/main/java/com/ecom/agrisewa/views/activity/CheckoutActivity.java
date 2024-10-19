package com.ecom.agrisewa.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecom.agrisewa.R;
import com.ecom.agrisewa.adapter.AddressAdapter;
import com.ecom.agrisewa.api.ApiClient;
import com.ecom.agrisewa.api.ServiceApi;
import com.ecom.agrisewa.handler.AddressHandler;
import com.ecom.agrisewa.model.AddressRequest;
import com.ecom.agrisewa.model.AddressResponse;
import com.ecom.agrisewa.model.CartAmount;
import com.ecom.agrisewa.model.LoginResponse;
import com.ecom.agrisewa.model.PlaceOrderResponse;
import com.ecom.agrisewa.utils.LocalStorage;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity implements AddressHandler {

    RecyclerView addressRecycler;
    LoginResponse loginResponse;
    AddressAdapter addressAdapter;
    List<AddressResponse> addressResponseList = new ArrayList<>();
    String addressId;
    CartAmount cartAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        LocalStorage localStorage = LocalStorage.getInstance(CheckoutActivity.this);
        loginResponse = new Gson().fromJson(localStorage.getLoginModel(), LoginResponse.class);
        MaterialToolbar toolBar = findViewById(R.id.toolBar);
        MaterialButton btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        TextView txtAddAddress = findViewById(R.id.txtAddAddress);
        TextView txtAmount = findViewById(R.id.txtAmount);
        addressRecycler = findViewById(R.id.addressRecycler);

        cartAmount = (CartAmount) getIntent().getSerializableExtra("cartAmount");
        if (cartAmount != null) {
            txtAmount.setText("₹ " + cartAmount.getTotalAmount());
        }

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txtAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CheckoutActivity.this, AddAddressActivity.class));
            }
        });

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addressId != null) {
                    Intent intent = new Intent(CheckoutActivity.this, PaymentActivity.class);
                    intent.putExtra("cartAmount", cartAmount.getTotalAmount());
                    intent.putExtra("addressId", addressId);
                    intent.putExtra("payMode", "Online");
                    startActivity(intent);
                } else {
                    Toast.makeText(CheckoutActivity.this, "Please select address", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getAddress(loginResponse.getToken());
    }

    public void getAddress(String token) {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<AddressRequest> call = api.getAddress(token);
        call.enqueue(new Callback<AddressRequest>() {
            @Override
            public void onResponse(Call<AddressRequest> call, Response<AddressRequest> response) {
                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        addressResponseList.clear();
                        addressRecycler.setLayoutManager(new LinearLayoutManager(CheckoutActivity.this, LinearLayoutManager.VERTICAL, false));
                        addressAdapter = new AddressAdapter(CheckoutActivity.this, addressResponseList, CheckoutActivity.this);
                        addressRecycler.setAdapter(addressAdapter);
                        addressResponseList.addAll(response.body().getAddresses());
                        addressAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CheckoutActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AddressRequest> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onAddressClick(AddressResponse addressResponse) {
        addressId = addressResponse.getId();
    }
}