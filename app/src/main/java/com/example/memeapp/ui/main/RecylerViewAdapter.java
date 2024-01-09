package com.example.memeapp.ui.main;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.startActivity;


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memeapp.R;
import com.example.memeapp.model.meme.Meme;
import com.example.memeapp.ui.profile.UserProfile;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RecylerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private List<Meme> memeList;
    private Context context;
    private final Activity myActivity;

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
        ImageButton imageButtonMenuPopup;

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
            imageButtonMenuPopup = itemView.findViewById(R.id.imageButtonMenuPopup);
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
        if (memeList.get(position).getReactionType() == 1) { // liked

        }
        else if (memeList.get(position).getReactionType() == -1) { // disliked

        }
        //String imageUri = memeList.get(position).getFile_path();
        String imageUri = "http://192.168.100.32:8080/meme/png/image.png";
        Picasso.get().setLoggingEnabled(true);
        Picasso.get().load(imageUri).into(viewHolder.imageViewMeme);




        viewHolder.imageButtonMenuPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(context, viewHolder.imageButtonMenuPopup);
                menu.inflate(R.menu.meme_popup_menu);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_download) {
                            if (isStoragePermissionGranted()) {
                                String state = Environment.getExternalStorageState();
                                if (Environment.MEDIA_MOUNTED.equals(state)) {
                                    Picasso.get().load(imageUri).into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            saveImageToDownloadFolder(Uri.parse(imageUri).getLastPathSegment(), bitmap);
                                        }
                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                        }
                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        }
                                    });
                                } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                                    Toast.makeText(context, "Storage is read only", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Storage does not exist", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        if (item.getItemId() == R.id.action_share) {
                            Picasso.get().load(imageUri).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    ContentValues values = new ContentValues();
                                    values.put(MediaStore.Images.Media.DISPLAY_NAME, "image.jpg");
                                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        values.put(MediaStore.Images.Media.IS_PENDING, 1);
                                    }
                                    Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                    try {
                                        OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                        outputStream.close();
                                        values.clear();
                                        values.put(MediaStore.Images.Media.IS_PENDING, 0);
                                        context.getContentResolver().update(uri, values, null, null);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("image/jpeg");
                                    share.putExtra(Intent.EXTRA_STREAM, uri);
                                    startActivity(context, Intent.createChooser(share, "Select"), null);
                                }
                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                }
                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                }
                            });
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
    public boolean isStoragePermissionGranted() {
        if (checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.v("LOG", "Permission granted");
            return true;
        } else {
            Log.v("LOG", "Permission revoked");
            ActivityCompat.requestPermissions(myActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
    }

    public void saveImageToDownloadFolder(String imageFile, Bitmap ibitmap) {
        try {
            File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), imageFile);
            int counter = 1;
            String newFileName = imageFile;
            String extension = newFileName.substring(newFileName.lastIndexOf("."));
            while (filePath.exists()) {
                newFileName = imageFile.substring(0, imageFile.lastIndexOf('.')) + "(" + counter + ")" + extension;
                filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), newFileName);
                counter++;
            }
            OutputStream outputStream = Files.newOutputStream(filePath.toPath());
            ibitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(context, "Saved " + newFileName + " in Download Folder", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
