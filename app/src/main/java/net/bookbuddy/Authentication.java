package net.bookbuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * Shows result of logging in.
 */
public class Authentication extends AppCompatActivity {

    /**
     * Creates activity.
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        boolean loggedIn = true;

        if (loggedIn) {
            findViewById(R.id.layout_auth_success).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.layout_auth_error).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Starts Search Activity.
     *
     * @param view
     */
    public void continueFromLogin(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }

    public void login(View view) {

    }

}
