package com.app.vidplayer.video;
import android.net.Uri;

public class VideoModel  {

    String videoTitle;
    String videoDuration;
    String str_thumb;
    String size;
    String view;
    String author;
    Uri videoUri;


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSize(){
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


    public String getResolution(){
        return view;
    }

    public void setResolution(String view) {
        this.view = view;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }
    public String getStr_thumb() {
        return str_thumb;
    }
    public void setStr_thumb( String str_thumb) {
        this.str_thumb = str_thumb;
    }


}