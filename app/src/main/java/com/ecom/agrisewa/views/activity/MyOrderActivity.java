package com.ecom.agrisewa.views.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecom.agrisewa.R;
import com.ecom.agrisewa.adapter.MyOrderAdapter;
import com.ecom.agrisewa.api.ApiClient;
import com.ecom.agrisewa.api.ServiceApi;
import com.ecom.agrisewa.model.LoginResponse;
import com.ecom.agrisewa.model.MyOrderRequest;
import com.ecom.agrisewa.model.MyOrderResponse;
import com.ecom.agrisewa.utils.LocalStorage;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrderActivity extends AppCompatActivity {

    List<MyOrderResponse> myOrderResponseList = new ArrayList<>();
    RecyclerView orderRecycler;
    MyOrderAdapter myOrderAdapter;
    LoginResponse loginResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        LocalStorage localStorage = LocalStorage.getInstance(MyOrderActivity.this);
        loginResponse = new Gson().fromJson(localStorage.getLoginModel(), LoginResponse.class);
        orderRecycler = findViewById(R.id.orderRecycler);
        MaterialToolbar toolBar = findViewById(R.id.toolBar);

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyOrder(loginResponse.getToken());
    }

    public void getMyOrder(String token) {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<MyOrderRequest> call = api.getMyOrder(token);
        call.enqueue(new Callback<MyOrderRequest>() {
            @Override
            public void onResponse(Call<MyOrderRequest> call, Response<MyOrderRequest> response) {
                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        myOrderResponseList.clear();
                        orderRecycler.setLayoutManager(new LinearLayoutManager(MyOrderActivity.this, LinearLayoutManager.VERTICAL, false));
                        myOrderAdapter = new MyOrderAdapter(MyOrderActivity.this, myOrderResponseList);
                        orderRecycler.setAdapter(myOrderAdapter);
                        myOrderResponseList.addAll(response.body().getOrders());
                        myOrderAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MyOrderActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyOrderRequest> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }
}