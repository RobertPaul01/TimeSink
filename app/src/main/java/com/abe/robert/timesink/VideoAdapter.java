package com.abe.robert.timesink;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Abe on 4/18/2017.
 */

public class VideoAdapter extends ArrayAdapter<VideoData> implements View.OnClickListener {
    public VideoAdapter(@NonNull Context context, ArrayList<VideoData> videos) {
        super(context, 0, videos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VideoData video = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_video, parent, false);
        }
        // Lookup view for data population
        TextView tvTitle = (TextView) convertView.findViewById(R.id.title);
        TextView tvLength = (TextView) convertView.findViewById(R.id.desc);
        // Populate the data into the template view using the data object
        tvTitle.setText(video.getTitle());
        tvLength.setText(video.getDesc());

        return convertView;
    }

    @Override
    public void onClick(View v) {

    }
}
