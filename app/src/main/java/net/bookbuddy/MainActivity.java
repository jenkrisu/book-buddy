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
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import net.bookbuddy.utilities.GoodreadsApi;
import net.bookbuddy.utilities.Global;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Launches app and handles OAuth authentication.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Request token.
     */
    private OAuth1RequestToken requestToken;

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

        return preferences.contains("loggedIn") && preferences.getBoolean("loggedIn", false);
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
        RequestTokenTask requestTokenTask = new RequestTokenTask();
        requestTokenTask.execute();
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

                    if (this.requestToken != null
                            && this.requestToken.getToken().equals(requestToken)) {
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
                    .build(GoodreadsApi.instance());

            try {
                token = service.getRequestToken();
            } catch (Exception ex) {
                displaySnackbar(getResources().getString(R.string.snackbar_login_error));
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
    }

    /**
     * Handles access token response.
     *
     * @param token OAuth1AccessToken
     */
    private void processAccessTokenResponse(OAuth1AccessToken token) {
        if (token != null) {
            saveAccessPreferences(token);
            UserIdTask userIdTask = new UserIdTask();
            userIdTask.execute(token);
        }
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

            if (requestToken == null) {
                return null;
            }

            OAuth10aService service = new ServiceBuilder()
                    .apiKey(BuildConfig.GOOD_READS_API_KEY)
                    .apiSecret(BuildConfig.GOOD_READS_API_SECRET)
                    .build(GoodreadsApi.instance());

            try {
                token = service.getAccessToken(requestToken, "1");
            } catch (Exception ex) {
                displaySnackbar(getResources().getString(R.string.snackbar_login_error));
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
    }

    /**
     * Handles response from UserIdTask.
     *
     * @param values List user id and name
     */
    private void processUserIdResponse(List<String> values) {
        findViewById(R.id.progressBarLogin).setVisibility(View.GONE);

        if (values != null && values.size() > 0) {
            SharedPreferences.Editor editor =
                    getSharedPreferences(Global.MY_PREFS_NAME, MODE_PRIVATE).edit();

            editor.putBoolean("loggedIn", true);
            editor.putString("userId", values.get(0));

            // Add id
            if (values.size() == 2) {
                editor.putString("userName", values.get(1));
            }

            editor.apply();

            findViewById(R.id.textView_loginSuccess).setVisibility(View.VISIBLE);
            findViewById(R.id.button_continue).setVisibility(View.VISIBLE);

        } else {
            findViewById(R.id.textView_loginInstructions).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_loginButtons).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Contacts server for user id with AsyncTask.
     */
    private class UserIdTask extends AsyncTask<OAuth1AccessToken, Integer, List<String>> {

        /**
         * Gets user id and possibly user name (optional) from GoodReads.
         *
         * @param args array OAuth1AccessToken
         * @return List<String> values user id and name
         */
        @Override
        protected List<String> doInBackground(OAuth1AccessToken... args) {
            List<String> values = new ArrayList<String>();
            OAuth1AccessToken accessToken = args[0];

            OAuth10aService service = new ServiceBuilder()
                    .apiKey(BuildConfig.GOOD_READS_API_KEY)
                    .apiSecret(BuildConfig.GOOD_READS_API_SECRET)
                    .build(GoodreadsApi.instance());

            try {
                OAuthRequest request = new OAuthRequest(Verb.GET,
                        "https://www.goodreads.com/api/auth_user",
                        service);
                service.signRequest(accessToken, request);
                Response response = request.send();

                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                InputSource src = new InputSource();
                src.setCharacterStream(new StringReader(response.getBody()));
                Document doc = builder.parse(src);

                // Get user id
                NamedNodeMap map = doc.getElementsByTagName("user").item(0).getAttributes();
                Node userIdNode = map.getNamedItem("id");
                String id = userIdNode.getNodeValue();
                values.add(id);

                // Get name
                if (doc.getElementsByTagName("name") != null
                        && doc.getElementsByTagName("name").getLength() > 0) {
                    String name = doc.getElementsByTagName("name").item(0).getTextContent();
                    values.add(name);
                }

            } catch (Exception ex) {
                displaySnackbar(getResources().getString(R.string.snackbar_login_error));
                ex.printStackTrace();
            }

            return values;
        }

        /**
         * Returns user id.
         *
         * @param values List
         */
        @Override
        protected void onPostExecute(List<String> values) {
            super.onPostExecute(values);
            processUserIdResponse(values);
        }
    }

    /**
     * Displays SnackBar.
     *
     * @param message String
     */
    private void displaySnackbar(String message) {
        runOnUiThread(() -> {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                    message, Snackbar.LENGTH_LONG);
            snackbar.show();
        });
    }

}
