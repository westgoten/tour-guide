package com.example.rodrigo.tourguide.models;

import android.graphics.Bitmap;

public class Business {
    private String name;
    private int review_count;
    private float rating;
    private String url;
    private String image_url;
    private Bitmap businessPhoto;
    private boolean hasImageDownloadFailed;

    public Business(String name, int review_count, float rating, String url, String image_url) {
        this.name = name;
        this.review_count = review_count;
        this.rating = rating;
        this.url = url;
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public int getReview_count() {
        return review_count;
    }

    public float getRating() {
        return rating;
    }

    public String getUrl() {
        return url;
    }

    public String getImage_url() {
        return image_url;
    }

    public Bitmap getBusinessPhoto() {
        return businessPhoto;
    }

    public void setBusinessPhoto(Bitmap businessPhoto) {
        this.businessPhoto = businessPhoto;
    }

    public boolean hasImageDownloadFailed() {
        return hasImageDownloadFailed;
    }

    public void setHasImageDownloadFailed(boolean hasImageDownloadFailed) {
        this.hasImageDownloadFailed = hasImageDownloadFailed;
    }
}
