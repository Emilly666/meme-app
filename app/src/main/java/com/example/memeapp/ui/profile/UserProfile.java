package com.example.memeapp.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.memeapp.R;
import com.example.memeapp.SharedPreferencesManager;
import com.example.memeapp.dto.GetCommentResponse;
import com.example.memeapp.dto.UserProfileResponse;
import com.example.memeapp.model.comment.MemeComment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserProfile extends AppCompatActivity {

    private Context context;
    private SharedPreferencesManager sharedPreferencesManager;
    private OkHttpClient client;
    private TextView textViewUserProfileNickname, textView8, textView9, textView10, textView11, textView12, textView13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        context = getApplicationContext();
        sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
        client = new OkHttpClient();

        initViews();

        getUserProfile();
    }
    private void initViews(){
        textViewUserProfileNickname = findViewById(R.id.textViewUserProfileNickname);
        textView8 = findViewById(R.id.textView8);
        textView9 = findViewById(R.id.textView9);
        textView10 = findViewById(R.id.textView10);
        textView11 = findViewById(R.id.textView11);
        textView12 = findViewById(R.id.textView12);
        textView13 = findViewById(R.id.textView13);
    }
    private void getUserProfile(){
        Intent intent = getIntent();

        Request request = new Request.Builder()
                .url(sharedPreferencesManager.getServerAddress() + "users/" + intent.getIntExtra("user_id", 0))
                .get()
                .addHeader("Authorization", "Bearer " + sharedPreferencesManager.getToken())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                String responseString = response.body().string();
                if(responseString.equals("User not found")){
                    return;
                }
                Gson gson = new Gson();
                UserProfileResponse userProfileResponse = gson.fromJson(responseString, new TypeToken<UserProfileResponse>() {}.getType());

                textViewUserProfileNickname.setText(userProfileResponse.getNickname());
                textView8.setText(String.valueOf(userProfileResponse.getTotalMemesUploaded()));
                textView9.setText(String.valueOf(userProfileResponse.getTotalLikesGiven()));
                textView10.setText(String.valueOf(userProfileResponse.getTotalDislikesGiven()));
                textView11.setText(String.valueOf(userProfileResponse.getTotalLikesReceived()));
                textView12.setText(String.valueOf(userProfileResponse.getTotalDislikesReceived()));
                textView13.setText(String.valueOf(userProfileResponse.getTotalComments()));

            }
        });
    }
}