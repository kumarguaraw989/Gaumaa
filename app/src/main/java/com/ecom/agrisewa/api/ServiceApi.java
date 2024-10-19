package com.ecom.agrisewa.api;

import com.ecom.agrisewa.model.AddToCart;
import com.ecom.agrisewa.model.AddressRequest;
import com.ecom.agrisewa.model.BannerRequest;
import com.ecom.agrisewa.model.CartRequest;
import com.ecom.agrisewa.model.CategoryRequest;
import com.ecom.agrisewa.model.DeleteCart;
import com.ecom.agrisewa.model.DistrictRequest;
import com.ecom.agrisewa.model.FeaturedProductRequest;
import com.ecom.agrisewa.model.LoginRequest;
import com.ecom.agrisewa.model.MyOrderRequest;
import com.ecom.agrisewa.model.Order;
import com.ecom.agrisewa.model.OrderRequest;
import com.ecom.agrisewa.model.OtpRequest;
import com.ecom.agrisewa.model.PlaceOrderResponse;
import com.ecom.agrisewa.model.ProductImageRequest;
import com.ecom.agrisewa.model.ProductRequest;
import com.ecom.agrisewa.model.ProfileRequest;
import com.ecom.agrisewa.model.RegisterRequest;
import com.ecom.agrisewa.model.SaveAddress;
import com.ecom.agrisewa.model.StateRequest;
import com.ecom.agrisewa.model.SubCategoryRequest;
import com.ecom.agrisewa.model.UpdateProfileRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ServiceApi {

    @FormUrlEncoded
    @POST("login")
    Call<LoginRequest> doLogin(@Field("mobile") String mobile
            , @Field("password") String password
            , @Field("device_id") String device_id
            , @Field("device_name") String device_name
            , @Field("regid") String regid);

    @FormUrlEncoded
    @POST("register")
    Call<RegisterRequest> doRegister(@Field("name") String name
            , @Field("mobile") String mobile
            , @Field("email") String email
            , @Field("password") String password
            , @Field("device_id") String device_id
            , @Field("device_name") String device_name
            , @Field("regid") String regid);

    @FormUrlEncoded
    @POST("verifyotp")
    Call<OtpRequest> verifyOtp(@Field("token") String token
            , @Field("otp") String otp);

    @POST("getbannerimages")
    Call<BannerRequest> getBanner();

    @POST("getcategory")
    Call<CategoryRequest> getCategory();

    @FormUrlEncoded
    @POST("getsubcategory")
    Call<SubCategoryRequest> getSubCategory(@Field("category") String category);

    @POST("getfeaturedproducts")
    Call<FeaturedProductRequest> getFeaturedProduct();

    @FormUrlEncoded
    @POST("getproducts")
    Call<ProductRequest> getProduct(@Field("token") String token
            , @Field("query") String query
            , @Field("category") String category);

    @FormUrlEncoded
    @POST("getproductimages")
    Call<ProductImageRequest> getProductImage(@Field("product_id") String product_id);

    @FormUrlEncoded
    @POST("addtocart")
    Call<AddToCart> addToCart(@Field("device_id") String device_id,
                              @Field("token") String token,
                              @Field("product_id") String product_id,
                              @Field("quantity") String quantity,
                              @Field("package_id") String package_id);

    @FormUrlEncoded
    @POST("getcart")
    Call<CartRequest> getCart(@Field("device_id") String device_id
            , @Field("token") String token);
    @FormUrlEncoded
    @POST("cartcount")
    Call<CartRequest> getCartCount(@Field("device_id") String device_id
            , @Field("token") String token);

    @FormUrlEncoded
    @POST("deletefromcart")
    Call<DeleteCart> deleteFromCart(@Field("id") String id);

    @FormUrlEncoded
    @POST("myaddresses")
    Call<AddressRequest> getAddress(@Field("token") String token);

    @FormUrlEncoded
    @POST("placeorder")
    Call<PlaceOrderResponse> placeOrder(@Field("token") String token
            , @Field("address_id") String address_id
            , @Field("paymode") String paymode);

    @POST("getstates")
    Call<StateRequest> getState();

    @FormUrlEncoded
    @POST("getdistricts")
    Call<DistrictRequest> getDistrict(@Field("state_id") String state_id);

    @FormUrlEncoded
    @POST("saveaddress")
    Call<SaveAddress> saveAddress(@Field("token") String token
            , @Field("type") String type
            , @Field("name") String name
            , @Field("mobile") String mobile
            , @Field("address") String address
            , @Field("state_id") String state_id
            , @Field("district_id") String district_id
            , @Field("landmark") String landmark
            , @Field("pincode") String pincode);

    @FormUrlEncoded
    @POST("myorders")
    Call<MyOrderRequest> getMyOrder(@Field("token") String token);

    @FormUrlEncoded
    @POST("getprofile")
    Call<ProfileRequest> getProfile(@Field("token") String token);

    @Multipart
    @POST("updateprofile")
    Call<UpdateProfileRequest> updateProfile(@Part("token") RequestBody token
            , @Part("name") RequestBody name
            , @Part("email") RequestBody email
            , @Part("mobile") RequestBody mobile
            , @Part MultipartBody.Part photo);

    @POST("orders")
    Call<Order> createOrder(@Body OrderRequest orderRequest);

}
