package com.example.memeapp.ui.main;

import static com.example.memeapp.ui.main.MainActivity.JSON;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memeapp.SharedPreferencesManager;
import com.example.memeapp.databinding.FragmentMainBinding;

import com.example.memeapp.R;
import com.example.memeapp.model.AuthenticationResponse;
import com.example.memeapp.model.meme.Meme;
import com.example.memeapp.model.tag.Tag;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        super.onCreate(savedInstanceState);
        recyclerView = (RecyclerView)root.findViewById(R.id.recyclerViewMain);
        context = getContext();
        sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
        client = new OkHttpClient();

        initViews(root);
        moreMemes(MEME_BATCH);
        initAdapter();
        initScrollListener();

        return root;
    }
    private void initViews(View view) {

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.category_name_main);

    }
    public void initAdapter() {
        recylerViewAdapter = new RecylerViewAdapter(memesArrayList, getActivity());
        recyclerView.setAdapter(recylerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
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
        String json = "{ " +
                "\"lastMeme_id\" : \"" + lastMeme_Id + "\", " +
                "\"tag_id\" : \"" + "0" +"\", " +
                "\"count\" : \"" + MEME_BATCH +"\" " +
                "}";
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(sharedPreferencesManager.getServerAddress() + "meme/get/" + sharedPreferencesManager.getUser().getId())
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
                    JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
                    JsonArray list = jsonResponse.get("memesWithTags").getAsJsonArray();
                    if(list.size() < MEME_BATCH){
                        loadMore = false;
                    }
                    for(int i = 0; i < list.size(); i++){
                        JsonObject user = list.get(i).getAsJsonObject().get("user").getAsJsonObject();
                        JsonObject meme = list.get(i).getAsJsonObject().get("meme").getAsJsonObject();

                        List<Tag> tagList = new ArrayList<>();
                        JsonArray tags = list.get(i).getAsJsonObject().get("tags").getAsJsonArray();
                        for(int j = 0; j < tags.size(); j++){
                            int id = tags.get(j).getAsJsonObject().get("id").getAsInt();
                            String name= tags.get(j).getAsJsonObject().get("name").getAsString();
                            tagList.add(new Tag(id, name));
                        }
                        int value = 0;
                        if(!list.get(i).getAsJsonObject().get("liked").isJsonNull()){
                            value = list.get(i).getAsJsonObject().get("liked").getAsInt();
                        }

                        Meme newMeme = new Meme(
                                meme.get("id").getAsInt(),
                                meme.get("file_path").getAsString(),
                                meme.get("title").getAsString(),
                                Timestamp.valueOf(meme.get("add_timestamp").getAsString()),
                                meme.get("total_likes").getAsInt(),
                                user.get("id").getAsInt(),
                                user.get("nickname").getAsString(),
                                value,
                                tagList
                        );
                        memesArrayList.add(newMeme);
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