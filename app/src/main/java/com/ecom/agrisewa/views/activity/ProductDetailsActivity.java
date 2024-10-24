package com.ecom.agrisewa.views.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.ecom.agrisewa.R;
import com.ecom.agrisewa.adapter.BannerAdapter;
import com.ecom.agrisewa.adapter.CartAdapter;
import com.ecom.agrisewa.adapter.PackageAdapter;
import com.ecom.agrisewa.adapter.ProductAdapter;
import com.ecom.agrisewa.adapter.ProductImageAdapter;
import com.ecom.agrisewa.adapter.RelatedProductAdapter;
import com.ecom.agrisewa.api.ApiClient;
import com.ecom.agrisewa.api.ServiceApi;
import com.ecom.agrisewa.handler.CartCallback;
import com.ecom.agrisewa.handler.PackageCallback;
import com.ecom.agrisewa.handler.ProductCallback;
import com.ecom.agrisewa.model.AddToCart;
import com.ecom.agrisewa.model.BannerRequest;
import com.ecom.agrisewa.model.CartAmount;
import com.ecom.agrisewa.model.CartCount;
import com.ecom.agrisewa.model.CartRequest;
import com.ecom.agrisewa.model.CartResponse;
import com.ecom.agrisewa.model.DeleteCart;
import com.ecom.agrisewa.model.LoginResponse;
import com.ecom.agrisewa.model.PackageResponse;
import com.ecom.agrisewa.model.ProductImageRequest;
import com.ecom.agrisewa.model.ProductImageResponse;
import com.ecom.agrisewa.model.ProductRequest;
import com.ecom.agrisewa.model.ProductResponse;
import com.ecom.agrisewa.utils.LocalStorage;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity implements PackageCallback, ProductCallback, CartCallback {

    ProductResponse productResponse;
    //SliderView slider;
    LoginResponse loginResponse;
    RecyclerView packageRecycler, relatedRecycler;
    PackageAdapter packageAdapter;
    RelatedProductAdapter relatedProductAdapter;
    ProductImageAdapter productImageAdapter;
    List<ProductImageResponse> productImageResponseList = new ArrayList<>();
    List<PackageResponse> packageResponseList = new ArrayList<>();
    List<ProductResponse> productResponseList = new ArrayList<>();
    TextView txtAmount, txtPrice, txtDiscountPrice, txtOff, txtDescription, txtUnit;
    int quantity = 1;
    String packageId;
    CartAmount cartAmount;
    //my slider code
    private ViewPager2 viewPager;
    private List<String> imageList;
    private Handler handler;
    private Runnable sliderRunnable;
    private int currentPage = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        LocalStorage localStorage = LocalStorage.getInstance(ProductDetailsActivity.this);
        loginResponse = new Gson().fromJson(localStorage.getLoginModel(), LoginResponse.class);
        MaterialToolbar toolBar = findViewById(R.id.toolBar);
        //slider = findViewById(R.id.slider);
        txtPrice = findViewById(R.id.txtPrice);
        txtDiscountPrice = findViewById(R.id.txtDiscountPrice);
        txtOff = findViewById(R.id.txtOff);
        txtDescription = findViewById(R.id.txtDescription);
        txtAmount = findViewById(R.id.txtAmount);
        txtUnit = findViewById(R.id.txtUnit);
        txtPrice.setPaintFlags(txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        packageRecycler = findViewById(R.id.packageRecycler);
        relatedRecycler = findViewById(R.id.relatedRecycler);
        TextView txtProductName = findViewById(R.id.txtProductName);
        TextView txtRelatedProduct = findViewById(R.id.txtRelatedProduct);
        CircleImageView imgMinus = findViewById(R.id.imgMinus);
        CircleImageView imgPlus = findViewById(R.id.imgPlus);
        MaterialButton btnAddToCart = findViewById(R.id.btnAddToCart);
        viewPager = findViewById(R.id.slider);
        imageList = new ArrayList<>();
        productResponse = (ProductResponse) getIntent().getSerializableExtra("product");
        if (productResponse != null) {
            packageResponseList.addAll(productResponse.getPackages());
            toolBar.setTitle(productResponse.getName());
            txtProductName.setText(productResponse.getName());
            txtRelatedProduct.setText("Explore product related to " + productResponse.getName());
            txtDescription.setText(productResponse.getShortDescription().isEmpty() ? "No Description" : productResponse.getShortDescription().replace("\r\n", ""));
            txtUnit.setText(quantity + " Qty");
        }

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                txtUnit.setText(quantity + " Qty");
            }
        });

        imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    txtUnit.setText(quantity + " Qty");
                }
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
                        , loginResponse.getToken()
                        , productResponse.getId()
                        , String.valueOf(quantity)
                        , packageId);
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
        getBannerImages(productResponse.getId());
        getCartCount();
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


    public void getBannerImages(String productId) {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<ProductImageRequest> call = api.getProductImage(productId);
        call.enqueue(new Callback<ProductImageRequest>() {
            @Override
            public void onResponse(Call<ProductImageRequest> call, Response<ProductImageRequest> response) {
                getPackage();
                if (response.body() != null) {
                    if (response.body().getStatus()) {
//                        productImageResponseList.clear();
//                        productImageAdapter = new ProductImageAdapter(ProductDetailsActivity.this, productImageResponseList);
////                        slider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
////                        slider.setSliderAdapter(productImageAdapter);
////                        slider.setScrollTimeInSec(3);
////                        slider.setAutoCycle(true);
////                        slider.startAutoCycle();
//                        productImageResponseList.addAll(response.body().getImages());
//                        productImageAdapter.notifyDataSetChanged();
                        imageList.clear();
                        productImageAdapter = new ProductImageAdapter(ProductDetailsActivity.this, imageList);
//                        bannerResponseList.addAll(response.body().getBannerimages());
//                        Log.e("TAG", "onResponse: BannerRequestttt"+new Gson().toJson(bannerResponseList));
                        for (int i=0;i<response.body().getImages().size();i++){
                            imageList.add(response.body().getImages().get(i).getImage());
                        }
                        Log.e("TAG", "onResponse: "+new Gson().toJson(imageList));
                        //  Auto slide functionality
//                        sliderAdapter = new ImageSliderAdapter(this, imageList);
                        viewPager.setAdapter(productImageAdapter);
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
                        Toast.makeText(ProductDetailsActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductImageRequest> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

    public void getPackage() {
        packageRecycler.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        packageAdapter = new PackageAdapter(ProductDetailsActivity.this, productResponse.getUnit(), packageResponseList, this);
        packageRecycler.setAdapter(packageAdapter);
    }

    public void getRelatedProducts(String token, String query, String category) {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<ProductRequest> call = api.getProduct(token, query, category);
        call.enqueue(new Callback<ProductRequest>() {
            @Override
            public void onResponse(Call<ProductRequest> call, Response<ProductRequest> response) {
                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        productResponseList.clear();
                        relatedRecycler.setLayoutManager(new GridLayoutManager(ProductDetailsActivity.this, 2));
                        relatedProductAdapter = new RelatedProductAdapter(ProductDetailsActivity.this, productResponseList, ProductDetailsActivity.this);
                        relatedRecycler.setAdapter(relatedProductAdapter);
                        productResponseList.addAll(response.body().getFeaturedproducts());
                        relatedProductAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ProductDetailsActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductRequest> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

    public void addToCart(String deviceId, String token, String productId, String quantity, String packageId) {
        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
        Call<AddToCart> call = api.addToCart(deviceId, token, productId, quantity, packageId);
        call.enqueue(new Callback<AddToCart>() {
            @Override
            public void onResponse(Call<AddToCart> call, Response<AddToCart> response) {
                if (response.body() != null) {
                    Toast.makeText(ProductDetailsActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddToCart> call, Throwable t) {
                Log.e("EXCEPTION", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onPackageClick(PackageResponse packageResponse) {
        packageId = packageResponse.getId();
        txtPrice.setText("₹ " + packageResponse.getPrice());
        txtDiscountPrice.setText("₹ " + packageResponse.getDiscountPrice());
        txtAmount.setText("₹ " + packageResponse.getDiscountPrice());
        if (productResponse.getDiscount().equals("0")) {
            txtOff.setVisibility(View.GONE);
        } else {
            txtOff.setVisibility(View.VISIBLE);
            txtOff.setText(packageResponse.getDiscount() + "% OFF");
        }
        getRelatedProducts(loginResponse.getToken(), "", productResponse.getCategory());
    }

    @Override
    public void onProductClick(ProductResponse productResponse) {
        Intent intent = new Intent(ProductDetailsActivity.this, ProductDetailsActivity.class);
        intent.putExtra("product", productResponse);
        startActivity(intent);
    }

    Dialog cartDialog;
    RecyclerView cartRecycler;
    CartAdapter cartAdapter;
    TextView txtQty, txtCartAmount, txtDeliveryAmount, txtTotalAmount;
    List<CartResponse> cartResponseList = new ArrayList<>();

    private void showCartDialog() {
        cartDialog = new Dialog(ProductDetailsActivity.this);
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
                    Intent intent = new Intent(ProductDetailsActivity.this, CheckoutActivity.class);
                    intent.putExtra("cartAmount", cartAmount);
                    startActivity(intent);
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
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
                    cartRecycler.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
                    cartAdapter = new CartAdapter(ProductDetailsActivity.this, cartResponseList, ProductDetailsActivity.this);
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
                        Toast.makeText(ProductDetailsActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ProductDetailsActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
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