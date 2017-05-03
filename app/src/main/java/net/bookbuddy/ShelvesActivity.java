package net.bookbuddy;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import net.bookbuddy.adapters.BookShelfAdapter;
import net.bookbuddy.data.Shelf;
import net.bookbuddy.utilities.BookShelfParser;
import net.bookbuddy.utilities.DownloadCallback;
import net.bookbuddy.utilities.DownloadXmlTask;
import net.bookbuddy.utilities.Global;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

            DownloadXmlTask downloadXmlTask = new DownloadXmlTask();
            downloadXmlTask.callback = this;

            try {
                downloadXmlTask.execute(new URL(uri.toString()));
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void processFinish(DownloadXmlTask.Result result) {
        List<Shelf> shelves = new ArrayList<Shelf>();

        if (result.status == 200 && result.document != null) {
            shelves = BookShelfParser.docToShelves(result.document);
        } else {
            displaySnackbar();
        }

        createListView(shelves);
    }

    /**
     * Creates ListView of shelves.
     *
     * @param shelves List shelves
     */
    private void createListView(List<Shelf> shelves) {
        ListView listView = (ListView) findViewById(R.id.listViewShelves);

        ListAdapter customAdapter = new BookShelfAdapter(shelves, this);
        listView.setAdapter(customAdapter);

        findViewById(R.id.progressBarShelves).setVisibility(View.GONE);
        findViewById(R.id.listViewShelves).setVisibility(View.VISIBLE);
    }

    /**
     * Displays Snackbar with error message.
     */
    private void displaySnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                getResources().getText(R.string.snackbar_load_error), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
