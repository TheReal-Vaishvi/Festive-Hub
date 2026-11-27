package com.example.festivehub;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

public class EventBannerAdapter extends RecyclerView.Adapter<EventBannerAdapter.BannerViewHolder> {
    ArrayList<EventBanner> bannerlist;
    public EventBannerAdapter(ArrayList<EventBanner> bannerlist) {
        this.bannerlist = bannerlist;
    }
    @NonNull
    @Override
    public EventBannerAdapter.BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_banner, parent, false);
        return new BannerViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        EventBanner banner = bannerlist.get(position);
        // Display Cloudinary image if available
        if (banner.imageUrl != null && !banner.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(banner.imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.bannerImage);
        } else if (banner.image != 0) {
            // Display drawable image
            holder.bannerImage.setImageResource(banner.image);
        } else {
            holder.bannerImage.setImageResource(android.R.color.darker_gray);
        }
        holder.bannerTitle.setText(banner.title);
        holder.bannerSubtitle.setText(banner.subtitle);
    }
    @Override
    public int getItemCount() {
        return bannerlist.size();
    }
    public class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;
        TextView bannerTitle, bannerSubtitle;
        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);

            bannerImage = itemView.findViewById(R.id.BannerImg);
            bannerTitle = itemView.findViewById(R.id.txtTitle);
            bannerSubtitle = itemView.findViewById(R.id.txtSubTitle);
        }
    }
}