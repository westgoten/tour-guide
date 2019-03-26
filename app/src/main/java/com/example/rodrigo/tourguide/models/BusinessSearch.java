package com.example.rodrigo.tourguide.models;

import java.util.List;

public class BusinessSearch {
    private List<Business> businesses;

    public BusinessSearch(List<Business> businesses) {
        this.businesses = businesses;
    }

    public List<Business> getBusinesses() {
        return businesses;
    }
}
