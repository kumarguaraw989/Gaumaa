package com.ecom.agrisewa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {

    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("amount_due")
    @Expose
    private Integer amountDue;
    @SerializedName("amount_paid")
    @Expose
    private Integer amountPaid;
    @SerializedName("attempts")
    @Expose
    private Integer attempts;
    @SerializedName("created_at")
    @Expose
    private Integer createdAt;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("entity")
    @Expose
    private String entity;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("notes")
    @Expose
    private List<Object> notes;
    @SerializedName("offer_id")
    @Expose
    private String offerId;
    @SerializedName("offers")
    @Expose
    private List<String> offers;
    @SerializedName("receipt")
    @Expose
    private Object receipt;
    @SerializedName("status")
    @Expose
    private String status;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(Integer amountDue) {
        this.amountDue = amountDue;
    }

    public Integer getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Integer amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Object> getNotes() {
        return notes;
    }

    public void setNotes(List<Object> notes) {
        this.notes = notes;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public List<String> getOffers() {
        return offers;
    }

    public void setOffers(List<String> offers) {
        this.offers = offers;
    }

    public Object getReceipt() {
        return receipt;
    }

    public void setReceipt(Object receipt) {
        this.receipt = receipt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
