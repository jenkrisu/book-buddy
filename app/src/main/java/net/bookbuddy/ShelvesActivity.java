package net.bookbuddy;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import net.bookbuddy.utilities.DownloadCallback;
import net.bookbuddy.utilities.DownloadTask;
import net.bookbuddy.utilities.Global;

import java.net.MalformedURLException;
import java.net.URL;

public class ShelvesActivity extends BaseActivity implements DownloadCallback {

    /**
     * Creates activity.
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelves);

        // Add navigation drawer
        super.onCreateDrawer();
        findViewById(R.id.progressBarShelves).setVisibility(View.VISIBLE);

        SharedPreferences preferences =
                getApplicationContext().getSharedPreferences(Global.MY_PREFS_NAME, MODE_PRIVATE);

        if (preferences.contains("userId")) {

            String userId = preferences.getString("userId", "");
            System.out.println("userId: " + userId);

            Uri uri = Uri.parse("https://www.goodreads.com/shelf/list")
                    .buildUpon()
                    .appendQueryParameter("key", BuildConfig.GOOD_READS_API_KEY)
                    .appendQueryParameter("user_id", userId)
                    .build();

            DownloadTask downloadTask = new DownloadTask();
            downloadTask.callback = this;

            try {
                downloadTask.execute(new URL(uri.toString()));
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void processFinish(DownloadTask.Result result) {
        System.out.println("STATUS:" + result.status);
        findViewById(R.id.progressBarShelves).setVisibility(View.GONE);
    }

}
