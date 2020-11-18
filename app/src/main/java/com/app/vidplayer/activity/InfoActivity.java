package com.app.vidplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.app.vidplayer.R;

public class InfoActivity extends AppCompatActivity {
AppCompatTextView title, summary, details, copyright;
    AppCompatImageView  imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getSupportActionBar();
        getIntent();
        title  = (AppCompatTextView) findViewById(R.id.title);
        summary = (AppCompatTextView) findViewById(R.id.summary);
       details = (AppCompatTextView) findViewById(R.id.details);
       copyright =  (AppCompatTextView) findViewById(R.id.copyright);
       imageView = (AppCompatImageView) findViewById(R.id.imageView);

    }
}