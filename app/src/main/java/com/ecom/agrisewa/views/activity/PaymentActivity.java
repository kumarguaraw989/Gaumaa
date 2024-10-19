package com.ecom.agrisewa.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ecom.agrisewa.R;
import com.ecom.agrisewa.api.ApiClient;
import com.ecom.agrisewa.api.ServiceApi;
import com.ecom.agrisewa.model.ErrorRequest;
import com.ecom.agrisewa.model.LoginResponse;
import com.ecom.agrisewa.model.Order;
import com.ecom.agrisewa.model.OrderRequest;
import com.ecom.agrisewa.model.PlaceOrderResponse;
import com.ecom.agrisewa.utils.LocalStorage;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {

    Checkout checkout;
    LoginResponse loginResponse;
    String addressId, payMode;
    int cartAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        LocalStorage localStorage = LocalStorage.getInstance(PaymentActivity.this);
        loginResponse = new Gson().fromJson(localStorage.getLoginModel(), LoginResponse.class);
        checkout = new Checkout();
        checkout.setKeyID("rzp_live_IMlBmajNHiDgl3");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            cartAmount = bundle.getInt("cartAmount");
            addressId = bundle.getString("addressId");
            payMode = bundle.getString("payMode");
            createOrder(String.valueOf(cartAmount), "order_now");
//            makePayment(cartAmount * 100, "orderId", "orderNo");
        }
    }

    public void createOrder(String amount, String orderNo) {
        ServiceApi api = ApiClient.getPaymentClient().create(ServiceApi.class);
        int payAmount = Math.round(Float.parseFloat(amount) * 100);
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setAmount(payAmount);
        orderRequest.setCurrency("INR");
        Call<Order> call = api.createOrder(orderRequest);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.body() != null) {
                    String orderId = response.body().getId();
                    Log.e("ORDER ID", orderId + "");
                    makePayment(payAmount, orderId, orderNo);
                } else {
                    Log.e("BODY", "Body is null");
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

    public void makePayment(int payAmount, String orderId, String orderNo) {
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Order_" + orderNo);
            options.put("description", "Payment for order id: " + orderId);
            options.put("currency", "INR");
            options.put("amount", payAmount);
            options.put("order_id", "" + orderId);
            options.put("prefill.email", loginResponse.getEmail() + "");
            options.put("prefill.contact", loginResponse.getMobile() + "");
            checkout.open(this, options);
        } catch (Exception e) {
            Log.e("Razorpay", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        placeOrder(loginResponse.getToken()
                , addressId
                , payMode);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.e("Payment Error", s);
        ErrorRequest errorRequest = new Gson().fromJson(s, ErrorRequest.class);
        Toast.makeText(this, "" + errorRequest.getError().getDescription(), Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    public void placeOrder(String token, String addressId, String payMode) {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<PlaceOrderResponse> call = api.placeOrder(token, addressId, payMode);
        call.enqueue(new Callback<PlaceOrderResponse>() {
            @Override
            public void onResponse(Call<PlaceOrderResponse> call, Response<PlaceOrderResponse> response) {
                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        Toast.makeText(PaymentActivity.this, "" + response.body().getOrders(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PaymentActivity.this, DashboardActivity.class));
                        finishAffinity();
                    } else {
                        Toast.makeText(PaymentActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaceOrderResponse> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

}