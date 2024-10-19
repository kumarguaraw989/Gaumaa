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
import com.ecom.agrisewa.model.CategoryResponse;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context context;
    List<CategoryResponse> categoryResponses;
    CategoryCallback categoryCallback;
    int selectedPosition = -1;

    public CategoryAdapter(Context context, List<CategoryResponse> categoryResponses, CategoryCallback categoryCallback) {
        this.context = context;
        this.categoryResponses = categoryResponses;
        this.categoryCallback = categoryCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_category_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryResponse categoryResponse = categoryResponses.get(holder.getAdapterPosition());
        holder.txtName.setText(categoryResponse.getName());
        if (!categoryResponse.getImage().isEmpty()) {
            Glide.with(context)
                    .load(categoryResponse.getImage())
                    .into(holder.imgCategory);
        }

        holder.cardCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });

        if (selectedPosition == holder.getAdapterPosition()) {
            holder.cardCategory.setCardBackgroundColor(context.getColor(R.color.main_color));
            holder.txtName.setTextColor(context.getColor(R.color.white));
            categoryCallback.onCategoryClick(categoryResponse);
        } else {
            holder.cardCategory.setCardBackgroundColor(Color.parseColor("#F1F1F1"));
            holder.txtName.setTextColor(context.getColor(R.color.black));
        }

    }

    @Override
    public int getItemCount() {
        return categoryResponses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        CircleImageView imgCategory;
        MaterialCardView cardCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            cardCategory = itemView.findViewById(R.id.cardCategory);

        }
    }

}
