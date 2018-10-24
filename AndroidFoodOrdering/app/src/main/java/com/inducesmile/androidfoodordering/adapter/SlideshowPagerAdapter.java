package com.inducesmile.androidfoodordering.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.inducesmile.androidfoodordering.Fragment.DealsFragment;

public class SlideshowPagerAdapter extends FragmentStatePagerAdapter{

    private static final String TAG = SlideshowPagerAdapter.class.getSimpleName();

    private static final int DEAL_NUMBER = 4;


    public SlideshowPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new DealsFragment();
    }

    @Override
    public int getCount() {
        return DEAL_NUMBER;
    }
}
