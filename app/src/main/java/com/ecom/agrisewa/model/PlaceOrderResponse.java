package com.ecom.agrisewa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceOrderResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("orders")
    @Expose
    private String orders;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("order_id")
    @Expose

    private Integer orderId;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
