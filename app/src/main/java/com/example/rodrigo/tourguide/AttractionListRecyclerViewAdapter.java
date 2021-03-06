package com.example.rodrigo.tourguide;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rodrigo.tourguide.models.Business;

import java.util.List;

public class AttractionListRecyclerViewAdapter extends RecyclerView.Adapter<AttractionListRecyclerViewAdapter.ViewHolder> {
    private List<Business> businesses;
    private String reviewCountText;
    private Context context;

    public AttractionListRecyclerViewAdapter(List<Business> businesses, Context context) {
        this.businesses = businesses;
        this.context = context;
        this.reviewCountText = context.getString(R.string.review_count);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView businessPhoto;
        public TextView businessName;
        public ImageView businessRating;
        public TextView reviewCount;
        public ImageView yelpTrademark;
        public ViewHolder(RelativeLayout relativeLayout) {
            super(relativeLayout);
            this.businessPhoto = (ImageView) relativeLayout.getChildAt(0);
            this.businessName = (TextView) relativeLayout.getChildAt(1);
            this.businessRating = (ImageView) relativeLayout.getChildAt(2);
            this.reviewCount = (TextView) relativeLayout.getChildAt(3);
            this.yelpTrademark = (ImageView) relativeLayout.getChildAt(4);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attraction_list, parent, false);

        return new ViewHolder(relativeLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Business business = businesses.get(position);

        if (business.getBusinessPhoto() != null)
            holder.businessPhoto.setImageBitmap(business.getBusinessPhoto());
        else if (business.hasImageDownloadFailed())
            holder.businessPhoto.setImageResource(R.drawable.baseline_error_24);
        else
            holder.businessPhoto.setImageResource(R.drawable.baseline_image_24);

        holder.businessName.setText(business.getName());
        holder.reviewCount.setText(String.format(reviewCountText, business.getReview_count()));

        holder.yelpTrademark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri dataUri = Uri.parse(business.getUrl());
                Intent businessPageIntent = new Intent(Intent.ACTION_VIEW, dataUri);

                if (businessPageIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(businessPageIntent);
                } else {
                    Log.d("BusinessPageIntent", "No activity available");
                }
            }
        });

        String rating = String.valueOf(business.getRating());
        switch (rating) {
            case "0.0":
                holder.businessRating.setImageResource(R.drawable.stars_regular_0);
                break;
            case "1.0":
                holder.businessRating.setImageResource(R.drawable.stars_regular_1);
                break;
            case "1.5":
                holder.businessRating.setImageResource(R.drawable.stars_regular_1_half);
                break;
            case "2.0":
                holder.businessRating.setImageResource(R.drawable.stars_regular_2);
                break;
            case "2.5":
                holder.businessRating.setImageResource(R.drawable.stars_regular_2_half);
                break;
            case "3.0":
                holder.businessRating.setImageResource(R.drawable.stars_regular_3);
                break;
            case "3.5":
                holder.businessRating.setImageResource(R.drawable.stars_regular_3_half);
                break;
            case "4.0":
                holder.businessRating.setImageResource(R.drawable.stars_regular_4);
                break;
            case "4.5":
                holder.businessRating.setImageResource(R.drawable.stars_regular_4_half);
                break;
            default:
                holder.businessRating.setImageResource(R.drawable.stars_regular_5);
        }
    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }
}
