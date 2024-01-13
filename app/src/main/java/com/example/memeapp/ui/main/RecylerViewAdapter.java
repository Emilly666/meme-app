package com.example.memeapp.ui.main;

import static androidx.core.content.ContextCompat.getDrawable;
import static androidx.core.content.ContextCompat.startActivity;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.memeapp.R;
import com.example.memeapp.SharedPreferencesManager;
import com.example.memeapp.model.meme.Meme;
import com.example.memeapp.ui.addmeme.AddMeme;
import com.example.memeapp.ui.profile.UserProfile;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public class RecylerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private List<Meme> memeList;
    private Context context;
    private final Activity myActivity;
    private SharedPreferencesManager sharedPreferencesManager;

    public RecylerViewAdapter(List<Meme> itemList, Activity activity) {
        memeList = itemList;
        myActivity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        context = parent.getContext();
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meme, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading, parent, false);
            return new LoadingviewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            try {
                populateItemRows((ItemViewHolder) holder, position);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (holder instanceof LoadingviewHolder) {
            showLoadingView((LoadingviewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return memeList == null ? 0 : memeList.size();
    }

    public int getItemViewType(int position) {
        int VIEW_TYPE_LOADING = 1;
        return memeList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewMeme;
        TextView authorTextView, dateTextView, titleTextView;
        LinearLayout tagsLayout;
        Button buttonLikes, buttonDislikes, likesDisplay, buttonComments;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewMeme = itemView.findViewById(R.id.imageViewMeme);
            titleTextView = itemView.findViewById(R.id.title);
            authorTextView = itemView.findViewById(R.id.author);
            dateTextView = itemView.findViewById(R.id.date);
            buttonLikes = itemView.findViewById(R.id.buttonLikes);
            buttonDislikes = itemView.findViewById(R.id.buttonDislikes);
            likesDisplay = itemView.findViewById(R.id.likesDisplay);
            buttonComments = itemView.findViewById(R.id.buttonComments);
            tagsLayout = itemView.findViewById(R.id.tagsLayout);
        }
    }

    private class LoadingviewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingviewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressbar);
        }
    }

    private void showLoadingView(LoadingviewHolder viewHolder, int position) {
        // Progressbar would be displayed
    }

    private void populateItemRows(ItemViewHolder viewHolder, int position) throws IOException {
        viewHolder.titleTextView.setText(memeList.get(position).getTitle());
        viewHolder.authorTextView.setText(memeList.get(position).getAuthor_nickname());
        viewHolder.dateTextView.setText(hoursDifference(memeList.get(position).getAdd_timestamp()));
        viewHolder.likesDisplay.setText(String.valueOf(memeList.get(position).getTotal_likes()));
        sharedPreferencesManager =SharedPreferencesManager.getInstance(context);
        if (memeList.get(position).getReactionValue() == 1) { // liked

        }
        else if (memeList.get(position).getReactionValue() == -1) { // disliked

        }
        String imageUrl;
        if(memeList.get(position).getContent_type().equals("image/gif")){
            imageUrl = sharedPreferencesManager.getServerAddress() + "meme/gif/" + memeList.get(position).getFile_path();
            Glide
                .with(context)
                .asGif()
                .load(imageUrl)
                .into(viewHolder.imageViewMeme);
        }else{
            imageUrl = sharedPreferencesManager.getServerAddress() + "meme/png/" + memeList.get(position).getFile_path();
            Glide
                .with(context)
                .load(imageUrl)
                .into(viewHolder.imageViewMeme);
        }

        for(int i = 0; i < memeList.get(position).getTags().size(); i++){
            Button button = new Button(context);
            button.setText(memeList.get(position).getTags().get(i).getName());
            button.setBackground(getDrawable(context, R.drawable.tag_background));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 10, 10, 10);
            button.setLayoutParams(params);
            int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!sharedPreferencesManager.checkUserSavedTags(memeList.get(position).getTags().get(finalI))){
                        sharedPreferencesManager.addUserSavedTags(memeList.get(position).getTags().get(finalI));
                        myActivity.finish();
                        myActivity.overridePendingTransition(0, 0);
                        Intent myIntent = new Intent(myActivity, myActivity.getClass());
                        startActivity(context, myIntent, null);
                        myActivity.overridePendingTransition(0, 0);
                    }
                }
            });
            viewHolder.tagsLayout.addView(button);
        }
        viewHolder.authorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UserProfile.class);
                intent.putExtra("user_id", memeList.get(position).getAuthor_id());
                view.getContext().startActivity(intent);
            }
        });
        viewHolder.buttonLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            };
        });
        viewHolder.buttonDislikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
