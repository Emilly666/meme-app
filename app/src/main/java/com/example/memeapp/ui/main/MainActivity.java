package com.example.memeapp.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.memeapp.R;
import com.example.memeapp.SharedPreferencesManager;
import com.example.memeapp.model.Tag.Tag;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout mTabLayout;
    private List<Tag> userSavedTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);

        SharedPreferencesManager sp = SharedPreferencesManager.getInstance(getApplicationContext());

        Tag tag = new Tag();
        tag.setName("ooooooooooooooooooooooooooooooooooooooooooooooooooooo");
        tag.setId((long)77);
        //sp.addUserSavedTags(tag);

        userSavedTags = sp.getUserSavedTags();

        initViews();
    }

    private void initViews() {

        // initialise the layout
        viewPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tabs);

        // setOffscreenPageLimit means number
        // of tabs to be shown in one page
        viewPager.setOffscreenPageLimit(5);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // setCurrentItem as the tab position
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setDynamicFragmentToTabLayout();
    }

    private void setDynamicFragmentToTabLayout() {

        mTabLayout.addTab(mTabLayout.newTab().setText("Main"));

        userSavedTags.forEach(tag -> {
            mTabLayout.addTab(mTabLayout.newTab().setText(tag.getName()));
        });

        DynamicFragmentAdapter mDynamicFragmentAdapter = new DynamicFragmentAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());

        // set the adapter
        viewPager.setAdapter(mDynamicFragmentAdapter);

        // set the current item as 0 (when app opens for first time)
        viewPager.setCurrentItem(0);
    }
}