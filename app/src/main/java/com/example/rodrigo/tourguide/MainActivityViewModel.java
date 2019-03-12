package com.example.rodrigo.tourguide;

import androidx.lifecycle.ViewModel;
import com.example.rodrigo.tourguide.models.Business;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {
    private List<Business[]> businessMatrix = new ArrayList<>();

    public List<Business[]> getBusinessMatrix() {
        return businessMatrix;
    }

    public void setBusinessMatrix(List<Business[]> businessMatrix) {
        this.businessMatrix = businessMatrix;
    }
}
