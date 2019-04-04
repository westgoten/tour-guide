package com.example.rodrigo.tourguide.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.rodrigo.tourguide.models.Business;

public class BusinessPhotoDecodeRunnable implements Runnable {
    private Business business;
    private byte[] imgBytes;

    public BusinessPhotoDecodeRunnable(Business business, byte[] imgBytes) {
        this.business = business;
        this.imgBytes = imgBytes;
    }

    @Override
    public void run() {
        Bitmap businessPhoto = null;
        if (imgBytes != null)
            businessPhoto = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        business.setBusinessPhoto(businessPhoto);
    }
}
