package com.example.memeapp.ui.comment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memeapp.R;
import com.example.memeapp.SharedPreferencesManager;
import com.example.memeapp.dto.AuthenticationResponse;
import com.example.memeapp.dto.GetCommentResponse;
import com.example.memeapp.model.comment.MemeComment;
import com.example.memeapp.model.meme.Meme;
import com.example.memeapp.model.user.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentActivity extends AppCompatActivity {
    private LinearLayout linearLayoutComments;
    private EditText editTextTextMultiLine2;
    private Button buttonAddComment;
    private Context context;
    private SharedPreferencesManager sharedPreferencesManager;
    private OkHttpClient client;
    private List<MemeComment> memeCommentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        context = getApplicationContext();
        sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
        client = new OkHttpClient();

        initViews();

        getComments();


    }

    private void initViews(){
        linearLayoutComments = findViewById(R.id.linearLayoutComments);
        editTextTextMultiLine2 = findViewById(R.id.editTextTextMultiLine2);
        buttonAddComment = findViewById(R.id.buttonAddComment);

        if(!sharedPreferencesManager.isLogged()){
            editTextTextMultiLine2.setVisibility(View.INVISIBLE);
            buttonAddComment.setVisibility(View.INVISIBLE);
        }
        buttonAddComment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.getIntExtra("meme_id", 0);
                String json = "{ " +
                        "\"meme_id\" : \"" + intent.getIntExtra("meme_id", 0) + "\", " +
                        "\"comment\" : \"" + editTextTextMultiLine2.getText().toString() + "\" " +
                        "}";
                RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
                Request request = new Request.Builder()
                        .url(sharedPreferencesManager.getServerAddress() + "comment/add")
                        .post(body)
                        .addHeader("Authorization", "Bearer " + sharedPreferencesManager.getToken())
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {


                    }
                });
            }
        });
    }
    private void getComments(){
        Intent intent = getIntent();
        intent.getIntExtra("meme_id", 0);
        String json = "{ " +
                "\"meme_id\" : \"" + intent.getIntExtra("meme_id", 0) + "\" " +
                "}";
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(sharedPreferencesManager.getServerAddress() + "comment/get")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {


                Gson gson = new Gson();
                GetCommentResponse getCommentResponse = gson.fromJson(response.body().string(), new TypeToken<GetCommentResponse>() {}.getType());


                memeCommentList.addAll(getCommentResponse.getCommentsList());

                if(memeCommentList.isEmpty()){
                    TextView textView = new TextView(context);
                    textView.setText("No comments");
                    linearLayoutComments.addView(textView);
                }
                for (MemeComment memeComment: memeCommentList) {
                    TextView textView = new TextView(context);
                    textView.setText(memeComment.getAuthorNickname() + " "+ hoursDifference(memeComment.getAdd_timestamp()) + "\n"+ memeComment.getComment() + "\n");
                    linearLayoutComments.addView(textView);
                }
            }
        });
    }

    private static String hoursDifference(Timestamp addDate) {
        LocalDateTime date1 = LocalDateTime.now();
        LocalDateTime date2 = addDate.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
        long diffInMin = ChronoUnit.MINUTES.between(date2, date1);
        if (diffInMin < 60) {
            return diffInMin + "min";
        } else if ((diffInMin / 60) < 24) {
            return ChronoUnit.HOURS.between(date2, date1) + "h";
        } else {
            return ChronoUnit.DAYS.between(date2, date1) + "d";
        }
    }
}