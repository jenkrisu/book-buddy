package net.bookbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Shows welcome message and opens the application.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Called when activity is created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: If app opened for first time, show welcome stuff and encourage to login
        // to access all features of the application
        boolean firstTime = false;
        if (!firstTime) {
            startActivity(new Intent(this, SearchActivity.class));
        }
    }

}
