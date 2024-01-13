package com.example.memeapp.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class DynamicFragmentAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public DynamicFragmentAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = MainFragment.newInstance();
        Bundle b = new Bundle();
        b.putInt("position", position);

        frag.setArguments(b);

        return frag;
    }

    // get total number of tabs
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
