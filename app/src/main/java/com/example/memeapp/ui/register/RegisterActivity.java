package com.example.memeapp.ui.register;

import static com.example.memeapp.ui.main.MainActivity.JSON;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memeapp.R;
import com.example.memeapp.SharedPreferencesManager;
import com.example.memeapp.model.AuthenticationResponse;
import com.example.memeapp.model.user.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private OkHttpClient client;
    private EditText editTextNickname, editTextEmailAddress, editTextPassword, editTextPassword2;
    private Button buttonRegister;
    private TextView textViewRegisterError;
    private SharedPreferencesManager sharedPreferencesManager;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = getApplicationContext();
        sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
        client = new OkHttpClient();

        initViews();
    }
    public void initViews(){
        editTextNickname = findViewById(R.id.editTextNickname);
        editTextEmailAddress = findViewById(R.id.editTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPassword2 = findViewById(R.id.editTextPassword2);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewRegisterError = findViewById(R.id.textViewRegisterError);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override public void run() { textViewRegisterError.setText(""); }
                });
                if(checkRegisterFields()){
                    register();
                }
            }
        });
    }
    private void register(){
        String json = "{ " +
                    "\"nickname\" : \"" + editTextNickname.getText().toString() + "\", " +
                    "\"email\" : \"" + editTextEmailAddress.getText().toString() + "\", " +
                    "\"password\" : \"" + editTextPassword.getText().toString() +"\" " +
                "}";
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(sharedPreferencesManager.getServerAddress() + "auth/register")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) { e.printStackTrace(); }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.code() != 200){
                    runOnUiThread(new Runnable() {
                        @Override public void run() { textViewRegisterError.setText(R.string.login_error); }
                    });
                }
                else{

                    Gson gson = new Gson();
                    AuthenticationResponse authenticationResponse = null;
                    String responseBody = response.body().string();
                    try{
                        authenticationResponse = gson.fromJson(responseBody, new TypeToken<AuthenticationResponse>() {}.getType());

                    }catch (Exception e){
                        Log.d("ddd",e.toString());
                        Log.d("ddd",responseBody);
                        if(responseBody.equals("Email already used")){
                            runOnUiThread(new Runnable() {
                                @Override public void run() { textViewRegisterError.setText(R.string.email_taken); }
                            });
                        }
                        return;
                    }
                    User user = new User(authenticationResponse);

                    sharedPreferencesManager.setUser(user);
                    sharedPreferencesManager.setToken(authenticationResponse.getToken());
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            Toast.makeText(context, R.string.register_successful, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            setResult(1, intent);
                            finish();
                            }
                    });
                }
            }
        });
    }
    private boolean checkRegisterFields(){
        boolean flag = true;

        if (editTextNickname.length() == 0) {
            editTextNickname.setError(getResources().getString(R.string.field_required));
            flag = false;
        }

        if (editTextEmailAddress.length() == 0) {
            editTextEmailAddress.setError(getResources().getString(R.string.field_required));
            flag = false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(editTextEmailAddress.getText().toString()).matches()){
            editTextEmailAddress.setError(getResources().getString(R.string.email_invalid));
            flag = false;
        }
        if (editTextPassword.length() == 0) {
            editTextPassword.setError(getResources().getString(R.string.field_required));
            flag = false;
        } else if (editTextPassword.length() < 3) { // TODO change back to 8
            editTextPassword.setError(getResources().getString(R.string.password_minimum));
            flag = false;
        } else if(!isValidPassword(editTextPassword.getText().toString())){
            editTextPassword.setError(getResources().getString(R.string.password_weak));
            flag = false;
        }

        if (editTextPassword2.length() == 0) {
            editTextPassword2.setError(getResources().getString(R.string.field_required));
            flag = false;
        }else if (!editTextPassword.getText().toString().equals(editTextPassword2.getText().toString())) {
            editTextPassword2.setError(getResources().getString(R.string.password_not_matching));
            flag = false;
        }
        return flag;
    }
    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }
}