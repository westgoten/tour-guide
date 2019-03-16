package com.example.rodrigo.tourguide.tasks;

import android.util.Log;
import com.example.rodrigo.tourguide.AttractionListFragment;
import com.example.rodrigo.tourguide.MainActivityViewModel;
import com.example.rodrigo.tourguide.models.Business;
import com.example.rodrigo.tourguide.models.BusinessSearch;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class BusinessListDownloadRunnable implements Runnable {
    private Call<BusinessSearch> businessSearchCall;
    private AttractionListFragment.AttractionType attractionType;
    private MainActivityViewModel viewModel;

    public BusinessListDownloadRunnable(Call<BusinessSearch> businessSearchCall,
                                        AttractionListFragment.AttractionType attractionType,
                                        MainActivityViewModel viewModel) {
        this.businessSearchCall = businessSearchCall;
        this.attractionType = attractionType;
        this.viewModel = viewModel;
    }

    @Override
    public void run() {
        try {
            Response<BusinessSearch> response = businessSearchCall.execute();
            if (response.isSuccessful()) {
                BusinessSearch businessSearch = response.body();
                Business[] businesses = businessSearch.getBusinesses();
                viewModel.getBusinessMatrix().put(attractionType, businesses);
            } else {
                Log.d("BusinessSearchResponse", response.code() + " - " + response.message());
            }
        } catch (IOException e) {
            Log.d("BusinessSearchCall", e.getClass().getSimpleName() + " - " + e.getMessage());
        } catch (RuntimeException e) {
            Log.d("BusinessSearchCall", e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}
