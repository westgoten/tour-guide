package com.example.rodrigo.tourguide.models;

public class BusinessSearch {
    private Business[] businesses;

    public BusinessSearch(Business[] businesses) {
        this.businesses = businesses;
    }

    public Business[] getBusinesses() {
        return businesses;
    }
}
