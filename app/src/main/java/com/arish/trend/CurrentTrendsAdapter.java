package com.arish.trend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arish on 14-01-2016.
 */
public class CurrentTrendsAdapter extends RecyclerView.Adapter<CurrentTrendsAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<TrendData> data = new ArrayList<>();
    private Context mContext;
    private TrendData currentTrendData;

    public CurrentTrendsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.trend_card_with_image, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        currentTrendData = data.get(position);
        if (currentTrendData.user != null)
        holder.mUserTextView.setText(currentTrendData.user);
        if (currentTrendData.date != null) {
            String fDate = new SimpleDateFormat("dd MMM,yy").format(currentTrendData.date);
            holder.mDateTextView.setText(fDate);
        }
        holder.mTitleTextView.setText(currentTrendData.title);
        holder.mCountTextView.setText(currentTrendData.upvoteCounts.toString());

       // Log.d("boolean_liked", String.valueOf(currentTrendData.liked));
        if (currentTrendData.liked) {
            holder.mLikeImageView.setImageResource(R.drawable.ic_favorited_icon);
            holder.mLikeImageView.setTag(R.drawable.ic_favorited_icon);
        }

            holder.mLikeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.mLikeImageView.setImageResource(R.drawable.ic_favorited_icon);
                    int likes = data.get(position).upvoteCounts.intValue() + 1;
                    holder.mCountTextView.setText(Integer.toString(likes));
                    //  holder.mLikeImageView.setImageResource(R.drawable.ic_favorited_icon);
                    ParseObject likeParseObject = new ParseObject("Likes");
                    likeParseObject.put("trendId", data.get(position).trendId);
                    likeParseObject.put("userId", ParseUser.getCurrentUser().getObjectId());
                    likeParseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("TrendData");
                            Log.e("current object ID", data.get(position).trendId);
                            parseQuery.getInBackground(data.get(position).trendId, new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    Log.e("objectid", object.getObjectId());
                                    object.increment("upvotesCount");
                                    object.saveInBackground();
                                    ParsePush newLikePush = new ParsePush();
                                    JSONObject objectLike = new JSONObject();
                                    try {
                                        objectLike.put("is_background", false);
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("message", "Hello! A new like is here!!!");
                                        jsonObject.put("title", "Trend");
                                        jsonObject.put("category", Constants.PUSH_FOR_LIKE);
                                        objectLike.put("data", jsonObject);
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }


                                    newLikePush.setData(objectLike);
                                    newLikePush.sendInBackground();
                                }
                            });
                        }
                    });
                }
            });
        Glide.with(mContext).load(currentTrendData.url).into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void refreshData() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("TrendData");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (int i = 0; i < objects.size(); i++) {
                    Log.e("DATA_LENGTH", Integer.toString(data.size()));
                    data.get(i).upvoteCounts = objects.get(objects.size() - i - 1).getNumber("upvotesCount");
                    notifyItemChanged(i);
                }
            }
        });
    }

    public void addData(TrendData newTrendData) {
        data.add(newTrendData);
        notifyItemInserted(data.size() - 1);
    }

    public void addRefreshData(TrendData newTrendData) {
        data.add(0, newTrendData);
        notifyItemInserted(data.size() - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mDateTextView;
        private TextView mUserTextView;
        private TextView mTitleTextView;
        private TextView mCountTextView;
        private ImageView mImageView;
        private ImageView mLikeImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.trend_description_id);
            mUserTextView = (TextView) itemView.findViewById(R.id.name_trend_created_by);
            mDateTextView = (TextView) itemView.findViewById(R.id.time_trend_created_at);
            mImageView = (ImageView) itemView.findViewById(R.id.current_trend_image);
            mLikeImageView = (ImageView) itemView.findViewById(R.id.trend_like_icon_id);
            mCountTextView = (TextView) itemView.findViewById(R.id.trend_upvote_count);
        }
    }
}
