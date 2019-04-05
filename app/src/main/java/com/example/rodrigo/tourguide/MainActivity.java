package com.example.rodrigo.tourguide;

import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.example.rodrigo.tourguide.database.BusinessListsContract;
import com.example.rodrigo.tourguide.database.BusinessListsDbHelper;
import com.example.rodrigo.tourguide.models.BusinessSearch;
import com.example.rodrigo.tourguide.tasks.BusinessManager;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private MainActivityViewModel viewModel;

    private ViewPager viewPager;
    private LinearLayout loading;
    private LinearLayout offlineMessage;
    private boolean wasTryAgainButtonPressed;

    private static final int NUM_OF_TABS = 4;
    private static final String SORT_SEARCH_BY = "rating";
    private static final int LIMIT_OF_BUSINESS_RESULTS = 20;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.pager);
        loading = findViewById(R.id.loading);
        offlineMessage = findViewById(R.id.offline);

        wasTryAgainButtonPressed = false;
        Button tryAgainButton = (Button) offlineMessage.getChildAt(2);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wasTryAgainButtonPressed = true;
                offlineMessage.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                initializeHttpRequests();
            }
        });

        viewModel.isDatabaseEmpty().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (savedInstanceState == null && viewModel.areRequestsDone().getValue() == null) {
                    if (aBoolean /*|| isUpdateDataTime() [TODO]*/) {
                        initializeHttpRequests();
                    } else {
                        BusinessManager.getInstance().fetchBusinessListsFromDatabase(viewModel, getApplicationContext());
                    }
                }
            }
        });

        viewModel.areRequestsDone().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    if (!viewModel.isOffline() || !viewModel.isDatabaseEmpty().getValue()) {
                        AttractionsViewPagerAdapter pagerAdapter = new AttractionsViewPagerAdapter(getSupportFragmentManager());
                        viewPager.setAdapter(pagerAdapter);

                        loading.setVisibility(View.GONE);
                        offlineMessage.setVisibility(View.GONE);
                        viewPager.setVisibility(View.VISIBLE);

                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            ActionBar actionBar = getSupportActionBar();
                            if (actionBar != null)
                                actionBar.hide();
                        }
                    } else {
                        if (savedInstanceState == null || wasTryAgainButtonPressed) {
                            loading.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loading.setVisibility(View.GONE);
                                    offlineMessage.setVisibility(View.VISIBLE);
                                }
                            }, 1000);
                        } else {
                            loading.setVisibility(View.GONE);
                            offlineMessage.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        if (savedInstanceState == null)
            verifyIfDatabaseIsEmpty();
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

    @Override
    protected void onDestroy() {
        if (!BusinessListsDbHelper.isInstanceNull()) {
            BusinessListsDbHelper.getInstance(this).close();
            BusinessListsDbHelper.resetInstance();
        }
        super.onDestroy();
    }

    private void initializeHttpRequests() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YelpService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        YelpService yelpService = retrofit.create(YelpService.class);

        BusinessManager businessManager = BusinessManager.getInstance();

        viewModel.areRequestsDone().setValue(false);
        viewModel.setOffline(false);

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

        businessManager.keepTrackOfRequests(viewModel, this);
    }

    private void verifyIfDatabaseIsEmpty() {
        final BusinessListsDbHelper dbHelper = BusinessListsDbHelper.getInstance(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                Cursor cursor = db.rawQuery("SELECT * FROM " + BusinessListsContract.BusinessListsEntry.TABLE_NAME +
                        0 + " LIMIT 1", null);
                int count = cursor.getCount();
                cursor.close();

                if (count == 0)
                    viewModel.isDatabaseEmpty().postValue(true);
                else
                    viewModel.isDatabaseEmpty().postValue(false);
            }
        }).start();
    }
}
