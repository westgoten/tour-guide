package com.example.rodrigo.tourguide;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    private static final int NUM_OF_TABS = 2;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AttractionsViewPagerAdapter pagerAdapter = new AttractionsViewPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
    }

    private class AttractionsViewPagerAdapter extends FragmentPagerAdapter {
        public AttractionsViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

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
    }
}
