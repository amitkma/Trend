package com.arish.trend;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.parse.ParseInstallation;
import com.parse.ParsePush;

public class CurrentTrendsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_trends);

        ParsePush.subscribeInBackground("TrendShoes");
        setup_toolbar();
        setup_nav_drawer();
        setup_nav_item_listener();
        View view=navigationView.inflateHeaderView(R.layout.nav_header);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParsePush push = new ParsePush();
                push.setChannel("TrendShoes");
                push.setMessage("Push has been configured. Let's build something awesome");
                push.sendInBackground();
               //startActivity(new Intent(CurrentTrendsActivity.this, CreateTrend.class));
            }
        });
    }

}
