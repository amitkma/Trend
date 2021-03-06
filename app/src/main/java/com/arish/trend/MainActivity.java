package com.arish.trend;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.transition.Fade;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends BaseActivity {

    private Button loginButton;
    private Button signupButton;
    private EditText usernameField;
    private EditText userPasswordField;


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
        setContentView(R.layout.activity_main);

        loginButton = (Button) findViewById(R.id.loginButtonId);
        signupButton = (Button) findViewById(R.id.signupButtonId);
        usernameField = (EditText) findViewById(R.id.username);
        userPasswordField = (EditText) findViewById(R.id.userPassword);




        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_connection() == false)
                    Snackbar.make(findViewById(R.id.coordinatorlay), R.string.network_state_false, Snackbar.LENGTH_SHORT).show();
                else if (usernameField.getText().toString().trim().length() != 0 || userPasswordField.getText().toString().trim().length() != 0)
                    loginParse();
                else {

                    Snackbar.make(findViewById(R.id.coordinatorlay), R.string.empty, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_connection() == false)
                    Snackbar.make(findViewById(R.id.coordinatorlay), R.string.network_state_false, Snackbar.LENGTH_SHORT).show();
                else if (usernameField.getText().toString().trim().length() != 0 && userPasswordField.getText().toString().trim().length() !=0)
                    signupParse();
                else if (userPasswordField.getText().toString().trim().length() == 0)
                    Snackbar.make(findViewById(R.id.coordinatorlay), R.string.empty, Snackbar.LENGTH_SHORT).show();

            }
        });


    }





    private void loginParse() {
        ParseUser.logInInBackground(usernameField.getText().toString().trim(), userPasswordField.getText().toString().trim(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(MainActivity.this, CurrentTrendsActivity.class);
                    finish();
                    startActivity(intent);
                } else {
                    switch (e.getCode()) {
                        case ParseException.OBJECT_NOT_FOUND:
                            Snackbar.make(findViewById(R.id.coordinatorlay), e.getMessage(), Snackbar.LENGTH_LONG).show();
                            break;
                        case ParseException.CONNECTION_FAILED:
                            Snackbar.make(findViewById(R.id.coordinatorlay), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Snackbar.make(findViewById(R.id.coordinatorlay), R.string.error_unclassified, Snackbar.LENGTH_SHORT).show();
                            e.printStackTrace();
                            break;
                    }
                }
            }
        });
    }

    private void signupParse() {

        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(usernameField.getText().toString().trim());
        parseUser.setPassword(userPasswordField.getText().toString().trim());
        parseUser.put("Name", "Default");
        parseUser.put("uri", "Default");
        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(MainActivity.this, ProfileSetup.class);

                    startActivity(intent);
                    finish();
                } else {
                    switch (e.getCode()) {
                        case ParseException.USERNAME_TAKEN:
                            Snackbar.make(findViewById(R.id.coordinatorlay), e.getMessage(), Snackbar.LENGTH_LONG).show();
                            break;
                        case ParseException.CONNECTION_FAILED:
                            Snackbar.make(findViewById(R.id.coordinatorlay), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Snackbar.make(findViewById(R.id.coordinatorlay), R.string.error_unclassified, Snackbar.LENGTH_SHORT).show();
                            e.printStackTrace();
                            break;
                    }

                }


            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
