package com.app.vidplayer.video;


import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import com.app.vidplayer.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.viewHolder>  {
    Context context;
    ArrayList<VideoModel > videoArrayList;
    ArrayList<VideoModel> arrayList;
    public OnItemClickListener onItemClickListener;
    public RelativeLayout viewForeground;



    public VideoAdapter (Context context, ArrayList<VideoModel> videoArrayList) {
        this.context = context;
        this.videoArrayList = videoArrayList;
        arrayList= new ArrayList<>(videoArrayList);

    }


    @Override
    public VideoAdapter .viewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View   view = LayoutInflater.from(context).inflate(R.layout.video_list, viewGroup, false);
              return new viewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(final VideoAdapter .viewHolder holder, final int i) {
        holder.title.setText(videoArrayList.get(i).getVideoTitle());
        holder.duration.setText(videoArrayList.get(i).getVideoDuration());
        Glide.with(context).load("file://" + videoArrayList.get(i).getStr_thumb())
                .skipMemoryCache(false)
                .into(holder.str_thumb);

        animate(holder);
        holder.size.setText(videoArrayList.get(i).getSize());
        holder.details.setText(videoArrayList.get(i).getResolution());
        holder.author.setText(videoArrayList.get(i).getAuthor());
       holder.image = videoArrayList.get(i).getStr_thumb();
         holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                final PopupMenu popupMenu = new PopupMenu(context, holder.itemView);
                popupMenu.inflate(R.menu.video_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.playall:
                                String _title = holder.title.getText().toString();
                                String _details = holder.details.getText().toString();
                                String _size = holder.size.getText().toString();
                                String _duration = holder.duration.getText().toString();
                                String _author = holder.author.getText().toString();
                                String image = holder.image.toString();



                                Intent info = new Intent(context, VideoInfo.class);
                                info.putExtra("_title", _title);
                                info.putExtra("_details", _details);
                                info.putExtra("_size", _size);
                                info.putExtra("_duration", _duration);
                                info.putExtra("_author", _author);
                                info.putExtra("image", image);
                                  context.startActivity(info);

                                break;
                            case R.id.delete:

                                break;

                        }

                        return false;
                    }
                });

                popupMenu.show();
                return  true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    public void removeItem(int pos){
        videoArrayList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, videoArrayList.size());
    }
    public void addItem(VideoModel videoModel) {
   videoArrayList.add(videoModel);
   notifyItemInserted(videoArrayList.size());
    }

        @Override
    public void onViewRecycled(@NonNull viewHolder holder) {
        super.onViewRecycled(holder);
    }

    public void updateData(viewHolder holder){
        notifyDataSetChanged();
    }

    public void restoreItem(ArrayList videoArrayList, int pos){
        videoArrayList.add(videoArrayList);
        notifyItemInserted(videoArrayList.size());
    }
    public void animate(RecyclerView.ViewHolder viewHolder){
        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.bounce);
        viewHolder.itemView.setAnimation(animation);
    }

    public class viewHolder extends RecyclerView.ViewHolder {


        public String image;
        AppCompatTextView title, duration, size, details, author;
        AppCompatImageView str_thumb;

        public viewHolder(View itemView) {
            super(itemView);

            title = (AppCompatTextView) itemView.findViewById(R.id.title);
            duration = (AppCompatTextView) itemView.findViewById(R.id.duration);
            str_thumb = (AppCompatImageView) itemView.findViewById(R.id.str_thumb);
            size = (AppCompatTextView) itemView.findViewById(R.id.size);
            details = (AppCompatTextView) itemView.findViewById(R.id.details);
author = (AppCompatTextView) itemView.findViewById(R.id.author);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);

                }
            });
        }

    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onItemClick(int pos, View v);
    }

}