package com.app.vidplayer.video;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.app.vidplayer.R;
import com.bumptech.glide.Glide;


import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;


import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VideoInfo extends AppCompatActivity {
VideoAdapter.viewHolder viewHolder, holder;
AppCompatImageButton delete;
AppCompatTextView _details, _duration, _title, _size, _author, name, resolution, size, duration;
public static ArrayList<VideoModel> videoArrayList;
VideoModel videoModel;
AppCompatImageView str_thumb;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_info);

        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VideoPlayActivity.class);
                startActivity(intent);

            }
        });



resolution = (AppCompatTextView) findViewById(R.id._resolution);
size  = (AppCompatTextView) findViewById(R.id._size);
duration = (AppCompatTextView) findViewById(R.id._duration);
         delete = (AppCompatImageButton) findViewById(R.id.vid_delete);
         str_thumb = (AppCompatImageView) findViewById(R.id.image);
        _details = (AppCompatTextView) findViewById(R.id.details);
        _duration = (AppCompatTextView) findViewById(R.id.duration);
        _title = (AppCompatTextView) findViewById(R.id.title);
        _size = (AppCompatTextView) findViewById(R.id.size);
        _author = (AppCompatTextView) findViewById(R.id._author);
        name = (AppCompatTextView) findViewById(R.id.name);

        String title = getIntent().getStringExtra("_title");
        _title.setText(title);

        String details = getIntent().getStringExtra("_details");
       _details.setText(details);

        String duration = getIntent().getStringExtra("_duration");
        _duration.setText(duration);

        String size = getIntent().getStringExtra("_size");
        _size.setText(size);

String image = getIntent().getStringExtra("image");
        Glide.with(this).load(image)
                .skipMemoryCache(false)
                .into(str_thumb);
        String author = getIntent().getStringExtra("_author");
        _author.setText(author);

        getIntent();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

}