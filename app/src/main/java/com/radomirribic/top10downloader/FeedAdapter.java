package com.radomirribic.top10downloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "xxxFeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> applications;

    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.applications = applications;
        this.layoutResource=resource;
        this.layoutInflater=LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return applications.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
        convertView=layoutInflater.inflate(layoutResource,parent,false);
        viewHolder=new ViewHolder(convertView);
        convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }



        FeedEntry currentApp=applications.get(position);

        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvSummary.setText(currentApp.getSummary());

        return convertView;
    }

    private class ViewHolder {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        ViewHolder(View v){
            this.tvArtist=v.findViewById(R.id.tvArtist);
            this.tvName=v.findViewById(R.id.tvName);
            this.tvSummary=v.findViewById(R.id.tvSummary);
        }

    }


}
