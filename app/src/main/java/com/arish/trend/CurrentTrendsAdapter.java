package com.arish.trend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Arish on 14-01-2016.
 */
public class CurrentTrendsAdapter extends RecyclerView.Adapter<CurrentTrendsAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<TrendData> data = new ArrayList<>();
    private Context mContext;

    public CurrentTrendsAdapter(Context context){
        inflater = LayoutInflater.from(context);
        mContext = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.activity_current_trends_cards_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TrendData trendData = data.get(position);
        holder.mTextView.setText(trendData.title);
        Glide.with(mContext).load(trendData.url).into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mTextView;
        private ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mTextView=(TextView)itemView.findViewById(R.id.current_trend_title);
            mImageView=(ImageView)itemView.findViewById(R.id.current_trend_image);
        }
    }

    public void addData(TrendData newTrendData){
        data.add(newTrendData);
        notifyItemInserted(data.size()-1);
    }
    public void addRefreshData(TrendData newTrendData){
        data.add(0, newTrendData);
        notifyItemInserted(data.size()-1);
    }
}
