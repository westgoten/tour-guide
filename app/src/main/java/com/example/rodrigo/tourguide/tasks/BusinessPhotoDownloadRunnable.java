package com.example.rodrigo.tourguide.tasks;

import android.graphics.Bitmap;
import android.util.Log;
import com.example.rodrigo.tourguide.models.Business;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class BusinessPhotoDownloadRunnable implements Runnable {
    private Business business;

    public BusinessPhotoDownloadRunnable(Business business) {
        this.business = business;
    }

    @Override
    public void run() {
        String imagePath = business.getImage_url();
        imagePath = imagePath.substring(0, imagePath.length()-5) + "l.jpg";
        try {
            Bitmap businessPhoto = Picasso.get().load(imagePath)
                    .placeholder(android.R.color.black)
                    .error(android.R.color.holo_red_light)
                    .get();
            business.setBusinessPhoto(businessPhoto);
        } catch (IOException e) {
            Log.d("BusinessPhotoRequest", e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}
