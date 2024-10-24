package com.ecom.agrisewa.views.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.ecom.agrisewa.R;
import com.ecom.agrisewa.adapter.BannerAdapter;
import com.ecom.agrisewa.adapter.CartAdapter;
import com.ecom.agrisewa.adapter.CategoryAdapter;
import com.ecom.agrisewa.adapter.FeaturedProductAdapter;
import com.ecom.agrisewa.api.ApiClient;
import com.ecom.agrisewa.api.ServiceApi;
import com.ecom.agrisewa.handler.CartCallback;
import com.ecom.agrisewa.handler.CategoryCallback;
import com.ecom.agrisewa.handler.ProductCallback;
import com.ecom.agrisewa.model.BannerRequest;
import com.ecom.agrisewa.model.BannerResponse;
import com.ecom.agrisewa.model.CartAmount;
import com.ecom.agrisewa.model.CartCount;
import com.ecom.agrisewa.model.CartRequest;
import com.ecom.agrisewa.model.CartResponse;
import com.ecom.agrisewa.model.CategoryRequest;
import com.ecom.agrisewa.model.CategoryResponse;
import com.ecom.agrisewa.model.DeleteCart;
import com.ecom.agrisewa.model.FeaturedProductRequest;
import com.ecom.agrisewa.model.LoginResponse;
import com.ecom.agrisewa.model.ProductResponse;
import com.ecom.agrisewa.utils.GpsLocation;
import com.ecom.agrisewa.utils.LocalStorage;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity implements CategoryCallback, ProductCallback, CartCallback {

//    SliderView slider;
    LoginResponse loginResponse;
    RecyclerView categoryRecycler, featuredRecycler;
    List<CategoryResponse> categoryResponseList = new ArrayList<>();
    List<ProductResponse> productResponseList = new ArrayList<>();
    BannerAdapter bannerAdapter;
    CategoryAdapter categoryAdapter;
    FeaturedProductAdapter featuredProductAdapter;
    LocalStorage localStorage;
    CartAmount cartAmount;


    //my slider code
    private List<String> imageList;
    private ViewPager2 viewPager;
    private Handler handler;
    private Runnable sliderRunnable;
    private int currentPage = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

//        slider = findViewById(R.id.slider);
        MaterialToolbar toolBar = findViewById(R.id.toolBar);
        categoryRecycler = findViewById(R.id.categoryRecycler);
        featuredRecycler = findViewById(R.id.featuredRecycler);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.sliders);
        imageList = new ArrayList<>();
        String location = GpsLocation.getGPSLocation(DashboardActivity.this);
        if (!location.isEmpty()) {
            String[] address = location.split("-");
            Log.e("CITY", address[0] + "");
            Log.e("LOCATION", address[1] + "");
//            toolBar.setTitle(address[0]);
//            toolBar.setSubtitle(address[1]);
        }

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDrawerDialog();
            }
        });
    }


    public void getCartCount() {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        @SuppressLint("HardwareIds") Call<CartCount> call = api.getCartCount(
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID),
                loginResponse.getToken()
        );

        call.enqueue(new Callback<CartCount>() {
            @Override
            public void onResponse(Call<CartCount> call, Response<CartCount> response) {
                    int cartCountValue = response.body().getCount();
                    Log.e("TAG", "onResponse: getCartCount"+cartCountValue);
                    TextView cartCountTextView = findViewById(R.id.cart_count);
                    if (response.body()!=null&&response.body().getStatus()){
                        cartCountTextView.setText(String.valueOf(cartCountValue));
                    }
            }

            @Override
            public void onFailure(Call<CartCount> call, Throwable throwable) {
                Log.e("EXCEPTION", throwable.getLocalizedMessage());
            }
        });
    }

    // Similarly modify your getNotificationCount method
    public void getNotificationCount() {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        // Your API call logic here
        // Update notification badge count similarly to cart
    }


    @Override
    protected void onResume() {
        super.onResume();
        localStorage = LocalStorage.getInstance(DashboardActivity.this);
        loginResponse = new Gson().fromJson(localStorage.getLoginModel(), LoginResponse.class);
        getBannerImages();
        Log.e("TAG", "onResume: getCartCount");
        getCartCount();
        getNotificationCount();
    }

    public void getBannerImages() {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<BannerRequest> call = api.getBanner();
        call.enqueue(new Callback<BannerRequest>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<BannerRequest> call, Response<BannerRequest> response) {
                getCategory();
                Log.e("TAG", "onResponse: BannerRequest"+new Gson().toJson(response.body()));
                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        imageList.clear();
                        bannerAdapter = new BannerAdapter(DashboardActivity.this, imageList);
//                        bannerResponseList.addAll(response.body().getBannerimages());
//                        Log.e("TAG", "onResponse: BannerRequestttt"+new Gson().toJson(bannerResponseList));
                        for (int i=0;i<response.body().getBannerimages().size();i++){
                            imageList.add(response.body().getBannerimages().get(i).getImage());
                        }
                        Log.e("TAG", "onResponse: "+new Gson().toJson(imageList));
                        //  Auto slide functionality
//                        sliderAdapter = new ImageSliderAdapter(this, imageList);
                        viewPager.setAdapter(bannerAdapter);



                        // Auto slide functionality
                        handler = new Handler();
                        sliderRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (currentPage == imageList.size()) {
                                    currentPage = 0;
                                }
                                viewPager.setCurrentItem(currentPage++, true);
                                handler.postDelayed(this, 3000); // Slide every 3 seconds
                            }
                        };

                        handler.post(sliderRunnable);

                        // Optional: Add page change listener to reset timer when user swipes manually
                        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                            @Override
                            public void onPageSelected(int position) {
                                currentPage = position;
                                handler.removeCallbacks(sliderRunnable);
                                handler.postDelayed(sliderRunnable, 3000);
                            }
                        });
                    } else {
                        Toast.makeText(DashboardActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BannerRequest> call, @NonNull Throwable t) {
                Log.e("EXCEPTION", Objects.requireNonNull(t.getLocalizedMessage()));
            }
        });
    }

    public void getCategory() {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<CategoryRequest> call = api.getCategory();
        call.enqueue(new Callback<CategoryRequest>() {
            @Override
            public void onResponse(Call<CategoryRequest> call, Response<CategoryRequest> response) {
                getFeaturedProduct();
                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        categoryResponseList.clear();
                        categoryRecycler.setLayoutManager(new LinearLayoutManager(DashboardActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        categoryAdapter = new CategoryAdapter(DashboardActivity.this, categoryResponseList, DashboardActivity.this);
                        categoryRecycler.setAdapter(categoryAdapter);
                        categoryResponseList.addAll(response.body().getCategories());
                        categoryAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(DashboardActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryRequest> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

    public void getFeaturedProduct() {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<FeaturedProductRequest> call = api.getFeaturedProduct();
        call.enqueue(new Callback<FeaturedProductRequest>() {
            @Override
            public void onResponse(Call<FeaturedProductRequest> call, Response<FeaturedProductRequest> response) {
                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        productResponseList.clear();
                        featuredRecycler.setLayoutManager(new GridLayoutManager(DashboardActivity.this, 2));
                        featuredProductAdapter = new FeaturedProductAdapter(DashboardActivity.this, productResponseList, DashboardActivity.this);
                        featuredRecycler.setAdapter(featuredProductAdapter);
                        productResponseList.addAll(response.body().getFeaturedproducts());
                        featuredProductAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(DashboardActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<FeaturedProductRequest> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

    Dialog drawerDialog;

    private void showDrawerDialog() {
        drawerDialog = new Dialog(DashboardActivity.this);
        drawerDialog.setContentView(R.layout.custom_drawer_layout);
        drawerDialog.setCancelable(true);

        LinearLayout transLayer = drawerDialog.findViewById(R.id.transLayer);
        LinearLayout myOrderLayout = drawerDialog.findViewById(R.id.myOrderLayout);
        LinearLayout logoutLayout = drawerDialog.findViewById(R.id.logoutLayout);
        LinearLayout profileLayout = drawerDialog.findViewById(R.id.profileLayout);
        TextView txtName = drawerDialog.findViewById(R.id.txtName);
        TextView txtEmail = drawerDialog.findViewById(R.id.txtEmail);
        if (loginResponse != null) {
            txtName.setText(loginResponse.getName());
            txtEmail.setText(loginResponse.getEmail());
            Log.e("TOKEN", "" + loginResponse.getToken());
        }

        myOrderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerDialog.dismiss();
                startActivity(new Intent(DashboardActivity.this, MyOrderActivity.class));
            }
        });

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerDialog.dismiss();
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
            }
        });

        transLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
            }
        });

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(DashboardActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout this session?")
                        .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                drawerDialog.dismiss();
                                localStorage.logout();
                                startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                                finishAffinity();
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });

        drawerDialog.show();
        drawerDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        drawerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        drawerDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        drawerDialog.getWindow().setGravity(Gravity.TOP);
        drawerDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        drawerDialog.getWindow().setStatusBarColor(getColor(R.color.main_color));
    }

    Dialog cartDialog;
    RecyclerView cartRecycler;
    CartAdapter cartAdapter;
    TextView txtQty, txtCartAmount, txtDeliveryAmount, txtTotalAmount;
    List<CartResponse> cartResponseList = new ArrayList<>();

    private void showCartDialog() {
        cartDialog = new Dialog(DashboardActivity.this);
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
                    Intent intent = new Intent(DashboardActivity.this, CheckoutActivity.class);
                    intent.putExtra("cartAmount", cartAmount);
                    startActivity(intent);
                } else {
                    Toast.makeText(DashboardActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
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
    /*public void getCartCount(){
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Toast.makeText(this, "cart is here !", Toast.LENGTH_SHORT).show();
        Call<CartRequest> call = api.getCartCount(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
                , loginResponse.getToken());
        call.enqueue(new Callback<CartRequest>() {
            @Override
            public void onResponse(Call<CartRequest> call, Response<CartRequest> response) {
                Log.e("TAG", "onResponse: cart is here !"+response);
                if (response.body() != null) {
                    if (response.body().getStatus()) {

                    }
                }
            }

            @Override
            public void onFailure(Call<CartRequest> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

    ///Model not prepared yet !
    public void getNotificationCount(){
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<CartRequest> call = api.getCartCount(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
                , loginResponse.getToken());
        call.enqueue(new Callback<CartRequest>() {
            @Override
            public void onResponse(Call<CartRequest> call, Response<CartRequest> response) {
                if (response.body() != null) {
                    if (response.body().getStatus()) {

                    }
                }
            }

            @Override
            public void onFailure(Call<CartRequest> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }*/
    public void getCart(String deviceId, String token) {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<CartRequest> call = api.getCart(deviceId, token);
        call.enqueue(new Callback<CartRequest>() {
            @Override
            public void onResponse(Call<CartRequest> call, Response<CartRequest> response) {
                if (response.body() != null) {
                    Log.e("TAG", "onResponse: cart api"+new Gson().toJson(response.body()));
                    cartRecycler.setLayoutManager(new LinearLayoutManager(DashboardActivity.this, LinearLayoutManager.VERTICAL, false));
                    cartAdapter = new CartAdapter(DashboardActivity.this, cartResponseList, DashboardActivity.this);
                    if (response.body().getStatus()) {
                        cartAmount = response.body().getAmount();
                        cartResponseList.clear();
                        cartRecycler.setAdapter(cartAdapter);
                        cartResponseList.addAll(response.body().getCart());
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
                        Toast.makeText(DashboardActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(DashboardActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onCategoryClick(CategoryResponse categoryResponse) {
        Intent intent = new Intent(DashboardActivity.this, SubCategoryActivity.class);
        intent.putExtra("category", categoryResponse);
        startActivity(intent);
    }

    @Override
    public void onProductClick(ProductResponse productResponse) {
        Intent intent = new Intent(DashboardActivity.this, ProductDetailsActivity.class);
        intent.putExtra("product", productResponse);
        startActivity(intent);
    }

    @Override
    public void onCartDelete(CartResponse cartResponse) {
        deleteCart(cartResponse.getId());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(sliderRunnable);
    }
}