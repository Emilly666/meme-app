package com.example.memeapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.memeapp.R;
import com.example.memeapp.SharedPreferencesManager;
import com.example.memeapp.model.tag.Tag;
import com.example.memeapp.ui.addmeme.AddMeme;
import com.example.memeapp.ui.profile.UserProfile;
import com.example.memeapp.ui.settings.SettingsActivity;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout mTabLayout;
    private List<Tag> userSavedTags = new ArrayList<>();
    private NavigationBarView bottomNavigation;

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

        viewPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tabs);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        viewPager.setOffscreenPageLimit(5);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        bottomNavigation.setSelectedItemId(R.id.page_add);
        addOnTabSelectedListener();
        setDynamicFragmentToTabLayout();
        setOnItemSelectedListener();
    }

    private void setDynamicFragmentToTabLayout() {

        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.category_name_main));

        userSavedTags.forEach(tag -> {
            mTabLayout.addTab(mTabLayout.newTab().setText(tag.getName()));
        });

        DynamicFragmentAdapter mDynamicFragmentAdapter = new DynamicFragmentAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());

        // set the adapter
        viewPager.setAdapter(mDynamicFragmentAdapter);

        // set the current item as 0 (when app opens for first time)
        viewPager.setCurrentItem(0);
    }
    private void setOnItemSelectedListener(){
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int menuItemId =  item.getItemId();

                if (menuItemId ==  R.id.page_profile) {
                    Intent myIntent = new Intent(MainActivity.this, UserProfile.class);
                    //myIntent.putExtra("key", 1); //Optional parameters
                    MainActivity.this.startActivity(myIntent);
                    return true;
                }
                else if (menuItemId ==  R.id.page_add) {
                    Intent myIntent = new Intent(MainActivity.this, AddMeme.class);
                    //myIntent.putExtra("key", 1); //Optional parameters
                    MainActivity.this.startActivity(myIntent);
                    return true;
                }
                else if (menuItemId ==  R.id.page_settings) {
                    Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    //myIntent.putExtra("key", 1); //Optional parameters
                    MainActivity.this.startActivity(myIntent);
                    return true;
                }
                return true;
            }
        });
    }
    private void addOnTabSelectedListener(){
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
    }
}