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
import com.ecom.agrisewa.model.CartResponse;
import com.ecom.agrisewa.model.MyOrderItem;
import com.ecom.agrisewa.model.MyOrderResponse;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    Context context;
    List<MyOrderItem> myOrderItemList;

    public OrderItemAdapter(Context context, List<MyOrderItem> myOrderItemList) {
        this.context = context;
        this.myOrderItemList = myOrderItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_order_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyOrderItem myOrderItem = myOrderItemList.get(holder.getAdapterPosition());
        holder.txtProductName.setText(myOrderItem.getName());
        holder.txtDiscountPrice.setText("₹ " + myOrderItem.getAmount());
        holder.txtPrice.setPaintFlags(holder.txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.txtPrice.setText("₹ " + myOrderItem.getPrice());
        if (myOrderItem.getDiscount().equals("0.00")) {
            holder.txtOff.setVisibility(View.GONE);
        } else {
            holder.txtOff.setVisibility(View.VISIBLE);
            holder.txtOff.setText(myOrderItem.getDiscount() + "% OFF");
        }
        holder.txtQty.setText(myOrderItem.getQuantity() + " Item");
        Glide.with(context)
                .load(R.drawable.logo)
                .into(holder.imgBanner);
    }

    @Override
    public int getItemCount() {
        return myOrderItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtProductName, txtDiscountPrice, txtPrice, txtOff, txtQty;
        ImageView imgBanner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtDiscountPrice = itemView.findViewById(R.id.txtDiscountPrice);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtOff = itemView.findViewById(R.id.txtOff);
            txtQty = itemView.findViewById(R.id.txtQty);
            imgBanner = itemView.findViewById(R.id.imgBanner);

        }
    }

}
