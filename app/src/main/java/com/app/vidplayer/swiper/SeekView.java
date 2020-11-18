package com.app.vidplayer.swiper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.app.vidplayer.R;

/**
 * Created by pulkit on 14/12/16.
 */

public class SeekView {

    private Context mContext;
    private View view;
    private AppCompatTextView textView;

    public SeekView(Context context) {
        mContext = context;
        ViewGroup layout = (ViewGroup) ((AppCompatActivity) context).findViewById(android.R.id.content).getRootView();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.custom_seekview, null);
        view.getBackground().setAlpha(100);
        textView=(AppCompatTextView)view.findViewById(R.id.textView3);
        Typeface.createFromAsset(context.getAssets(),"DroidSans-Bold.ttf");
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        params.leftMargin = 100;
        params.bottomMargin = 10;
        ConstraintLayout relativeLayout = new ConstraintLayout(context);
         relativeLayout.addView(view);
        layout.addView(relativeLayout, params);
        view.requestLayout();
        view.setVisibility(View.INVISIBLE);
    }
    public void show()
    {
        view.setVisibility(View.VISIBLE);
    }
    public void hide()
    {
        view.setVisibility(View.INVISIBLE);

    }
    public void setText(String s)
    {
        textView.setText(s);
    }
    public boolean isVisible()
    {
        if(view.getVisibility()==View.VISIBLE)
            return true;
        else
            return false;
    }
}
