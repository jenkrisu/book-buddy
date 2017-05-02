package net.bookbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Shows welcome message and opens the application.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Creates activity.
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: If app opened for first time, show welcome stuff and encourage to login
        // to access all features of the application
        boolean loggedIn = false;
        if (loggedIn) {
            startActivity(new Intent(this, SearchActivity.class));
        }
    }

    public void skip(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }

    public void login(View view) {

    }

}
