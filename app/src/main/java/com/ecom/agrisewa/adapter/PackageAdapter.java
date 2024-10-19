package com.ecom.agrisewa.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ecom.agrisewa.R;
import com.ecom.agrisewa.handler.CategoryCallback;
import com.ecom.agrisewa.handler.PackageCallback;
import com.ecom.agrisewa.model.CategoryResponse;
import com.ecom.agrisewa.model.PackageResponse;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {

    Context context;
    String unit;
    List<PackageResponse> packageResponseList;
    PackageCallback packageCallback;
    int selectedPosition = 0;

    public PackageAdapter(Context context, String unit, List<PackageResponse> packageResponseList, PackageCallback packageCallback) {
        this.context = context;
        this.unit = unit;
        this.packageResponseList = packageResponseList;
        this.packageCallback = packageCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_package_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PackageResponse packageResponse = packageResponseList.get(holder.getAdapterPosition());
        holder.txtName.setText(packageResponse.getQuantity() + " " + unit);
        holder.cardPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });

        if (selectedPosition == holder.getAdapterPosition()) {
            holder.cardPackage.setCardBackgroundColor(context.getColor(R.color.main_color));
            holder.txtName.setTextColor(context.getColor(R.color.white));
            packageCallback.onPackageClick(packageResponse);
        } else {
            holder.cardPackage.setCardBackgroundColor(Color.parseColor("#F1F1F1"));
            holder.txtName.setTextColor(context.getColor(R.color.black));
        }

    }

    @Override
    public int getItemCount() {
        return packageResponseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        MaterialCardView cardPackage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            cardPackage = itemView.findViewById(R.id.cardPackage);

        }
    }

}
