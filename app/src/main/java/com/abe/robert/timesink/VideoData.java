package com.abe.robert.timesink;

/**
 * Created by Robby on 4/17/17.
 */

public class VideoData {
    public String videoId;
    public String title;
    public String desc;

    public VideoData(String videoId, String title, String desc) {
        this.videoId = videoId;
        this.title = title;
        this.desc = desc;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "VideoData{" +
                "videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}