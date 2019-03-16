package com.example.rodrigo.tourguide;

import androidx.lifecycle.ViewModel;
import com.example.rodrigo.tourguide.models.Business;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class MainActivityViewModel extends ViewModel {
    private Map<AttractionListFragment.AttractionType, Business[]> businessMatrix = Collections
            .synchronizedMap(new EnumMap<AttractionListFragment.AttractionType,
                    Business[]>(AttractionListFragment.AttractionType.class));

    public Map<AttractionListFragment.AttractionType, Business[]> getBusinessMatrix() {
        return businessMatrix;
    }

    public void setBusinessMatrix(Map<AttractionListFragment.AttractionType, Business[]> businessMatrix) {
        this.businessMatrix = businessMatrix;
    }
}
