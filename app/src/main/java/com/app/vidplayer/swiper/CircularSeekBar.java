package com.app.vidplayer.swiper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import androidx.core.content.ContextCompat;
import android.content.Context;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.TypefaceCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewGroupCompat;
import androidx.core.view.WindowCompat;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.appcompat.widget.AppCompatTextView;


public class CircularSeekBar {

    final Context mContext;
    private int percent;
    private int currentVolume;
    private int maxVolume;
    private int valueCounter;
    private float brightness;
    private String Type;
    private AudioManager audio;
    private DialView dailView;
    public static ConstraintLayout relativeLayout;

    public CircularSeekBar(Context context) {
        mContext = context;
        ViewGroup layout = (ViewGroup) ((AppCompatActivity) context).findViewById(android.R.id.content).getRootView();
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        relativeLayout = new ConstraintLayout(context) {


            private int valueCounter = (int) ((android.provider.Settings.System.getFloat(getContext().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, -1) / 255) * 360 / 3f);
            public AppCompatTextView textView;

            {
                if (Type == "Brightness") {
                    valueCounter = (int) ((android.provider.Settings.System.getFloat(getContext().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, -1) / 255) * 360 / 3f);
                } else if (Type == "Volume") {
                    currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                    valueCounter = (int) (((float) currentVolume / (float) maxVolume) * 360 / 3f);
                }
                addView(dailView = new DialView(getContext()) {
                    {
                        setStepAngle(3f);
                        setDiscArea(.35f, .48f);
                        setLastAngle((android.provider.Settings.System.getFloat(getContext().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, -1) / 255) * 360);
                       valueCounter = (int) ((android.provider.Settings.System.getFloat(getContext().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, -1) / 255) * 360 / 3f);
//                        valueCounter = 23;
                        brightness = android.provider.Settings.System.getFloat(getContext().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, -1);
                        WindowManager.LayoutParams layout = ((AppCompatActivity) mContext).getWindow().getAttributes();
                        layout.screenBrightness = brightness / 255;
                        ((AppCompatActivity) mContext).getWindow().setAttributes(layout);
                        if (Type == "Brightness") {
                            setLastAngle((android.provider.Settings.System.getFloat(getContext().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, -1) / 255) * 360);
                           valueCounter = (int) ((android.provider.Settings.System.getFloat(getContext().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, -1) / 255) * 360 / 3f);
                            valueCounter = (int) ((100 / 255) * 360 / 3f);
                            brightness = android.provider.Settings.System.getFloat(getContext().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, -1);
                            WindowManager.LayoutParams layout1 = ((AppCompatActivity) mContext).getWindow().getAttributes();
                           layout1.screenBrightness = brightness / 255;
                            layout1.screenBrightness = 1;
                            ((AppCompatActivity) mContext).getWindow().setAttributes(layout1);
                        } else if (Type == "Volume") {
                            currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                            setLastAngle(((float) currentVolume / (float) maxVolume) * 360);
                            valueCounter = (int) (((float) currentVolume / (float) maxVolume) * 360 / 3f);
                            audio.setStreamVolume(AudioManager.STREAM_MUSIC, (int) currentVolume, 0);

                        }
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    protected void onRotate(int offset) {
                        valueCounter += offset;
                        if (valueCounter <= 100 && valueCounter >= 0) {
                            percent = valueCounter;
                            textView.setText(String.valueOf(valueCounter) + "%");
                            if (Type == "Brightness") {
                               WindowManager.LayoutParams layout = ((AppCompatActivity) mContext).getWindow().getAttributes();
                                layout.screenBrightness = (float) ((double) valueCounter / (double) 100);
                                ((AppCompatActivity) mContext).getWindow().setAttributes(layout);
//                                Swipper.fl.getBackground().setAlpha((int) (((100-(double) valueCounter )/ (double) 100) * 255));
                            } else if (Type == "Volume") {
                                audio.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (((double) valueCounter / (double) 100) * 15), 0);
                            }
                        }
                        if (valueCounter > 100)
                            valueCounter = 100;
                        if (valueCounter < 0)
                            valueCounter = 0;
                    }
                }, new LayoutParams(0, 0) {
                    {
                        width = MATCH_PARENT;
                        height = MATCH_PARENT;

                    }
                });
                addView(textView = new androidx.appcompat.widget.AppCompatTextView(getContext()) {
                    {

                        setText(Integer.toString(valueCounter) + "%");
                        setTextColor(Color.WHITE);
                        setTextSize(40);
                        setTypeface(Typeface.createFromAsset(mContext.getAssets(), "DroidSans-Bold.ttf"));
                    }
                }, new LayoutParams(0, 0) {
                    {
                        width = WRAP_CONTENT;
                        height = WRAP_CONTENT;

                    }
                });
            }
        };
        relativeLayout.setVisibility(View.INVISIBLE);
        layout.addView(relativeLayout, params);

    }

    public void show() {
        relativeLayout.setVisibility(View.VISIBLE);
    }

    public void hide() {

        relativeLayout.setVisibility(View.INVISIBLE);
    }

    public boolean isVisibile() {
        if (relativeLayout.getVisibility() == View.VISIBLE)
            return true;
        else
            return false;
    }

    public void setType(String type) {
        Type = type;
        audio = (AudioManager) (mContext).getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        valueCounter = (int) ((android.provider.Settings.System.getFloat((mContext).getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, -1) / 255) * 360 / 3f);

    }
}
