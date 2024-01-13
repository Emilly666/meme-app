package com.example.memeapp.ui.addmeme;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.memeapp.ui.main.MainActivity.JSON;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.memeapp.R;
import com.example.memeapp.SharedPreferencesManager;
import com.example.memeapp.dto.AuthenticationResponse;
import com.example.memeapp.model.user.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddMeme extends AppCompatActivity {
    private int PICKFILE_RESULT_CODE;
    private Button chooseFileButton, addMemeButton, addTagButton;
    private TextView authorTextView, dateTextView, titleTextView;
    private LinearLayout add_tags_layout;
    private EditText editTextTitle;
    private Uri uri = null;
    private ImageView imagePreview;
    private Context context;
    private OkHttpClient client;
    private SharedPreferencesManager sharedPreferencesManager;
    private boolean imageSelected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meme);
        context = getApplicationContext();
        client = new OkHttpClient();
        sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
        initViews();
    }
    private void initViews(){
        chooseFileButton =findViewById(R.id.chooseFileButton);
        imagePreview = findViewById(R.id.imageViewMeme);
        addMemeButton = findViewById(R.id.addMemeButton);
        titleTextView = findViewById(R.id.title);
        authorTextView = findViewById(R.id.author);
        dateTextView = findViewById(R.id.date);
        editTextTitle = findViewById(R.id.editTextTitle);
        add_tags_layout = findViewById(R.id.add_tags_layout);
        addTagButton = findViewById(R.id.button_add_tag);

        titleTextView.setText(R.string.title);
        authorTextView.setText(sharedPreferencesManager.getUser().getNickname());
        dateTextView.setText("");
        chooseFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                String [] mimeTypes = {"image/png", "image/gif"};
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(intent, PICKFILE_RESULT_CODE);
            }
        });
        addMemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = true;
                if(!imageSelected){
                    flag = false;
                    runOnUiThread(new Runnable() {
                        @Override public void run() { Toast.makeText(context, "Please select file", Toast.LENGTH_SHORT).show(); }
                    });
                }
                if(editTextTitle.getText().toString().equals("")){
                    flag = false;
                    runOnUiThread(new Runnable() {
                        @Override public void run() { Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show(); }
                    });
                }
                if(add_tags_layout.getChildCount() == 1){
                    flag = false;
                    runOnUiThread(new Runnable() {
                        @Override public void run() { Toast.makeText(context, "Please add at least 1 tag", Toast.LENGTH_SHORT).show(); }
                    });
                }
                if(flag){
                    addMeme();
                }

            }
        });
        editTextTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                titleTextView.setText(editable);
            }
        });
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = new EditText(context);
                add_tags_layout.addView(editText);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK && data != null) {
            uri = data.getData();

            Glide
                    .with(context)
                    .load(uri)
                    .into(imagePreview);
            imageSelected = true;
        }
    }
    private void addMeme(){
        List<String> list = new ArrayList<>();
        for(int i = 1; i < add_tags_layout.getChildCount();i++){
            EditText editText = (EditText) add_tags_layout.getChildAt(i);
            list.add(String.valueOf(editText.getText()));
        }
        Gson gson = new Gson();
        String jsonText = gson.toJson(list);

        File file;
        try {
            file = FileUtil.from(AddMeme.this, uri);
            Log.d("file", "File...:::: uti - "+file .getPath()+" file -" + file + " : " + file .exists());

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(),
                        RequestBody.create(MediaType.parse(getMimeType(uri)), file))
                .addFormDataPart("title", editTextTitle.getText().toString())
                .addFormDataPart("tags", jsonText)
                .build();
        Request request = new Request.Builder()
                .url(sharedPreferencesManager.getServerAddress() + "meme/upload")
                .post(body)
                .addHeader("Authorization", "Bearer " + sharedPreferencesManager.getToken())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) { e.printStackTrace(); }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.code() != 200){
                    Log.d("request", "File...:::: uti - "+file .getPath()+" file -" + file + " : " + file .exists());
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            Toast.makeText(context, "Add meme successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }
}