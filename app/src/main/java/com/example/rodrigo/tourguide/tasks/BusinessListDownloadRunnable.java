package com.example.rodrigo.tourguide.tasks;

import android.util.Log;
import com.example.rodrigo.tourguide.MainActivityViewModel;
import com.example.rodrigo.tourguide.models.Business;
import com.example.rodrigo.tourguide.models.BusinessSearch;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class BusinessListDownloadRunnable implements Runnable {
    private Call<BusinessSearch> businessSearchCall;
    private MainActivityViewModel viewModel;
    private int attractionType;

    public BusinessListDownloadRunnable(Call<BusinessSearch> businessSearchCall, MainActivityViewModel viewModel,
                                        int attractionType) {
        this.businessSearchCall = businessSearchCall;
        this.viewModel = viewModel;
        this.attractionType = attractionType;
    }

    @Override
    public void run() {
        try {
            Response<BusinessSearch> response = businessSearchCall.execute();
            if (response.isSuccessful()) {
                BusinessSearch businessSearch = response.body();
                Business[] businesses = businessSearch.getBusinesses();
                viewModel.getBusinessMatrix().add(attractionType, businesses);
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
