package com.ecom.agrisewa.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ecom.agrisewa.R;
import com.ecom.agrisewa.handler.ProductCallback;
import com.ecom.agrisewa.model.ProductResponse;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    Context context;
    List<ProductResponse> productResponseList;
    ProductCallback productCallback;

    public ProductAdapter(Context context, List<ProductResponse> productResponseList, ProductCallback productCallback) {
        this.context = context;
        this.productResponseList = productResponseList;
        this.productCallback = productCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductResponse productResponse = productResponseList.get(holder.getAdapterPosition());
        holder.txtProductName.setText(productResponse.getName());
        holder.txtDiscountPrice.setText("₹ " + productResponse.getDiscountPrice());
        holder.txtPrice.setText("₹ " + productResponse.getPrice());
        holder.txtPrice.setPaintFlags(holder.txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if (productResponse.getDiscount().equals("0")) {
            holder.txtOff.setVisibility(View.GONE);
        } else {
            holder.txtOff.setVisibility(View.VISIBLE);
            holder.txtOff.setText(productResponse.getDiscount() + "% OFF");
        }
        if (!productResponse.getImage().isEmpty()) {
            Glide.with(context)
                    .load(productResponse.getImage())
                    .into(holder.imgProduct);
        }

        holder.cardProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productCallback.onProductClick(productResponse);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productResponseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        MaterialCardView cardProduct;
        TextView txtProductName, txtDiscountPrice, txtPrice, txtOff;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtDiscountPrice = itemView.findViewById(R.id.txtDiscountPrice);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtOff = itemView.findViewById(R.id.txtOff);
            cardProduct = itemView.findViewById(R.id.cardProduct);

        }
    }

}
