package com.ecom.agrisewa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("profile")
    @Expose
    private UpdateProfileResponse profile;

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

    public UpdateProfileResponse getProfile() {
        return profile;
    }

    public void setProfile(UpdateProfileResponse profile) {
        this.profile = profile;
    }

}
