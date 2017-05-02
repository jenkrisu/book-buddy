package net.bookbuddy;

import android.content.Intent;
import android.net.Uri;
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

    /**
     * Starts Search Activity.
     *
     * @param view
     */
    public void skip(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }

    public void login(View view) {
        /*final OAuth10aService service = new ServiceBuilder()
                .apiKey(BuildConfig.GOOD_READS_API_KEY)
                .apiSecret(BuildConfig.GOOD_READS_API_SECRET)
                .build(GoodReadsApi.instance());

        Token requestToken = null;

        try {
            requestToken = service.getRequestToken();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println(requestToken.toString());*/

        /*Uri uri = Uri.parse("http://goodreads.com/oauth/authorize")
                .buildUpon()
                .appendQueryParameter("key", BuildConfig.GOOD_READS_API_KEY)
                .appendQueryParameter("mobile", "1")
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);*/
    }

}
