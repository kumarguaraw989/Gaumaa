package com.ecom.agrisewa.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {

    @SuppressLint("StaticFieldLeak")
    private static LocalStorage localInstance;
    private final Context context;
    public static final String SHARED_PREF_NAME = "vyapargrowkaro";
    public static final String loginModel = "login_model";

    public LocalStorage(Context context) {
        this.context = context;
    }

    public static LocalStorage getInstance(Context context) {
        if (localInstance == null) {
            localInstance = new LocalStorage(context);
        }
        return localInstance;
    }

    public void logout() {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }

    public void saveLoginModel(String loginData) {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(loginModel, loginData);
        editor.apply();
    }

    public String getLoginModel() {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return settings.getString(loginModel, "");
    }

}
