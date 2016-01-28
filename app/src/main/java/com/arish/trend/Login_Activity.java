package com.arish.trend;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;

public class Login_Activity extends AppCompatActivity {
    TextView login;
    TextView marquee_login;
    TextView trend;
    List<String> permissions;
    private CallbackManager callbackManager;
    private Button fb_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition fade = new Fade();
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);
            getWindow().setExitTransition(fade);

            getWindow().setEnterTransition(fade);
        }
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_login_);


        if (ParseUser.getCurrentUser() != null) {
            startActivity(new Intent(this, CurrentTrendsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY));
        } else if (Profile.getCurrentProfile() != null)
            startActivity(new Intent(this, CurrentTrendsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY));



        login = (TextView) findViewById(R.id.login);
        marquee_login = (TextView) findViewById(R.id.MarqueeText);
        trend = (TextView) findViewById(R.id.textView2);
        fb_button = (Button) findViewById(R.id.login_button);
        marquee_login.setSelected(true);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startloginactivity();

            }
        });
        callbackManager = CallbackManager.Factory.create();
        permissions = Arrays.asList("email");

        // App code

        fb_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parse_fb();
            }
        });
    }

    private void parse_fb() {
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, com.parse.ParseException e) {

                if (e != null)
                    e.printStackTrace();

                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    user.put("Name", "Default");
                    user.put("uri", "Default");
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            start_current_trends_activity();
                            Toast.makeText(getApplicationContext(), "Signed up successfully through Facebook !", Toast.LENGTH_SHORT);
                            Log.d("MyApp", "User signed up and logged in through Facebook!");
                        }
                    });

                } else {
                    start_current_trends_activity();
                    Log.d("MyApp", "User logged in through Facebook!");

                }

            }
        });
    }

    private void start_current_trends_activity() {
        startActivity(new Intent(this, CurrentTrendsActivity.class));
        finish();
    }

    private void startloginactivity() {
        Intent i = new Intent(this, MainActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, trend, getString(R.string.activity_image_trans));
            startActivity(i, options.toBundle());


        } else {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
