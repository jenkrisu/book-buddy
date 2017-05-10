package net.bookbuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

/**
 * Shows all user shelves.
 */
public class ShelvesActivity extends BaseActivity implements DownloadCallback {

    /**
     * Saves userId.
     */
    private String userId;

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
            userId = preferences.getString("userId", "");

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

    /**
     * Handles result of fetching shelves.
     *
     * @param result DownloadTask.Result with document and status
     */
    @Override
    public void processFinish(DownloadXmlTask.Result result) {
        List<Shelf> shelves = new ArrayList<Shelf>();

        if (result.status == 200 && result.document != null) {
            shelves = BookShelfParser.docToShelves(result.document);
            setTitle("All Shelves (" + shelves.size() + ")");
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

        addFooter(listView);

        ListAdapter customAdapter = new BookShelfAdapter(shelves, this);
        listView.setAdapter(customAdapter);

        // Add listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obs! Footer at last position but it is non-clickable.
                Intent intent =
                        new Intent(getApplicationContext(), ShelfActivity.class);
                intent.putExtra("shelf", shelves.get(position));
                startActivity(intent);
            }
        });

        findViewById(R.id.progressBarShelves).setVisibility(View.GONE);
        findViewById(R.id.listViewShelves).setVisibility(View.VISIBLE);
    }

    /**
     * Creates footer.
     *
     * @param listView ListView
     */
    private void addFooter(ListView listView) {
        View footer = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.list_item_shelf_footer, null, false);

        listView.addFooterView(footer, "Footer", false);

        // Set attribution link
        TextView goodreads = (TextView) findViewById(R.id.textViewGoodreadsDataShelves);
        String url = "https://www.goodreads.com/user/show/" + userId;
        String attribution = "Shelves on <a href='" + url + "'>Goodreads</a>";
        goodreads.setClickable(true);
        goodreads.setMovementMethod(LinkMovementMethod.getInstance());
        goodreads.setText(Html.fromHtml(attribution));
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
