package com.example.rodrigo.tourguide.tasks;

import com.example.rodrigo.tourguide.R;
import android.graphics.Bitmap;
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
        if (imagePath.length() > 0) {
            imagePath = imagePath.substring(0, imagePath.length() - 5) + "l.jpg";
            try {
                Bitmap businessPhoto = Picasso.get().load(imagePath).get();
                business.setBusinessPhoto(businessPhoto);
            } catch (IOException e) {
                business.setBusinessPhoto(null);
                business.setHasImageDownloadFailed(true);
            }
        } else {
            business.setBusinessPhoto(null);
            business.setHasImageDownloadFailed(false);
        }
    }
}
