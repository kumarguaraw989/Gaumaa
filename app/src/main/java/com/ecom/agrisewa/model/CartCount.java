package com.ecom.agrisewa.model;

import com.google.gson.annotations.SerializedName;


public class CartCount {

    @SerializedName("status")
    boolean status;

    @SerializedName("count")
    int count;


    public void setStatus(boolean status) {
        this.status = status;
    }
    public boolean getStatus() {
        return status;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public int getCount() {
        return count;
    }

}
