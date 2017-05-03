package net.bookbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;

import net.bookbuddy.utilities.GoodReadsApi;
import net.bookbuddy.utilities.Global;


/**
 * Shows welcome message and opens the application.
 */
public class MainActivity extends AppCompatActivity {

    private OAuth1RequestToken requestToken;

    private OAuth1AccessToken accessToken;

    /**
     * Creates activity.
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (hasLoggedIn()) {
            startActivity(new Intent(this, SearchActivity.class));
        } else {
            // Handle OAuth response
            Intent intent = getIntent();
            handleOAuthResponse(intent);
        }
    }

    /**
     * Saves request token and secret to shared preferences if user has chosen to login.
     */
    @Override
    protected void onPause() {
        super.onPause();
        saveRequestPreferences();
    }

    /**
     * Checks if user has logged in previously.
     *
     * @return boolean logged in or not
     */
    private boolean hasLoggedIn() {
        SharedPreferences preferences =
                getApplicationContext().getSharedPreferences(Global.MY_PREFS_NAME, MODE_PRIVATE);

        return preferences.contains("accessToken") && preferences.contains("accessTokenSecret");
    }

    /**
     * Starts Search Activity.
     *
     * @param view View
     */
    public void continueToApp(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }

    /**
     * Starts RequestTokenTask.
     *
     * @param view View
     */
    public void login(View view) {
        removeRequestPreferences();
        RequestTokenTask requestTokenTask = new RequestTokenTask();
        requestTokenTask.execute();
    }

    /**
     * Removes request token and secret from shared preferences.
     */
    private void removeRequestPreferences() {
        SharedPreferences.Editor editor =
                getSharedPreferences(Global.MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.remove("requestToken");
        editor.remove("requestTokenSecret");
        editor.apply();
    }

    /**
     * Saves request token and secret to shared preferences.
     */
    private void saveRequestPreferences() {
        if (this.requestToken != null) {
            SharedPreferences.Editor editor =
                    getSharedPreferences(Global.MY_PREFS_NAME, MODE_PRIVATE).edit();

            editor.putString("requestToken", this.requestToken.getToken());
            editor.putString("requestTokenSecret", this.requestToken.getTokenSecret());

            editor.apply();
        }
    }

    /**
     * Fetches request token and secret from shared preferences.
     */
    private void getRequestPreferences() {
        SharedPreferences preferences =
                getApplicationContext().getSharedPreferences(Global.MY_PREFS_NAME, MODE_PRIVATE);

        if (preferences.contains("requestToken") && preferences.contains("requestTokenSecret")) {
            String token = preferences.getString("requestToken", "");
            String secret = preferences.getString("requestTokenSecret", "");
            this.requestToken = new OAuth1RequestToken(token, secret);
        }
    }

    /**
     * Checks if app is opened from OAuth response.
     *
     * @param intent Intent
     */
    private void handleOAuthResponse(Intent intent) {
        Uri uri = intent.getData();

        if (uri != null) {
            String authorize = uri.getQueryParameter("authorize");
            String requestToken = uri.getQueryParameter("oauth_token");

            if (authorize != null && requestToken != null) {
                // If 0, user denied access
                if (authorize.equals("0")) {
                    findViewById(R.id.textView_loginInstructions).setVisibility(View.GONE);
                    findViewById(R.id.textView_loginCancel).setVisibility(View.VISIBLE);
                    // If 1, user granted access
                } else if (authorize.equals("1")) {
                    findViewById(R.id.textView_loginInstructions).setVisibility(View.GONE);
                    findViewById(R.id.layout_loginButtons).setVisibility(View.GONE);
                    findViewById(R.id.progressBarLogin).setVisibility(View.VISIBLE);

                    // Fetches request token and request token secret from shared preferences
                    getRequestPreferences();

                    if (this.requestToken != null) {
                        // Gets access token
                        AccessTokenTask accessTokenTask = new AccessTokenTask();
                        accessTokenTask.execute(this.requestToken);
                    }
                }
            }
        }
    }

    /**
     * Handles response from RequestTokenTask.
     *
     * @param token OAuth1RequestToken
     */
    private void processRequestTokenResponse(OAuth1RequestToken token) {
        if (token != null) {
            this.requestToken = token;
            Uri uri = Uri.parse("http://goodreads.com/oauth/authorize")
                    .buildUpon()
                    .appendQueryParameter("oauth_token", token.getToken())
                    .appendQueryParameter("mobile", "1")
                    .build();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    /**
     * Contacts server for request token with AsyncTask.
     */
    private class RequestTokenTask extends AsyncTask<String, Integer, OAuth1RequestToken> {

        /**
         * Contacts GoodReads for request token.
         *
         * @param args String (none)
         * @return OAuth1RequestToken
         */
        @Override
        protected OAuth1RequestToken doInBackground(String... args) {
            OAuth1RequestToken token = null;

            OAuth10aService service = new ServiceBuilder()
                    .apiKey(BuildConfig.GOOD_READS_API_KEY)
                    .apiSecret(BuildConfig.GOOD_READS_API_SECRET)
                    .build(GoodReadsApi.instance());

            try {
                token = service.getRequestToken();


            } catch (Exception ex) {
                displaySnackBar(getResources().getString(R.string.snackbar_login_error));
                ex.printStackTrace();
            }

            return token;
        }

        /**
         * Returns request token.
         *
         * @param token OAuth1RequestToken
         */
        @Override
        protected void onPostExecute(OAuth1RequestToken token) {
            super.onPostExecute(token);
            processRequestTokenResponse(token);
        }

        /**
         * Displays SnackBar.
         *
         * @param message String
         */
        private void displaySnackBar(String message) {
            runOnUiThread(() -> {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                        message, Snackbar.LENGTH_LONG);
                snackbar.show();
            });
        }
    }

    /**
     * Handles access token response.
     *
     * @param token OAuth1AccessToken
     */
    private void processAccessTokenResponse(OAuth1AccessToken token) {
        if (token != null) {
            this.accessToken = token;
            saveAccessPreferences(token);
            findViewById(R.id.progressBarLogin).setVisibility(View.GONE);
            findViewById(R.id.textView_loginSuccess).setVisibility(View.VISIBLE);
            findViewById(R.id.button_continue).setVisibility(View.VISIBLE);
        }

        // TODO: Another asynctask to get the user id... THEN user can be logged in.
    }

    /**
     * Saves access token and secret to shared preferences.
     *
     * @param token OAuth1AccessToken access token
     */
    private void saveAccessPreferences(OAuth1AccessToken token) {
        SharedPreferences.Editor editor =
                getSharedPreferences(Global.MY_PREFS_NAME, MODE_PRIVATE).edit();

        editor.putString("accessToken", token.getToken());
        editor.putString("accessTokenSecret", token.getTokenSecret());

        editor.apply();
    }

    /**
     * Contacts server for access token with AsyncTask.
     */
    private class AccessTokenTask extends AsyncTask<OAuth1RequestToken, Integer, OAuth1AccessToken> {

        /**
         * Contacts GoodReads for access token.
         *
         * @param args OAuth1RequestToken, request token
         * @return OAuth1AccessToken
         */
        @Override
        protected OAuth1AccessToken doInBackground(OAuth1RequestToken... args) {
            OAuth1AccessToken token = null;
            OAuth1RequestToken requestToken = args[0];

            OAuth10aService service = new ServiceBuilder()
                    .apiKey(BuildConfig.GOOD_READS_API_KEY)
                    .apiSecret(BuildConfig.GOOD_READS_API_SECRET)
                    .build(GoodReadsApi.instance());

            try {
                token = service.getAccessToken(requestToken, "1");
            } catch (Exception ex) {
                displaySnackBar(getResources().getString(R.string.snackbar_login_error));
                ex.printStackTrace();
            }

            return token;
        }

        /**
         * Returns access token.
         *
         * @param token OAuth1AccessToken
         */
        @Override
        protected void onPostExecute(OAuth1AccessToken token) {
            super.onPostExecute(token);
            processAccessTokenResponse(token);
        }

        /**
         * Displays SnackBar.
         *
         * @param message String
         */
        private void displaySnackBar(String message) {
            runOnUiThread(() -> {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                        message, Snackbar.LENGTH_LONG);
                snackbar.show();
            });
        }
    }

}
