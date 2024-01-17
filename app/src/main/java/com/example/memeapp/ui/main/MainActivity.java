package com.example.memeapp.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.memeapp.R;
import com.example.memeapp.SharedPreferencesManager;
import com.example.memeapp.dto.AuthenticationResponse;
import com.example.memeapp.model.tag.Tag;
import com.example.memeapp.model.user.User;
import com.example.memeapp.ui.addmeme.AddMeme;
import com.example.memeapp.ui.login.LoginActivity;
import com.example.memeapp.ui.profile.UserProfile;
import com.example.memeapp.ui.settings.SettingsActivity;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout mTabLayout;
    private List<Tag> userSavedTags = new ArrayList<>();
    private NavigationBarView bottomNavigation;
    private SharedPreferencesManager sharedPreferencesManager;
    public static final MediaType JSON = MediaType.get("application/json");
    private Context context;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);

        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        TimeZone.setDefault(utcTimeZone);

        context = getApplicationContext();
        sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
        client = new OkHttpClient();


        if(sharedPreferencesManager.getUserSavedTags() == null){
            sharedPreferencesManager.addUserSavedTags(new Tag(0,"MAIN"));
        }

        userSavedTags = sharedPreferencesManager.getUserSavedTags();

        if(sharedPreferencesManager.isLogged() && sharedPreferencesManager.getUser() != null){
            Log.d("sdfsd", sharedPreferencesManager.getUser().getPassword());
            authenticate();
        }



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

        if(userSavedTags != null){
            userSavedTags.forEach(tag -> {
                mTabLayout.addTab(mTabLayout.newTab().setText(tag.getName()));
            });
        }

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
                    Intent myIntent;
                    if(sharedPreferencesManager.isLogged()){
                        myIntent = new Intent(MainActivity.this, UserProfile.class);
                    }
                    else{
                        myIntent = new Intent(MainActivity.this, LoginActivity.class);
                    }
                    MainActivity.this.startActivity(myIntent);
                    return true;
                }
                else if (menuItemId ==  R.id.page_add) {
                    if(sharedPreferencesManager.isLogged()){
                        Intent myIntent = new Intent(MainActivity.this, AddMeme.class);
                        MainActivity.this.startActivity(myIntent);
                        return true;
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Log in to add meme", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
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
    private void authenticate(){
        String json = "{ " +
                "\"email\" : \"" + sharedPreferencesManager.getUser().getEmail() + "\", " +
                "\"password\" : \"" + sharedPreferencesManager.getUser().getPassword() +"\" " +
                "}";
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(sharedPreferencesManager.getServerAddress() + "auth/login")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() != 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "auth fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Gson gson = new Gson();
                    AuthenticationResponse authenticationResponse = gson.fromJson(response.body().string(), new TypeToken<AuthenticationResponse>() {
                    }.getType());
                    User user = new User(authenticationResponse, sharedPreferencesManager.getUser().getPassword());

                    sharedPreferencesManager.setUser(user);
                    sharedPreferencesManager.setToken(authenticationResponse.getToken());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "auth success", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}