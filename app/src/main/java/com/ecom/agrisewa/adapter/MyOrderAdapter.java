package com.ecom.agrisewa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecom.agrisewa.R;
import com.ecom.agrisewa.handler.AddressHandler;
import com.ecom.agrisewa.model.AddressResponse;
import com.ecom.agrisewa.model.MyOrderResponse;
import com.ecom.agrisewa.utils.Constant;

import java.util.List;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {

    Context context;
    List<MyOrderResponse> orderResponseList;

    public MyOrderAdapter(Context context, List<MyOrderResponse> orderResponseList) {
        this.context = context;
        this.orderResponseList = orderResponseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_my_order_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyOrderResponse myOrderResponse = orderResponseList.get(holder.getAdapterPosition());
        holder.txtOrderNo.setText("Order ID: #" + myOrderResponse.getOrderNo());
        holder.txtDate.setText(Constant.getFormattedDate(myOrderResponse.getDate()));
        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(context, myOrderResponse.getItems());
        holder.orderItemRecycler.setAdapter(orderItemAdapter);
    }

    @Override
    public int getItemCount() {
        return orderResponseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtOrderNo, txtDate;
        RecyclerView orderItemRecycler;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtOrderNo = itemView.findViewById(R.id.txtOrderNo);
            txtDate = itemView.findViewById(R.id.txtDate);
            orderItemRecycler = itemView.findViewById(R.id.orderItemRecycler);
            orderItemRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        }
    }

}
