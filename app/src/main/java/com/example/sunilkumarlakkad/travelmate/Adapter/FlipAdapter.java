package com.example.sunilkumarlakkad.travelmate.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sunilkumarlakkad.travelmate.Activity.HomeActivity;
import com.example.sunilkumarlakkad.travelmate.R;
import com.squareup.picasso.Picasso;
import com.yelp.clientlib.entities.Business;

public class FlipAdapter extends BaseAdapter {

    public interface Callback {
        void onPageRequested(int page);
    }


    private LayoutInflater inflater;
    private Callback callback;
    Context context;

    public FlipAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return HomeActivity.businessList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.attraction_recyclerview_row, parent, false);

            holder.thumbnail = (ImageView) convertView.findViewById(R.id.imgAttraction);
            holder.rating = (ImageView) convertView.findViewById(R.id.ar_rating);
            holder.title = (TextView) convertView.findViewById(R.id.ar_name);
            holder.categories = (TextView) convertView.findViewById(R.id.ar_categories);
            holder.timing = (TextView) convertView.findViewById(R.id.ar_timing);
            holder.description = (TextView) convertView.findViewById(R.id.ar_description);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Business business = HomeActivity.businessList.get(position);
        Picasso.with(context).load(business.imageUrl()).into(holder.thumbnail);
        Picasso.with(context).load(business.ratingImgUrlLarge()).into(holder.rating);
        holder.title.setText(business.name());
        holder.categories.setText(business.categories().get(0).name());
        holder.description.setText(business.snippetText());
        if (business.isClosed()) {
            holder.timing.setText(R.string.closed);
            holder.timing.setTextColor(Color.RED);
        } else {
            holder.timing.setText(R.string.open);
            holder.timing.setTextColor(Color.GREEN);
        }
        holder.description.setText(business.snippetText());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onPageRequested(position);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        ImageView thumbnail, rating;
        TextView title, categories, timing, description;
    }
}
