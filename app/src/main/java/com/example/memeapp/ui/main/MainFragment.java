package com.example.memeapp.ui.main;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.memeapp.ui.main.MainActivity.JSON;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.memeapp.SharedPreferencesManager;
import com.example.memeapp.databinding.FragmentMainBinding;

import com.example.memeapp.R;
import com.example.memeapp.dto.AuthenticationResponse;
import com.example.memeapp.dto.GetMemesResponse;
import com.example.memeapp.model.meme.Meme;
import com.example.memeapp.model.tag.Tag;
import com.example.memeapp.model.user.User;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private RecyclerView recyclerView;
    private RecylerViewAdapter recylerViewAdapter;
    private ArrayList<Meme> memesArrayList = new ArrayList<>();
    private final Integer MEME_BATCH = 3;
    private SharedPreferencesManager sharedPreferencesManager;
    private Context context;
    private OkHttpClient client;
    private Tag tag;

    public boolean isLoading = false, loadMore = true;

    public MainFragment() {
        // Required empty public constructor
    }


    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, true);
        View root = binding.getRoot();

        super.onCreate(savedInstanceState);
        context = getContext();
        recyclerView = (RecyclerView)root.findViewById(R.id.recyclerViewMain);

        context = getContext();
        sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
        client = new OkHttpClient();

        initViews(root);
        moreMemes(1);
        initAdapter();
        initScrollListener();

        return root;
    }
    private void initViews(View view) {
        tag = sharedPreferencesManager.getUserSavedTags().get(getArguments().getInt("position") );
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.hashtag, tag.getName()));

        if(getArguments().getInt("position") != 0){
            MaterialButton menuButton = new MaterialButton(context);
            Toolbar.LayoutParams l1 = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
            l1.gravity = Gravity.END;
            menuButton.setLayoutParams(l1);
            menuButton.setIcon(ContextCompat.getDrawable(context, R.drawable.baseline_more_vert_24));
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu menu = new PopupMenu(context, menuButton);
                    menu.inflate(R.menu.tag_menu);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.tag_close) {
                                sharedPreferencesManager.deleteUserSavedTags(getArguments().getInt("position"));
                                getActivity().finish();
                                getActivity().overridePendingTransition(0, 0);
                                Intent myIntent = new Intent(getActivity(), getActivity().getClass());
                                ContextCompat.startActivity(context, myIntent, null);
                                getActivity().overridePendingTransition(0, 0);
                            }
                            return false;
                        }
                    });
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        menu.setForceShowIcon(true);
                    }
                    menu.show();
                }
            });
            toolbar.addView(menuButton);
        }
    }
    public void initAdapter() {
        recylerViewAdapter = new RecylerViewAdapter(memesArrayList, getActivity());
        recyclerView.setAdapter(recylerViewAdapter);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }
    public void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                             @Override
                                             public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                                 super.onScrollStateChanged(recyclerView, newState);

                                             }
                                             @Override
                                             public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                                 super.onScrolled(recyclerView, dx, dy);

                                                 LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                                                 if (!isLoading && loadMore) {
                                                     if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == memesArrayList.size() - 1 && memesArrayList.get(0)!=null) {
                                                         isLoading = true;
                                                         loadMore();
                                                     }
                                                 }

                                                 if (dy > 0) {
                                                     Log.d("move", "up");
                                                 } else {
                                                     Log.d("move", "down");
                                                 }
                                             }
                                         }
        );
    }
    public void loadMore() {
        memesArrayList.add(null);
        recyclerView.post(new Runnable() {
            public void run() {
                recylerViewAdapter.notifyItemInserted(memesArrayList.size() - 1);
            }
        });
        moreMemes(MEME_BATCH);
        isLoading = false;
    }
    public void moreMemes(int newMemesCount){
        if(memesArrayList.size() !=0 && memesArrayList.get(memesArrayList.size()-1) == null){
            memesArrayList.remove(memesArrayList.size()-1);
            recyclerView.post(new Runnable() {
                public void run() {
                    recylerViewAdapter.notifyItemRemoved(memesArrayList.size() - 1);
                }
            });
        }
        Integer lastMeme_Id = 0;
        if(memesArrayList.size() > 0){
            lastMeme_Id = memesArrayList.get(memesArrayList.size() -1).getId();
        }
        User user;
        if(sharedPreferencesManager.getUser() == null){
            user = new User();
        }
        else{
            user = sharedPreferencesManager.getUser();
        }
        String json = "{ " +
                "\"lastMeme_id\" : \"" + lastMeme_Id + "\", " +
                "\"tag_id\" : \"" + tag.getId() +"\", " +
                "\"count\" : \"" + MEME_BATCH +"\", " +
                "\"user_id\" : \"" + user.getId() +"\" " +
                "}";
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(sharedPreferencesManager.getServerAddress() + "meme/get" )
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) { e.printStackTrace(); }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.code() != 200){
                    Log.d("serverError", response.toString());
                }
                else{
                    Gson gson = new Gson();
                    GetMemesResponse getMemesResponse = gson.fromJson(response.body().string(), new TypeToken<GetMemesResponse>() {}.getType());
                    if(getMemesResponse.getMemesWithTags().size() < MEME_BATCH){
                        loadMore = false;
                    }
                    for (Meme meme: getMemesResponse.getMemesWithTags()) {
                        memesArrayList.add(meme);
                        recyclerView.post(new Runnable() {
                            public void run() {
                                recylerViewAdapter.notifyItemInserted(memesArrayList.size()-1);
                            }
                        });
                    }
                }
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        memesArrayList.clear();
        isLoading = false;
        binding = null;
    }
}