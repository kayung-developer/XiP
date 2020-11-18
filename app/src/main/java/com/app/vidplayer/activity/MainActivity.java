package com.app.vidplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.vidplayer.R;
import com.app.vidplayer.swiper.Swipper;
import com.app.vidplayer.utils.StorageUtils;
import com.app.vidplayer.video.RecycleItemTouchHelper;
import com.app.vidplayer.audio.AudioActivity;
import com.app.vidplayer.search.SearchActivity;
import com.app.vidplayer.utils.PrefManager;
import com.app.vidplayer.utils.Utility;
import com.app.vidplayer.video.VideoAdapter;
import com.app.vidplayer.video.VideoModel;
import com.app.vidplayer.video.VideoPlayActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import android.Manifest;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;


import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.Toast;


import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.net.Uri;


public class MainActivity extends   AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RecycleItemTouchHelper.RecyclerItemTouchHelperListener, SwipeRefreshLayout.OnRefreshListener {
    public static ArrayList<VideoModel> videoArrayList;
    SwipeRefreshLayout swipe;
    RecyclerView recycler_view;
    public static final int PERMISSION_READ = 0;
     BottomNavigationView bottomNavigationView;
    CoordinatorLayout coordinate;
    private static final int THEME_DARK = 2;
    private static final int THEME_LIGHT = 1;
    VideoAdapter.viewHolder viewHolder;
    ConstraintLayout rellayout;
VideoPlayActivity activity;

String [] bitmap = {MediaStore.Video.Thumbnails.DATA};
String[] name = new String[]{MediaStore.Video.VideoColumns.DISPLAY_NAME};
    AudioManager audioManager;
    File file;
    Utility utility;
    private Animation rotate_fade;
    private AppCompatImageView image;
    VideoAdapter adapter;
    SwitchCompat switch_theme;
    int backgroundColor;
    ConstraintLayout constraint;


    @SuppressLint("ResourceAsColor")
     @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
          swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
         coordinate = (CoordinatorLayout) findViewById(R.id.coordinate);
        constraint = (ConstraintLayout) findViewById(R.id.constraint);

         swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
             @Override
             public void onRefresh() {
                 new Handler().postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         swipe.setRefreshing(false);
                         if(checkInternetConenction() || !checkInternetConenction()){
                             videoList();


                               }
                     }
                 }, 3000);

             }
         });

         swipe.setColorSchemeColors(
                 getResources().getColor(R.color.blue_dark),
                 getResources().getColor(R.color.red_dark),
                 getResources().getColor(R.color.color_green),
                 getResources().getColor(R.color.orange_dark)

         );
         image = (AppCompatImageView) findViewById(R.id.image_anim);
         rotate_fade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_fadeout);

         bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PrefManager prefManager = new PrefManager(getApplicationContext());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
           final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_item);
         updateTheme();
         navigationView.setNavigationItemSelectedListener(this);
        if (prefManager.isFirstTimeLaunch()) {
            prefManager.setFirstTimeLaunch(false);

        }

         if (checkPermission()) {
            videoList();
            getIntent();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
              @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.video:
                         break;
                    case R.id.share:
                        Share();
                        break;
                    case R.id.audio:
                        Intent audio = new Intent(getApplicationContext(), AudioActivity.class);
                        startActivity(audio);
                        break;
                }

                return false;
            }
        });


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public void updateTheme() {
        if (Utility.getTheme(getApplicationContext()) <= THEME_LIGHT) {
            setTheme(R.style.AppTheme_Light);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

            }
        } else if (Utility.getTheme(getApplicationContext()) == THEME_DARK) {
            setTheme(R.style.AppTheme_Dark);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));


            }
        }

    }


public void setThemeLight(){
    Utility.setTheme(getApplicationContext(), 1);
    recreateActivity();
        }
    public void setThemeDark(){
            Utility.setTheme(getApplicationContext(), 2);
            recreateActivity();
        }

public  void recreateActivity(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
}
    public void Share() {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Insert Subject here");
        String app_url = " https://play.google.com/store/apps/details?id=com.app.vdplayer";
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, app_url);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }


    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        getBaseContext();
        ConnectivityManager connec
                = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        // Check for network connections
        if (Objects.requireNonNull(connec.getNetworkInfo(0)).getState() ==
                android.net.NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connec.getNetworkInfo(0)).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                Objects.requireNonNull(connec.getNetworkInfo(1)).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                Objects.requireNonNull(connec.getNetworkInfo(1)).getState() == android.net.NetworkInfo.State.CONNECTED) {

        } else if (
                Objects.requireNonNull(connec.getNetworkInfo(0)).getState() ==
                        android.net.NetworkInfo.State.DISCONNECTED ||
                        Objects.requireNonNull(connec.getNetworkInfo(1)).getState() ==
                                android.net.NetworkInfo.State.DISCONNECTED) {

        }
        return false;
    }

    public void videoList() {
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recycler_view.setItemAnimator(itemAnimator);
        videoArrayList = new ArrayList<>();
        getVideos();
        ItemTouchHelper.SimpleCallback callback = new RecycleItemTouchHelper(0,  ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(callback).attachToRecyclerView(recycler_view);

    }


    //get video files from storage
    public void getVideos() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {

                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                int str_thumb = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                String details = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.RESOLUTION));
                 String author = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));


                VideoModel videoModel = new VideoModel();
                videoModel.setVideoTitle(title);
                videoModel.setVideoUri(Uri.parse(data));
                videoModel.setVideoDuration(time(Long.parseLong(duration)));
                videoModel.setStr_thumb(cursor.getString(str_thumb));
                videoModel.setSize(floatform(Long.parseLong(size)));
                videoModel.setResolution(details);
                videoModel.setAuthor(author);

                videoArrayList.add(videoModel);

            } while (cursor.moveToNext());

        }

        adapter =  new VideoAdapter(this, videoArrayList);
        recycler_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();



        adapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {

                    Intent intent = new Intent(getApplicationContext(), VideoPlayActivity.class);
                    intent.putExtra("pos", pos);
                    startActivity(intent);
                   StorageUtils storage = new StorageUtils(getApplicationContext());
                   storage.storeAudio(videoArrayList);
            }



         });

        image.setVisibility(View.VISIBLE);
        image.startAnimation(rotate_fade);
    }


//second time conversion
    private String floatform(long bytes){
         long kb = 1024;
         long mb = kb*1024;
         long gb = mb*1024;
         long tr = gb*1024;
         if((bytes >= 0 ) && (bytes < kb)){
             return bytes +" B";
         }
         else if((bytes >= kb) && (bytes < mb)){
             return (bytes / kb) + " KB";
         }else if((bytes >= mb) && (bytes < gb)){
    return (bytes / mb) + " MB";
        } else if((bytes >= gb) && (bytes < tr)){
             return (bytes / gb)+ " GB";
         } else if(bytes >= tr){
             return (bytes / tr) + " TB";
         }else {
             return bytes + " B";
         }
    }

    //time conversion
    public String time(long value) {
        String videoTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur % 3600000) / 60000;
        int scs = (dur % 3600000 % 60000) / 1000;

        if (hrs > 0) {
            videoTime = String.format("%02dh:%02dm:%02ds", hrs, mns, scs);
        } else {
            videoTime = String.format("%02dm:%02ds", mns, scs);
        }
        return videoTime;
    }

    //runtime storage permission
    public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if ((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ: {
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getApplicationContext(), "Please allow storage permission", Toast.LENGTH_LONG).show();
                    } else {
                        videoList();
                    }
                }
            }
        }
    }


    public void onBackPressed() {
        showAlertDialog();
    }


    private void showAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Exit");
        builder.setIcon(R.drawable._file_foreground);
        builder.setMessage("Are you really sure you want to quit??");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.finish();
            }
        });
        builder.setNeutralButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        builder.create().show();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main,  menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case R.id.info:
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(intent);
                break;
            case R.id.search:
                Intent search = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(search);
                break;
            case R.id.theme:
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                final SharedPreferences.Editor editor = pref.edit();
                backgroundColor = pref.getInt("backgroundColor", 0);
                constraint.setBackgroundColor(backgroundColor);
                backgroundColor = getResources().getColor(R.color.black);
                constraint.setBackgroundColor(backgroundColor);
                editor.putInt("backgroundColor", backgroundColor);
                editor.apply();
                break;


        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
         this.finish();
        return true;
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.homepage) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        else if (id == R.id.share) {
            Share();
        } else if (id == R.id.file) {
            startActivity(new Intent(getApplicationContext(), AudioActivity.class));
        } else if(id == R.id.exit){
            MainActivity.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, final int direction, final int pos) {
        if (viewHolder instanceof VideoAdapter.viewHolder) {
            // get the removed item name to display it in snack bar
            final String title = videoArrayList.get(viewHolder.getAdapterPosition()).getVideoTitle();
            adapter.removeItem(pos);
            adapter.notifyDataSetChanged();
            final File[] file = {new File(String.valueOf(viewHolder.getAdapterPosition()))};

            final Snackbar snackbar = Snackbar
                    .make(coordinate, title + " removed successfully!!", Snackbar.LENGTH_LONG);
             snackbar.setDuration(5000);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     adapter.notifyItemInserted(pos);
                    adapter.notifyDataSetChanged();
                     snackbar.getDuration();

                    // undo is selected, restore the deleted item

                }
            });
            if(file[0].exists() || file[0].isFile()){
                file[0] = new File(file[0].getName());
                file[0].delete();
            }
            snackbar.setActionTextColor(Color.BLUE);
            snackbar.show();
            // backup of removed item for undo purpose

        }
    }



    @Override
    public void onRefresh() {
          videoList();
          getVideos();
        adapter.updateData(viewHolder);
        adapter.notifyDataSetChanged();

    }
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return ( name.equalsIgnoreCase(".mp4")|| name.equalsIgnoreCase("wmv") || name.equalsIgnoreCase("3gp"));
        }
    }
}