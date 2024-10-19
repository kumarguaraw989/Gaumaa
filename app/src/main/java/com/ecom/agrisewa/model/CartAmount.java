package com.ecom.agrisewa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CartAmount implements Serializable {

    @SerializedName("cart_amount")
    @Expose
    private Integer cartAmount;
    @SerializedName("delivery_charge")
    @Expose
    private Integer deliveryCharge;
    @SerializedName("total_amount")
    @Expose
    private Integer totalAmount;

    public Integer getCartAmount() {
        return cartAmount;
    }

    public void setCartAmount(Integer cartAmount) {
        this.cartAmount = cartAmount;
    }

    public Integer getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Integer deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

}
