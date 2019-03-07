package com.example.rodrigo.tourguide;

import com.example.rodrigo.tourguide.models.BusinessSearch;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface YelpService {
    String BASE_URL = "https://api.yelp.com/v3";

    @GET("/businesses/search?location=Salvador, Bahia")
    Call<BusinessSearch> getBusinessSearch(@Header("Authorization") String header, @Query("term") String searchTerm,
                                           @Query("categories") String category, @Query("sort_by") String mode,
                                           @Query("locale") String language, @Query("limit") int limitOfBusinessResults);
}
