package com.ecom.agrisewa.adapter;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ecom.agrisewa.R;
import com.ecom.agrisewa.model.BannerResponse;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.SliderViewHolder>{

    Context context;
    List<BannerResponse> bannerList;
    private List<String> imageList;

    public BannerAdapter(Context context, List<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        String banner = imageList.get(position);
        Log.e("TAG", "onBindViewHolder: adapter"+banner);
        Glide.with(context)
                .load(banner)
                .into(holder.imgBanner);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
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
