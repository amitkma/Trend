package com.arish.trend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ProfileSetup extends BaseActivity {

    private final String LOG_TAG = "TrendLocationApp";
    private EditText nameField;
    private EditText emailField;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolBar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nameField = (EditText) findViewById(R.id.fullName);
        emailField = (EditText) findViewById(R.id.emailAddress);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.put("Name", nameField.getText().toString().trim());
            currentUser.setEmail(emailField.getText().toString().toLowerCase().trim());
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Toast.makeText(getApplicationContext(), "Done! we have updated your details.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileSetup.this, CurrentTrendsActivity.class);
                    finish();
                    startActivity(intent);

                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
