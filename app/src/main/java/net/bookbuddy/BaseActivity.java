package net.bookbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import net.bookbuddy.utilities.Global;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Creates drawer.
     */
    protected void onCreateDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Set login or logout button
        Menu menu = navigationView.getMenu();
        if (showLogin()) {
            menu.findItem(R.id.nav_logout).setVisible(true);
        } else {
            menu.findItem(R.id.nav_login).setVisible(true);
        }

        // Sets user name to drawer
        setUserName(navigationView);

        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Closes drawer if it is open and back button is pressed.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Creates options menu.
     *
     * @param menu menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Handles options item selections.
     *
     * @param item selected item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handles navigation item selection.
     *
     * @param item selected item
     * @return boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_search_books:
                startActivity(new Intent(this, SearchActivity.class));
            case R.id.nav_bookshelves:
                break;
            case R.id.nav_to_read:
                break;
            case R.id.nav_reading:
                break;
            case R.id.nav_read:
                break;
            case R.id.nav_login:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.nav_logout:
                removeTokenPreferences();
                startActivity(new Intent(this, MainActivity.class));
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Sets user name to navigation drawer.
     *
     * @param navigationView NavigationView
     */
    private void setUserName(NavigationView navigationView) {
        SharedPreferences preferences =
                getApplicationContext().getSharedPreferences(Global.MY_PREFS_NAME, MODE_PRIVATE);

        if (preferences.contains("userName")) {
            View header = navigationView.getHeaderView(0);
            TextView textView = (TextView) header.findViewById(R.id.textView_drawerName);
            textView.setText(preferences.getString("userName", ""));
        }
    }

    /**
     * Removes request and access tokens and secrets from shared preferences.
     */
    private void removeTokenPreferences() {
        SharedPreferences.Editor editor =
                getSharedPreferences(Global.MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Checks whether shared preferences contain access tokens.
     *
     * @return boolean access token found or not
     */
    private boolean showLogin() {
        SharedPreferences preferences =
                getApplicationContext().getSharedPreferences(Global.MY_PREFS_NAME, MODE_PRIVATE);

        return preferences.contains("loggedIn") && preferences.getBoolean("loggedIn", false);
    }

}
