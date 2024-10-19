package com.ecom.agrisewa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BannerRequest {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("bannerimages")
    @Expose
    private List<BannerResponse> bannerimages;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<BannerResponse> getBannerimages() {
        return bannerimages;
    }

    public void setBannerimages(List<BannerResponse> bannerimages) {
        this.bannerimages = bannerimages;
    }

}
