package com.ecom.agrisewa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyOrderItem {

    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("attributes")
    @Expose
    private String attributes;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("package")
    @Expose
    private String _package;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("amount")
    @Expose
    private String amount;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPackage() {
        return _package;
    }

    public void setPackage(String _package) {
        this._package = _package;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}
