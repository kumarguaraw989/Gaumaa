package com.ecom.agrisewa.api;

import static com.ecom.agrisewa.utils.Constant.BASE_URL;
import static com.ecom.agrisewa.utils.Constant.PAYMENT_URL;

import android.util.Base64;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static Retrofit paymentRetrofit = null;
    private static final String AUTH = "Basic " + Base64.encodeToString(("rzp_live_IMlBmajNHiDgl3:6H7MOssAi8ZmhKYLkEQMZGdi").getBytes(), Base64.NO_WRAP);

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getPaymentClient() {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original
                        .newBuilder()
                        .addHeader("Authorization", AUTH)
                        .method(original.method(), original.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        }).build();

        if(paymentRetrofit==null){
            paymentRetrofit = new Retrofit.Builder()
                    .baseUrl(PAYMENT_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return paymentRetrofit;
    }

}

