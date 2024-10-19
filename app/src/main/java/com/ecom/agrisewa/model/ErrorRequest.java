package com.ecom.agrisewa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorRequest {

    @SerializedName("error")
    @Expose
    private PaymentError error;

    public PaymentError getError() {
        return error;
    }

    public void setError(PaymentError error) {
        this.error = error;
    }

}
