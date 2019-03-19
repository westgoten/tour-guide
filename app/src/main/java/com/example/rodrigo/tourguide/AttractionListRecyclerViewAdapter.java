package com.example.rodrigo.tourguide;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rodrigo.tourguide.models.Business;

public class AttractionListRecyclerViewAdapter extends RecyclerView.Adapter<AttractionListRecyclerViewAdapter.ViewHolder> {
    private Business[] businesses;

    public AttractionListRecyclerViewAdapter(Business[] businesses) {
        this.businesses = businesses;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relativeLayout;
        public ViewHolder(RelativeLayout relativeLayout) {
            super(relativeLayout);
            this.relativeLayout = relativeLayout;
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
        // TO DO
    }

    @Override
    public int getItemCount() {
        return businesses.length;
    }
}
