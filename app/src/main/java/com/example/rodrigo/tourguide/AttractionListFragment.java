package com.example.rodrigo.tourguide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class AttractionListFragment extends Fragment {
    private int position;

    public enum AttractionType {LANDMARK, RESTAURANT, BEACH, BAR}

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
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_attraction_list, container, false);

        return recyclerView;
    }
}
