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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import com.example.rodrigo.tourguide.models.BusinessSearch;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    MainActivityViewModel viewModel;

    private static final int NUM_OF_TABS = 4;
    public static final String SORT_SEARCH_BY = "rating";
    public static final int LIMIT_OF_BUSINESS_RESULTS = 20;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        viewModel.getAreRequestsDone().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    AttractionsViewPagerAdapter pagerAdapter = new AttractionsViewPagerAdapter(getSupportFragmentManager());
                    ViewPager viewPager = findViewById(R.id.pager);
                    viewPager.setAdapter(pagerAdapter);
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null)
                actionBar.hide();
        }

        if (savedInstanceState == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(YelpService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            YelpService yelpService = retrofit.create(YelpService.class);
            initializeHttpRequests(yelpService);
        }
    }

    private void initializeHttpRequests(YelpService yelpService) {
        BusinessManager businessManager = BusinessManager.getInstance();

        viewModel.getAreRequestsDone().setValue(false);

        Call<BusinessSearch> businessSearchCall1 = yelpService.getBusinessSearch(getString(R.string.request_header),
                getString(R.string.term_landmarks), null, SORT_SEARCH_BY, getString(R.string.language),
                LIMIT_OF_BUSINESS_RESULTS);
        businessManager.startBusinessListDownload(businessSearchCall1,
                AttractionListFragment.AttractionType.LANDMARK, viewModel);

        Call<BusinessSearch> businessSearchCall2 = yelpService.getBusinessSearch(getString(R.string.request_header),
                getString(R.string.term_restaurants), getString(R.string.restaurant_category), SORT_SEARCH_BY,
                getString(R.string.language), LIMIT_OF_BUSINESS_RESULTS);
        businessManager.startBusinessListDownload(businessSearchCall2,
                AttractionListFragment.AttractionType.RESTAURANT, viewModel);

        Call<BusinessSearch> businessSearchCall3 = yelpService.getBusinessSearch(getString(R.string.request_header),
                getString(R.string.term_beaches), null, SORT_SEARCH_BY, getString(R.string.language),
                LIMIT_OF_BUSINESS_RESULTS);
        businessManager.startBusinessListDownload(businessSearchCall3,
                AttractionListFragment.AttractionType.BEACH, viewModel);

        Call<BusinessSearch> businessSearchCall4 = yelpService.getBusinessSearch(getString(R.string.request_header),
                getString(R.string.term_bars), null, SORT_SEARCH_BY, getString(R.string.language),
                LIMIT_OF_BUSINESS_RESULTS);
        businessManager.startBusinessListDownload(businessSearchCall4,
                AttractionListFragment.AttractionType.BAR, viewModel);

        businessManager.keepTrackOfRequests(viewModel);
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
            for (AttractionListFragment.AttractionType attractionType : AttractionListFragment.AttractionType.values()) {
                if (attractionType.ordinal() == position) {
                    return getString(attractionType.getStringResId());
                }
            }

            return null;
        }
    }
}
