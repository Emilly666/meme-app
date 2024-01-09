package com.example.memeapp.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.memeapp.databinding.FragmentMainBinding;

import com.example.memeapp.R;
import com.example.memeapp.model.meme.Meme;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    public FragmentMainBinding binding;
    public RecyclerView recyclerView;
    public RecylerViewAdapter recylerViewAdapter;
    public ArrayList<Meme> memesArrayList = new ArrayList<>();

    public boolean isLoading = false;

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

        initViews(root);
        memesArrayList.add(new Meme());
        memesArrayList.add(new Meme());
        memesArrayList.add(new Meme());
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

                                                 if (!isLoading) {
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

        moreMemes(3);
        isLoading = false;
    }
    public void moreMemes(int newMemesCount){
        if(memesArrayList.get(memesArrayList.size()-1)== null){
            memesArrayList.remove(memesArrayList.size()-1);
            recyclerView.post(new Runnable() {
                public void run() {
                    recylerViewAdapter.notifyItemInserted(memesArrayList.size() - 1);
                }
            });
        }
        for(int i = 0; i < newMemesCount; i++){
            memesArrayList.add(new Meme());
        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        memesArrayList.clear();
        isLoading = false;
        binding = null;
    }
}