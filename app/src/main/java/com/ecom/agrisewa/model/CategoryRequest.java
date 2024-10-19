package com.ecom.agrisewa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryRequest {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("categories")
    @Expose
    private List<CategoryResponse> categories;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<CategoryResponse> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryResponse> categories) {
        this.categories = categories;
    }

}
