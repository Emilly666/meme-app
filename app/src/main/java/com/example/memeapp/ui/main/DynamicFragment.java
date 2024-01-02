package com.example.memeapp.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.memeapp.R;
import com.example.memeapp.SharedPreferencesManager;
import com.example.memeapp.model.Tag.Tag;

import java.util.ArrayList;
import java.util.List;


public class DynamicFragment extends Fragment {
    SharedPreferencesManager sp;

    public static DynamicFragment newInstance() {
        return new DynamicFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = SharedPreferencesManager.getInstance(getContext());
    }

    // adding the layout with inflater
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dynamic, container, false);
        initViews(view);
        return view;
    }

    // initialise the categories
    private void initViews(View view) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    // pause function call
    @Override
    public void onPause() {
        super.onPause();
    }

    // resume function call
    @Override
    public void onResume() {
        super.onResume();
    }

    // stop when we close
    @Override
    public void onStop() {
        super.onStop();
    }

    // destroy the view
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
