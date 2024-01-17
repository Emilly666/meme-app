package com.example.memeapp.ui.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import android.app.LocaleManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.memeapp.R;
import com.example.memeapp.SharedPreferencesManager;

public class SettingsActivity extends AppCompatActivity {

    private NumberPicker numberPickerLanguage;
    private Context context;
    private SharedPreferencesManager sharedPreferencesManager;
    private Button buttonLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        context = getApplicationContext();
        sharedPreferencesManager = SharedPreferencesManager.getInstance(context);

        initViews();
    }
    private void initViews(){
        numberPickerLanguage = findViewById(R.id.numberPickerLanguage);
        buttonLogout = findViewById(R.id.buttonLogout);
        String[] arrayString = new String[]{"English","Polski"};
        numberPickerLanguage.setMinValue(0);
        numberPickerLanguage.setMaxValue(arrayString.length-1);
        numberPickerLanguage.setValue(sharedPreferencesManager.getLanguage());
        numberPickerLanguage.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return arrayString[value];
            }
        });
        numberPickerLanguage.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                String language = "en";
                if(i1 == 0){
                    language = "en";

                } else if (i1 == 1) {
                    language = "pl";
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.getSystemService(LocaleManager.class)
                            .setApplicationLocales(LocaleList.forLanguageTags(language));
                }else{
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language));
                }
                sharedPreferencesManager.setLanguage(i1);
            }
        });
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferencesManager.setUser(null);
                sharedPreferencesManager.setToken(null);
                sharedPreferencesManager.setLogged(false);
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        getOnBackPressedDispatcher().onBackPressed(); }
                });
            }
        });
    }
}