package com.arish.trend;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.parse.ParseUser;

/**
 * Created by Ayush on 10-Jan-16.
 */
public class BaseActivity extends AppCompatActivity {

    ListView listview;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ParseUser currentUser;
    int Temp;
    private Toolbar toolbar;

    public void setup_toolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }



    //Navigatio Drawer
    public void setup_nav_drawer() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View view = navigationView.inflateHeaderView(R.layout.nav_header);
        navigationView.inflateMenu(R.menu.nav_menu);
        Temp = 0;
        ImageView imageView_editprof = (ImageView) view.findViewById(R.id.nav_header_imageView_editprofile);
        imageView_editprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Temp == 0) {
                    navigationView.getMenu().clear();
                    //navigationView.getMenu().findItem(R.menu.nav_menu).setVisible(false);
                    navigationView.inflateMenu(R.menu.nav_menu_profile_settings);
                    Temp = 1;

                } else {
                    Temp = 0;
                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.nav_menu);
                }
            }
        });

        //listview=(ListView)view1.findViewById(R.id.listView_nav_menu);
        String[] array = {"a", "b", "c"};
        // listview.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,android.R.id.text1,array));
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                //super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setup_nav_item_listener();
    }

    public void setup_nav_item_listener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
              /*  if (menuItem.isChecked())
                    menuItem.setChecked(false);
                else
                    menuItem.setChecked(true);*/


                switch (menuItem.getItemId()) {

                    case R.id.About:
                        drawerLayout.closeDrawers();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, 150);
                        return true;

                    case R.id.logout:

                        if (check_connection()) {
                            drawerLayout.closeDrawers();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    logout();
                                }
                            }, 200);
                        }
                        return true;

                    case R.id.Feedback:
                        drawerLayout.closeDrawers();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, 500);
                        return true;


                    default:

                        return true;

                }
            }
        });
    }

    public boolean check_connection() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

//For 3G check
        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();
//For WiFi Check
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();

        System.out.println(is3g + " net " + isWifi);

        if (!is3g && !isWifi) {

            return false;
        } else {

            return true;
        }


    }

    private void logout() {
        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            ParseUser.logOutInBackground();
        } else {
            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
                LoginManager.getInstance().logOut();
            }
        }


        startActivity(new Intent(this, Login_Activity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY));
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);



    }
}
