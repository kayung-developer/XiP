package com.app.vidplayer.video;

import androidx.annotation.NonNull;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;


import com.app.vidplayer.R;
import com.app.vidplayer.swiper.Swipper;
import com.app.vidplayer.utils.StorageUtils;

import java.io.IOException;
import java.util.Random;

import static com.app.vidplayer.activity.MainActivity.videoArrayList;

public class VideoPlayActivity extends AppCompatActivity {
    private NotificationManager mNotifyManager;
    private static final int NOTIFICATION_ID = 0;
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    VideoView videoView;
    AppCompatImageView prev, next, pause, lock;
    AppCompatImageView btnForward, btnBackward, repeat, shuffle;
    AppCompatSeekBar seekBar;
    int video_index = 0;
    double current_pos, total_duration;
    AppCompatTextView current, total, textview, id_title;
    LinearLayoutCompat showProgress, button;
    Handler mHandler, handler;
    boolean isVisible = true;
    boolean locked = true;
    boolean unlocked = true;
 ConstraintLayout relativeLayout;
    public static final int PERMISSION_READ = 1;
    private VideoModel video;
    MediaPlayer mp;
    AudioManager audioManager;
    SurfaceView surfaceView;
    private int seekForwardTime = 10000; // 10000 milliseconds
    private int seekBackwardTime = 10000; // 10000 milliseconds
    private boolean isShuffle = false;
    private boolean isRepeat = false;
     private VideoModel activeVideo; //an object on the currently playing audio
    Random rand;
    Swipper swipper;
    StorageUtils storage;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        callStateListener();
       mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Swipper swipper = new Swipper();
        swipper.Brightness(Swipper.Orientation.CIRCULAR);
        swipper.Volume(Swipper.Orientation.VERTICAL);
        swipper.Seek(Swipper.Orientation.HORIZONTAL, videoView);
        swipper.set(this);

        //Listen for new Audio to play -- BroadcastReceiver
         if (checkPermission()) {
            setVideo();
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    public void setVideo() {
        rand=new Random();
        textview = (AppCompatTextView) findViewById(R.id.text);
        videoView = (VideoView) findViewById(R.id.videoview);
        prev = (AppCompatImageView) findViewById(R.id.prev);
        next = (AppCompatImageView) findViewById(R.id.next);
        pause = (AppCompatImageView) findViewById(R.id.pause);
        lock = (AppCompatImageView) findViewById(R.id.lock);
        btnForward = (AppCompatImageView) findViewById(R.id.forward);
        btnBackward  = (AppCompatImageView) findViewById(R.id.backward);
        shuffle  = (AppCompatImageView) findViewById(R.id.shuffle);
        repeat  = (AppCompatImageView) findViewById(R.id.repeat);
        seekBar = (AppCompatSeekBar) findViewById(R.id.seekbar);
         current = (AppCompatTextView) findViewById(R.id.current);
        total = (AppCompatTextView) findViewById(R.id.total);
        showProgress = (LinearLayoutCompat) findViewById(R.id.showProgress);
        button = (LinearLayoutCompat) findViewById(R.id.buttons);
        relativeLayout = (ConstraintLayout) findViewById(R.id.relative);
        id_title = (AppCompatTextView) findViewById(R.id.id_title);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);


        getIntent();
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(videoView.isPlaying()) {
                    videoView.pause();
                    pause.setImageResource(R.drawable.pause_foreground);
                }else if (!videoView.isPlaying()){
                    videoView.start();
                    pause.setImageResource(R.drawable.play_video_foreground);
                }
            }
        });

        video_index = getIntent().getIntExtra("pos" , 0);
        StorageUtils storage = new StorageUtils(getApplicationContext());
        storage.storeAudio(videoArrayList);
        storage.storeVideoIndex(video_index);


        if(locked){
            lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    lock.setImageResource(R.drawable.locked_foreground);
                    Toast.makeText(getApplicationContext(), "Locked, Hold to Unlock",  Toast.LENGTH_LONG)
                            .show();

                }
            });
        }
        if(unlocked){
            lock.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                    lock.setImageResource(R.drawable.unlocked_foreground);
                    Toast.makeText(getApplicationContext(), "Unlocked", Toast.LENGTH_LONG).show();
                    return true;
                }

            });
        }
        repeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    repeat.setImageResource(R.drawable.rpt_foreground);
                }else{
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    repeat.setImageResource(R.drawable.rpt_pressed_foreground);
                    shuffle.setImageResource(R.drawable.shffle_foreground);
                }
            }
        });
        shuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    shuffle.setImageResource(R.drawable.shffle_foreground);
                }else{
                    // make repeat to true
                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    shuffle.setImageResource(R.drawable.shffle_pressed_foreground);
                    repeat.setImageResource(R.drawable.rpt_foreground);
                }
            }
        });


        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                current_pos = videoView.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if(current_pos + seekForwardTime <= videoView.getDuration()){
                    // forward song
                    videoView.seekTo((int) (current_pos + seekForwardTime));
                }else{
                    // forward to end position
                    videoView.seekTo(videoView.getDuration());
                }

            }
        });
        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                current_pos = videoView.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if(current_pos - seekBackwardTime >= 0){
                    // forward song
                    videoView.seekTo((int) (current_pos - seekBackwardTime));
                }else{
                    // backward to starting position
                    videoView.seekTo(0);
                }

            }
        });





        mHandler = new Handler();
        handler = new Handler();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(isRepeat){
                    // repeat is on play same video again
                    playVideo(video_index);
                } else if(isShuffle){
                    // shuffle is on - play a random video
                    Random rand = new Random();
                    video_index = rand.nextInt((videoArrayList.size() - 1) + 1);
                    playVideo(video_index);
                } else{
                    // no repeat or shuffle ON - play next video
                    if(video_index < (videoArrayList.size() - 1)){
                        playVideo(video_index + 1);
                        video_index = video_index + 1;
                    }else{
                        // play first video
                        playVideo(0);
                        video_index = 0;
                    }
                }

            }
        });




        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPrepared(MediaPlayer mp) {
                setVideoProgress();
                mp.setScreenOnWhilePlaying(true);
                VideoNotify();

            }
        });




        playVideo(video_index);
        storage = new StorageUtils(getApplicationContext());
        storage.storeAudio(videoArrayList);
        storage.storeVideoIndex(video_index);

        prevVideo();
        nextVideo();
        setPause();
        hideLayout();

    }



    @Override
    public void setTheme(int resId) {
        super.setTheme(resId);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {

 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onConfigurationChanged(newConfig);
    }


    // play video file
    public void playVideo(int pos) {
        try  {
            videoView.setVideoURI(videoArrayList.get(pos).getVideoUri());
            id_title.setText(videoArrayList.get(pos).videoTitle);
            videoView.start();

            pause.setImageResource(R.drawable.play_video_foreground);
            video_index=pos;
            VideoNotify();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // display video progress
    public void setVideoProgress() {
        //get the video duration
        current_pos = videoView.getCurrentPosition();
        total_duration = videoView.getDuration();

        //display video duration
        total.setText(timeConversion((long) total_duration));
        current.setText(timeConversion((long) current_pos));
        seekBar.setMax((int) total_duration);
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    current_pos = videoView.getCurrentPosition();
                    current.setText(timeConversion((long) current_pos));
                    seekBar.setProgress((int) current_pos);
                    handler.postDelayed(this, 1000);
                } catch (IllegalStateException ed){
                    ed.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1000);

        //seekbar change listner
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                current_pos = seekBar.getProgress();
                videoView.seekTo((int) current_pos);
                new StorageUtils(getApplicationContext()).storeProgress((int) current_pos);
            }
        });
    }

    //play previous video
    public void prevVideo() {
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_index > 0) {
                    video_index--;
                    playVideo(video_index);
                } else {
                    video_index = videoArrayList.size() - 1;
                    playVideo(video_index);

                }
            }
        });
        new StorageUtils(getApplicationContext()).storeVideoIndex(video_index);

    }

    //play next video
    public void nextVideo() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_index < (videoArrayList.size()-1)) {
                    video_index++;
                    playVideo(video_index);
                } else {
                    video_index = 0;
                    playVideo(video_index);
                }
            }
        });
        new StorageUtils(getApplicationContext()).storeVideoIndex(video_index);
    }


    //pause video
    public void setPause() {
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    pause.setImageResource(R.drawable.pause_foreground);
                } else {
                    videoView.start();
                    pause.setImageResource(R.drawable.play_video_foreground);
                }
            }
        });
    }

    //time conversion
    @SuppressLint("DefaultLocale")
    public String timeConversion(long value) {
        String songTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur % 3600000) / 60000;
        int scs = (dur % 3600000 % 60000) / 1000;

        if (hrs > 0) {
            songTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            songTime = String.format("%02d:%02d", mns, scs);
        }
        return songTime;
    }


    //hide the layout wen playing video
    // hide progress when the video is playing
    public void hideLayout() {

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                showProgress.setVisibility(View.GONE);
                id_title.setVisibility(View.GONE);
                button.setVisibility(View.GONE);
                isVisible = false;
            }
        };
        handler.postDelayed(runnable, 5000);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(runnable);
                if (isVisible ) {
                    showProgress.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                    id_title.setVisibility(View.GONE);
                    isVisible = false;
                } else  {
                    showProgress.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                    id_title.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(runnable, 5000);
                    isVisible = true;
                }
            }
        });

    }

    // runtime storage permission
    public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if ((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_READ) {
            if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), "Please allow storage permission", Toast.LENGTH_LONG).show();
                } else {
                    setVideo();
                }
            }
        }
    }


    public void onPause(){
        super.onPause();
        assert videoView != null;
        if(videoView.isPlaying()){
            videoView.pause();
            current_pos = videoView.getCurrentPosition();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!videoView.isPlaying()) {
            videoView.seekTo((int) current_pos);
            videoView.start();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
new StorageUtils(getApplicationContext()).clearCachedVideoPlaylist();

    }




    private void callStateListener()
    {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService( Context.TELEPHONY_SERVICE );
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener()
        {
            @Override
            public void onCallStateChanged( int state, String incomingNumber )
            {
                switch ( state )
                {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if ( videoView != null )
                        {
                            videoView.pause();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if ( videoView != null )
                        {
                            if ( ongoingCall )
                            {
                                ongoingCall = false;
                                videoView.resume();

                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen( phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE );
    }



    public void VideoNotify(){
        Intent result = new Intent(this, VideoPlayActivity.class);
        result.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, result, PendingIntent.FLAG_UPDATE_CURRENT);  NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable._file_foreground)
                .setContentTitle("XP")
                .setContentText("Xperience the best @ XP")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                 .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable._file_foreground, "open", notificationPendingIntent);

        Notification myNotification = notifyBuilder.build();
        mNotifyManager.notify(NOTIFICATION_ID, myNotification);
    }


        }