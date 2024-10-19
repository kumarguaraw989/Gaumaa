package com.ecom.agrisewa.adapter;

import android.content.Context;
import android.graphics.Color;
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
import com.ecom.agrisewa.handler.CartCallback;
import com.ecom.agrisewa.handler.SubCategoryCallback;
import com.ecom.agrisewa.model.CartResponse;
import com.ecom.agrisewa.model.SubCategory;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    List<CartResponse> cartResponseList;
    CartCallback cartCallback;

    public CartAdapter(Context context, List<CartResponse> cartResponseList, CartCallback cartCallback) {
        this.context = context;
        this.cartResponseList = cartResponseList;
        this.cartCallback = cartCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cart_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartResponse cartResponse = cartResponseList.get(holder.getAdapterPosition());
        holder.txtProductName.setText(cartResponse.getName());
        holder.txtDiscountPrice.setText("₹ " + cartResponse.getAmount());
        holder.txtPrice.setPaintFlags(holder.txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.txtPrice.setText("₹ " + cartResponse.getPrice());
        if (cartResponse.getDiscount().equals("0.00")) {
            holder.txtOff.setVisibility(View.GONE);
        } else {
            holder.txtOff.setVisibility(View.VISIBLE);
            holder.txtOff.setText(cartResponse.getDiscount() + "% OFF");
        }
        holder.txtQty.setText(cartResponse.getQuantity() + " Item");
        Glide.with(context)
                .load(cartResponse.getImage())
                .into(holder.imgBanner);

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartCallback.onCartDelete(cartResponse);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartResponseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtProductName, txtDiscountPrice, txtPrice, txtOff, txtQty;
        ImageView imgBanner, imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtDiscountPrice = itemView.findViewById(R.id.txtDiscountPrice);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtOff = itemView.findViewById(R.id.txtOff);
            txtQty = itemView.findViewById(R.id.txtQty);
            imgBanner = itemView.findViewById(R.id.imgBanner);
            imgDelete = itemView.findViewById(R.id.imgDelete);

        }
    }

}
