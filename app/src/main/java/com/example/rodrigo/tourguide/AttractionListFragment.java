package com.example.rodrigo.tourguide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AttractionListFragment extends Fragment {
    private int position;

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
        ImageView imageView = (ImageView) inflater.inflate(R.layout.fragment_attraction_list, container, false);

        if (position == 0) {
            imageView.setImageResource(R.drawable.before);
        } else {
            imageView.setImageResource(R.drawable.after);
        }

        return imageView;
    }
}
