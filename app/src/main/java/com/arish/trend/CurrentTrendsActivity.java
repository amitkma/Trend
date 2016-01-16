package com.arish.trend;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class CurrentTrendsActivity extends BaseActivity {

    ParseUser currentUser = ParseUser.getCurrentUser();
    String currentUserid;
    boolean liked_temp=false;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private CurrentTrendsAdapter currentTrendsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_trends);

        if (currentUserid != null)
        currentUserid = currentUser.getObjectId();

        Intent intent = getIntent();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        currentTrendsAdapter = new CurrentTrendsAdapter(this);
        setup_toolbar();
        setup_nav_drawer();

        setUpRecyclerView();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CurrentTrendsActivity.this, CreateTrend.class));
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getStringExtra("message") != null) {
            refreshData();
        }
    }

    private void refreshData() {
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("TrendData");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                {
                    ParseObject parseObject = objects.get(objects.size() - 1);
                    TrendData td = new TrendData();
                    td.title = parseObject.getString("trendTitle");
                    ParseFile parseFile = parseObject.getParseFile("trendImage");
                    td.url = parseFile.getUrl();
                    currentTrendsAdapter.addRefreshData(td);
                }
            }
        });
    }

    private void setUpRecyclerView() {
        mRecyclerView.setAdapter(currentTrendsAdapter);
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("TrendData");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (int i = 0; i < objects.size(); i++) {
                    ParseObject parseObject = objects.get(objects.size() - i - 1);
                    TrendData td = new TrendData();
                    td.title = parseObject.getString("trendTitle");
                    td.trendId = parseObject.getObjectId();
                    td.upvoteCounts = parseObject.getNumber("upvotesCount");

                    ParseQuery<ParseObject> query=new ParseQuery<ParseObject>("Likes");
                    query.whereEqualTo("userId",currentUserid);
                    query.whereEqualTo("trendId",td.trendId);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if(objects.size()>0){
                                liked_temp=true;
                            }
                        }
                    });
                    td.liked=liked_temp;
                    ParseFile parseFile = parseObject.getParseFile("trendImage");
                    td.url = parseFile.getUrl();
                    currentTrendsAdapter.addData(td);
                }
            }
        });
    }

}
