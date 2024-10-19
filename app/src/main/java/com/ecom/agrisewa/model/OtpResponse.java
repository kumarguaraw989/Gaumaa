package com.ecom.agrisewa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtpResponse {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("vp")
    @Expose
    private String vp;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("salt")
    @Expose
    private String salt;
    @SerializedName("otp")
    @Expose
    private String otp;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("firebase_user_id")
    @Expose
    private Object firebaseUserId;
    @SerializedName("photo")
    @Expose
    private Object photo;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("updated_on")
    @Expose
    private String updatedOn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVp() {
        return vp;
    }

    public void setVp(String vp) {
        this.vp = vp;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Object getFirebaseUserId() {
        return firebaseUserId;
    }

    public void setFirebaseUserId(Object firebaseUserId) {
        this.firebaseUserId = firebaseUserId;
    }

    public Object getPhoto() {
        return photo;
    }

    public void setPhoto(Object photo) {
        this.photo = photo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

}
