package com.ecom.agrisewa.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ecom.agrisewa.R;
import com.ecom.agrisewa.handler.AddressHandler;
import com.ecom.agrisewa.handler.CartCallback;
import com.ecom.agrisewa.model.AddressResponse;
import com.ecom.agrisewa.model.CartResponse;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    Context context;
    List<AddressResponse> addressResponseList;
    AddressHandler addressHandler;
    int selectedPosition = -1;

    public AddressAdapter(Context context, List<AddressResponse> addressResponseList, AddressHandler addressHandler) {
        this.context = context;
        this.addressResponseList = addressResponseList;
        this.addressHandler = addressHandler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_address_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddressResponse addressResponse = addressResponseList.get(position);
        holder.checkType.setText(addressResponse.getType());
        holder.txtDetails.setText(addressResponse.getName() + "\n" + addressResponse.getMobile());
        holder.txtAddress.setText(addressResponse.getAddress() + ", " + addressResponse.getLandmark() + ", " + addressResponse.getDistrict() + ", " + addressResponse.getState() + " - " + addressResponse.getPincode());

        holder.cardAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });

        if (selectedPosition == holder.getAdapterPosition()) {
            holder.checkType.setChecked(true);
            addressHandler.onAddressClick(addressResponse);
        } else {
            holder.checkType.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return addressResponseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardAddress;
        CheckBox checkType;
        TextView txtDetails, txtAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardAddress = itemView.findViewById(R.id.cardAddress);
            checkType = itemView.findViewById(R.id.checkType);
            txtDetails = itemView.findViewById(R.id.txtDetails);
            txtAddress = itemView.findViewById(R.id.txtAddress);

        }
    }

}
