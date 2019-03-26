package com.example.rodrigo.tourguide;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rodrigo.tourguide.models.Business;

import java.util.List;

public class AttractionListFragment extends Fragment {
    private int position;

    public enum AttractionType {LANDMARK(R.string.term_landmarks),
        RESTAURANT(R.string.term_restaurants),
        BEACH(R.string.term_beaches),
        BAR(R.string.term_bars);

        private int stringResId;

        AttractionType(int stringResId) {
            this.stringResId = stringResId;
        }

        public int getStringResId() {
            return stringResId;
        }
    }

    public static final String ARG_POSITION = "position";
    private static final String TAG = "AttractionListFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        position = (args != null) ? args.getInt(ARG_POSITION) : 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivityViewModel viewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_attraction_list, container, false);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Business> businesses = null;
        for (AttractionType attractionType : AttractionType.values()) {
            if (attractionType.ordinal() == position) {
                businesses = viewModel.getBusinessMatrix().get(attractionType);
                break;
            }
        }

        if (businesses != null) {
            AttractionListRecyclerViewAdapter viewAdapter = new AttractionListRecyclerViewAdapter(businesses, getContext());
            recyclerView.setAdapter(viewAdapter);
        } else {
            Log.d(TAG, "Businesses data set is null");
        }

        return recyclerView;
    }
}
