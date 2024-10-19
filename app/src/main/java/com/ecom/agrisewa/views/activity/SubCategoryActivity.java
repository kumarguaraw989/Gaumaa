package com.ecom.agrisewa.views.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecom.agrisewa.R;
import com.ecom.agrisewa.adapter.CartAdapter;
import com.ecom.agrisewa.adapter.ProductAdapter;
import com.ecom.agrisewa.adapter.SubCategoryAdapter;
import com.ecom.agrisewa.api.ApiClient;
import com.ecom.agrisewa.api.ServiceApi;
import com.ecom.agrisewa.handler.CartCallback;
import com.ecom.agrisewa.handler.ProductCallback;
import com.ecom.agrisewa.handler.SubCategoryCallback;
import com.ecom.agrisewa.model.CartAmount;
import com.ecom.agrisewa.model.CartRequest;
import com.ecom.agrisewa.model.CartResponse;
import com.ecom.agrisewa.model.CategoryResponse;
import com.ecom.agrisewa.model.DeleteCart;
import com.ecom.agrisewa.model.LoginResponse;
import com.ecom.agrisewa.model.FeaturedProductRequest;
import com.ecom.agrisewa.model.ProductRequest;
import com.ecom.agrisewa.model.ProductResponse;
import com.ecom.agrisewa.model.SubCategory;
import com.ecom.agrisewa.model.SubCategoryRequest;
import com.ecom.agrisewa.utils.LocalStorage;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubCategoryActivity extends AppCompatActivity implements SubCategoryCallback, ProductCallback, CartCallback {

    CategoryResponse categoryResponse;
    RecyclerView subCategoryRecycler, productRecycler;
    List<SubCategory> subCategoryList = new ArrayList<>();
    List<ProductResponse> productResponseList = new ArrayList<>();
    SubCategoryAdapter subCategoryAdapter;
    ProductAdapter productAdapter;
    LoginResponse loginResponse;
    CartAmount cartAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        LocalStorage localStorage = LocalStorage.getInstance(SubCategoryActivity.this);
        loginResponse = new Gson().fromJson(localStorage.getLoginModel(), LoginResponse.class);
        MaterialToolbar toolBar = findViewById(R.id.toolBar);
        TextView txtSubCategory = findViewById(R.id.txtSubCategory);
        subCategoryRecycler = findViewById(R.id.subCategoryRecycler);
        productRecycler = findViewById(R.id.productRecycler);
        categoryResponse = (CategoryResponse) getIntent().getSerializableExtra("category");
        if (categoryResponse != null) {
            toolBar.setTitle(categoryResponse.getName());
            txtSubCategory.setText("Our sub categories related to " + categoryResponse.getName());
        }

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.notification) {
                    return true;
                } else if (item.getItemId() == R.id.cart) {
                    showCartDialog();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSubCategory(categoryResponse.getId());
    }

    public void getSubCategory(String category) {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<SubCategoryRequest> call = api.getSubCategory(category);
        call.enqueue(new Callback<SubCategoryRequest>() {
            @Override
            public void onResponse(Call<SubCategoryRequest> call, Response<SubCategoryRequest> response) {
                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        subCategoryRecycler.setLayoutManager(new LinearLayoutManager(SubCategoryActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        subCategoryAdapter = new SubCategoryAdapter(SubCategoryActivity.this, subCategoryList, SubCategoryActivity.this);
                        subCategoryRecycler.setAdapter(subCategoryAdapter);
                        subCategoryList.addAll(response.body().getSubcategories());
                        subCategoryAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SubCategoryActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SubCategoryRequest> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

    public void getProducts(String token, String query, String category) {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<ProductRequest> call = api.getProduct(token, query, category);
        call.enqueue(new Callback<ProductRequest>() {
            @Override
            public void onResponse(Call<ProductRequest> call, Response<ProductRequest> response) {
                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        productResponseList.clear();
                        productRecycler.setLayoutManager(new GridLayoutManager(SubCategoryActivity.this, 2));
                        productAdapter = new ProductAdapter(SubCategoryActivity.this, productResponseList, SubCategoryActivity.this);
                        productRecycler.setAdapter(productAdapter);
                        productResponseList.addAll(response.body().getFeaturedproducts());
                        productAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SubCategoryActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductRequest> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onSubCategoryClick(SubCategory subCategory) {
        getProducts(loginResponse.getToken()
                , ""
                , subCategory.getId());
    }

    @Override
    public void onProductClick(ProductResponse productResponse) {
        Intent intent = new Intent(SubCategoryActivity.this, ProductDetailsActivity.class);
        intent.putExtra("product", productResponse);
        startActivity(intent);
    }

    Dialog cartDialog;
    RecyclerView cartRecycler;
    CartAdapter cartAdapter;
    TextView txtQty, txtCartAmount, txtDeliveryAmount, txtTotalAmount;
    List<CartResponse> cartResponseList = new ArrayList<>();

    private void showCartDialog() {
        cartDialog = new Dialog(SubCategoryActivity.this);
        cartDialog.setContentView(R.layout.custom_cart_drawer_layout);
        cartDialog.setCancelable(true);

        txtQty = cartDialog.findViewById(R.id.txtQty);
        txtCartAmount = cartDialog.findViewById(R.id.txtCartAmount);
        txtDeliveryAmount = cartDialog.findViewById(R.id.txtDeliveryAmount);
        txtTotalAmount = cartDialog.findViewById(R.id.txtTotalAmount);
        cartRecycler = cartDialog.findViewById(R.id.cartRecycler);
        ImageView imgCross = cartDialog.findViewById(R.id.imgCross);
        LinearLayout transLayer = cartDialog.findViewById(R.id.transLayer);
        MaterialButton btnCheckOut = cartDialog.findViewById(R.id.btnCheckOut);
        getCart(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
                , loginResponse.getToken());

        imgCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartDialog.dismiss();
            }
        });

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartAmount != null) {
                    cartDialog.dismiss();
                    Intent intent = new Intent(SubCategoryActivity.this, CheckoutActivity.class);
                    intent.putExtra("cartAmount", cartAmount);
                    startActivity(intent);
                } else {
                    Toast.makeText(SubCategoryActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        transLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartDialog.dismiss();
            }
        });

        cartDialog.show();
        cartDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        cartDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cartDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        cartDialog.getWindow().setGravity(Gravity.TOP);
        cartDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        cartDialog.getWindow().setStatusBarColor(getColor(R.color.main_color));
    }

    public void getCart(String deviceId, String token) {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<CartRequest> call = api.getCart(deviceId, token);
        call.enqueue(new Callback<CartRequest>() {
            @Override
            public void onResponse(Call<CartRequest> call, Response<CartRequest> response) {
                if (response.body() != null) {
                    cartRecycler.setLayoutManager(new LinearLayoutManager(SubCategoryActivity.this, LinearLayoutManager.VERTICAL, false));
                    cartAdapter = new CartAdapter(SubCategoryActivity.this, cartResponseList, SubCategoryActivity.this);
                    if (response.body().getStatus()) {
                        cartAmount = response.body().getAmount();
                        cartResponseList.clear();
                        cartResponseList.addAll(response.body().getCart());
                        cartRecycler.setAdapter(cartAdapter);
                        txtQty.setText(cartResponseList.size() + " Item");
                        txtCartAmount.setText("₹ " + cartAmount.getCartAmount());
                        txtDeliveryAmount.setText("₹ " + cartAmount.getDeliveryCharge());
                        txtTotalAmount.setText("₹ " + cartAmount.getTotalAmount());
                        cartAdapter.notifyDataSetChanged();
                    } else {
                        cartAmount = null;
                        cartResponseList.clear();
                        txtQty.setText(cartResponseList.size() + " Item");
                        txtCartAmount.setText("₹ 0.00");
                        txtDeliveryAmount.setText("₹ 0.00");
                        txtTotalAmount.setText("₹ 0.00");
                        cartAdapter.notifyDataSetChanged();
                        Toast.makeText(SubCategoryActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartRequest> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

    public void deleteCart(String cartId) {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<DeleteCart> call = api.deleteFromCart(cartId);
        call.enqueue(new Callback<DeleteCart>() {
            @Override
            public void onResponse(Call<DeleteCart> call, Response<DeleteCart> response) {
                if (response.body() != null) {
                    Toast.makeText(SubCategoryActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    getCart(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
                            , loginResponse.getToken());
                }
            }

            @Override
            public void onFailure(Call<DeleteCart> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onCartDelete(CartResponse cartResponse) {
        deleteCart(cartResponse.getId());
    }

}