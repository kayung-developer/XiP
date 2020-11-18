package com.app.vidplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.app.vidplayer.R;
import com.app.vidplayer.utils.PrefManager;



public class SlashActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private int pStatus = 20*2;
    private Handler handler = new Handler();
   private AppCompatTextView textView, txtProgress;
   private PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash);
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
              finish();
        }
        AppCompatImageView imageView = (AppCompatImageView) findViewById(R.id.img);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        textView = (AppCompatTextView) findViewById(R.id.txtview);
        txtProgress = (AppCompatTextView) findViewById(R.id.txtProgress);
        progressBar.setVisibility(View.VISIBLE);
progressBar.setProgress(pStatus+10);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (pStatus <= 100) {
                    handler.post(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            progressBar.setProgress(pStatus);
                              txtProgress.setText(pStatus + " %");
                        if(pStatus == 100){
                        load();
                        } else{
                        } }
                    });
                    try {
                        Thread.sleep(99);
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }
                    pStatus  ++;

                }
            }
        }).start();


    }
    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(SlashActivity.this, MainActivity.class));
        finish();
    }
    public void load(){
        Intent intent = new Intent(SlashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
