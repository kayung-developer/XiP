package com.app.vidplayer.audio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.vidplayer.R;
import com.app.vidplayer.video.VideoModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kayung.
 */
public class RecyclerView_Adapter extends RecyclerView.Adapter<ViewHolder> {

    ArrayList<Audio> list;
    Context context;

    public RecyclerView_Adapter(ArrayList<Audio> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int direction) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.title.setText(list.get(direction).getTitle());
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

}
class ViewHolder extends RecyclerView.ViewHolder {

    AppCompatTextView title;
    AppCompatImageView play_pause;

    ViewHolder(View itemView) {
        super(itemView);
        title = (AppCompatTextView) itemView.findViewById(R.id.title);
        play_pause = (AppCompatImageView) itemView.findViewById(R.id.play_pause);
    }
}