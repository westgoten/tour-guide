package com.example.rodrigo.tourguide;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.rodrigo.tourguide.models.Business;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MainActivityViewModel extends ViewModel {
    private Map<AttractionListFragment.AttractionType, List<Business>> businessMatrix =
            Collections.synchronizedMap(
                    new EnumMap<AttractionListFragment.AttractionType, List<Business>>(
                            AttractionListFragment.AttractionType.class));
    private MutableLiveData<Boolean> areRequestsDone = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDatabaseEmpty = new MutableLiveData<>();
    private boolean isOffline;

    public Map<AttractionListFragment.AttractionType, List<Business>> getBusinessMatrix() {
        return businessMatrix;
    }

    public MutableLiveData<Boolean> areRequestsDone() {
        return areRequestsDone;
    }

    public MutableLiveData<Boolean> isDatabaseEmpty() {
        return isDatabaseEmpty;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean offline) {
        isOffline = offline;
    }
}
