package com.example.rodrigo.tourguide;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    MainActivityViewModel viewModel;

    private static final int NUM_OF_TABS = 4;
    public static final String SORT_SEARCH_BY = "rating";
    public static final int LIMIT_OF_BUSINESS_RESULTS = 10;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null)
                actionBar.hide();
        }

        if (savedInstanceState == null) {
            // TO DO: HTTP Requests
        }

        AttractionsViewPagerAdapter pagerAdapter = new AttractionsViewPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
    }

    private void keepTrackOfRequests() { // TO DO
        new Thread(new Runnable() {
            @Override
            public void run() {
                BusinessManager sInstance = BusinessManager.getInstance();

                boolean running = true;
                while (running) {
                    if (sInstance.getBusinessListThreadPoolCompletedTasks() == AttractionListFragment.AttractionType
                            .values().length)
                        running = false;
                    // Shutdown ThreadPoolExecutor
                }
                sInstance.startBusinessPhotoDownload(viewModel);

                running = true;
                while (running) {
                    // totalBusinesses == completedTasks
                }
                // Do something to indicate that the data requests are finished
            }
        }).start();
    }

    private class AttractionsViewPagerAdapter extends FragmentPagerAdapter {
        public AttractionsViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new AttractionListFragment();
            Bundle args = new Bundle();
            args.putInt(AttractionListFragment.ARG_POSITION, position);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_OF_TABS;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return "tab " + position;
        }
    }
}
