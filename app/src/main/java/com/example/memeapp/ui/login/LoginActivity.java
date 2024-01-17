package com.example.memeapp.ui.login;

import static com.example.memeapp.ui.main.MainActivity.JSON;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memeapp.R;
import com.example.memeapp.SharedPreferencesManager;
import com.example.memeapp.dto.AuthenticationResponse;
import com.example.memeapp.model.user.User;
import com.example.memeapp.ui.register.RegisterActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private OkHttpClient client;
    private EditText editTextEmailAddress, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister, textViewLoginError;
    private SharedPreferencesManager sharedPreferencesManager;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();
        sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
        client = new OkHttpClient();

        initViews();
    }
    public void initViews(){
        editTextEmailAddress = findViewById(R.id.editTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        textViewLoginError = findViewById(R.id.textViewLoginError);

        buttonLogin.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override public void run() { textViewLoginError.setText(""); }
                });
                if(checkLoginFields()){
                    login();
                }
            }
        });
        ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == 1) {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                Toast.makeText(context, "successful", Toast.LENGTH_SHORT).show();
                                getOnBackPressedDispatcher().onBackPressed(); }
                        });
                    }
                });
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(context, RegisterActivity.class);
                activityResultLaunch.launch(intent);
            }
        });
    }
    private void login(){
        String json = "{ " +
                    "\"email\" : \"" + editTextEmailAddress.getText().toString() + "\", " +
                    "\"password\" : \"" + editTextPassword.getText().toString() +"\" " +
                "}";
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(sharedPreferencesManager.getServerAddress() + "auth/login")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) { e.printStackTrace(); }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.code() != 200){
                    runOnUiThread(new Runnable() {
                        @Override public void run() { textViewLoginError.setText(R.string.login_error); }
                    });
                }
                else{
                    Gson gson = new Gson();
                    try{
                        AuthenticationResponse authenticationResponse = gson.fromJson(response.body().string(), new TypeToken<AuthenticationResponse>() {}.getType());
                        User user = new User(authenticationResponse, editTextPassword.getText().toString());

                        sharedPreferencesManager.setUser(user);
                        sharedPreferencesManager.setToken(authenticationResponse.getToken());
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                Toast.makeText(context, R.string.login_successful, Toast.LENGTH_SHORT).show();
                                sharedPreferencesManager.setLogged(true);
                                getOnBackPressedDispatcher().onBackPressed(); }
                        });
                    }
                    catch (Exception e){
                        runOnUiThread(new Runnable() {
                            @Override public void run() { textViewLoginError.setText(R.string.login_error); }
                        });
                    }
                }
            }
        });
    }
    private boolean checkLoginFields(){
        boolean flag = true;
        if (editTextEmailAddress.length() == 0) {
            editTextEmailAddress.setError(getResources().getString(R.string.field_required));
            flag = false;
        }
        if (editTextPassword.length() == 0) {
            editTextPassword.setError(getResources().getString(R.string.field_required));
            flag = false;
        } else if (editTextPassword.length() < 3) { // TODO change back to 8
            editTextPassword.setError(getResources().getString(R.string.password_minimum));
            flag = false;
        }
        return flag;
    }
}