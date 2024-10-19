package com.ecom.agrisewa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ecom.agrisewa.R;
import com.ecom.agrisewa.model.ProductImageResponse;
import java.util.List;

public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.SliderViewHolder>{

    Context context;
    List<String> productImageResponseList;

    public ProductImageAdapter(Context context, List<String> productImageResponseList) {
        this.context = context;
        this.productImageResponseList = productImageResponseList;
    }

//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product_banner_item_layout, parent, false);
//        return new ViewHolder(view);
//    }
@NonNull
@Override
public  SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, parent, false);
    return  new SliderViewHolder(view);
}
    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        String productImageResponse = productImageResponseList.get(position);
        Glide.with(context)
                .load(productImageResponse)
                .into(holder.imgBanner);
    }

//    @Override
//    public int getCount() {
//        return productImageResponseList.size();
//    }

    @Override
    public int getItemCount() {
        return productImageResponseList.size();
    }


//    public class ViewHolder extends SliderViewAdapter.ViewHolder {
//
//        ImageView imgBanner;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//
//            imgBanner = itemView.findViewById(R.id.imgBanner);
//
//        }
//    }
public static class SliderViewHolder extends RecyclerView.ViewHolder {
    ImageView imgBanner;

    public SliderViewHolder(@NonNull View itemView) {
        super(itemView);
        imgBanner = itemView.findViewById(R.id.imgBanner);
    }
}

}
