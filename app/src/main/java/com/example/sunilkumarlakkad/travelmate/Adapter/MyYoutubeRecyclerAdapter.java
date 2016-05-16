package com.example.sunilkumarlakkad.travelmate.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sunilkumarlakkad.travelmate.Activity.HomeActivity;
import com.example.sunilkumarlakkad.travelmate.Model.VideoItem;
import com.example.sunilkumarlakkad.travelmate.R;
import com.squareup.picasso.Picasso;

public class MyYoutubeRecyclerAdapter extends RecyclerView.Adapter<MyYoutubeRecyclerAdapter.ViewHolder> {

    private Context context;
    OnItemListener onItemListener;

    public interface OnItemListener {
        void onItemClicked(int t);
    }

    public void setOnItemClickListener(final OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    public MyYoutubeRecyclerAdapter(Context context) {
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbnail;
        TextView title;
        TextView description;

        public ViewHolder(final View itemView, int ViewType) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
            title = (TextView) itemView.findViewById(R.id.video_title);
            description = (TextView) itemView.findViewById(R.id.video_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemListener != null)
                        onItemListener.onItemClicked(getAdapterPosition());
                }
            });
        }
    }

    @Override
    public MyYoutubeRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_recyclerview_row, parent, false);
        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(MyYoutubeRecyclerAdapter.ViewHolder holder, int position) {
        VideoItem video = HomeActivity.videoItemList.get(position);

        Picasso.with(context).load(video.getThumbnailURL()).into(holder.thumbnail);
        holder.title.setText(video.getTitle());
        holder.description.setText(video.getPublishDate());
    }

    @Override
    public int getItemCount() {
        return HomeActivity.videoItemList.size();
    }
}
