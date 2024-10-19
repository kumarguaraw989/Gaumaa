package com.ecom.agrisewa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartRequest {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("cart")
    @Expose
    private List<CartResponse> cart;
    @SerializedName("amount")
    @Expose
    private CartAmount amount;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CartResponse> getCart() {
        return cart;
    }

    public void setCart(List<CartResponse> cart) {
        this.cart = cart;
    }

    public CartAmount getAmount() {
        return amount;
    }

    public void setAmount(CartAmount amount) {
        this.amount = amount;
    }

}
