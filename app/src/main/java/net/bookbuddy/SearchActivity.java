package net.bookbuddy;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.bookbuddy.adapters.BookSearchAdapter;
import net.bookbuddy.data.Work;
import net.bookbuddy.utilities.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Search Activity for searching books.
 */
public class SearchActivity extends BaseActivity implements DownloadCallback {

    /**
     * Shows list items.
     */
    private ListView listView;

    /**
     * User search query.
     */
    private String query;

    /**
     * Creates activity
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Add navigation drawer
        super.onCreateDrawer();

        // Handle search
        handleIntent(getIntent());

        // Create ListView
        createListView();
    }

    /**
     * Creates ListView for search results.
     */
    private void createListView() {
        // Create ListView
        listView = (ListView) findViewById(R.id.listViewSearchBooks);

        // Add non selectable header to ListView
        View footer = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.list_item_footer, null, false);

        listView.addFooterView(footer, "Footer", false);

        // Add non selectable footer to ListView
        View header = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.list_item_header, null, false);

        listView.addHeaderView(header, "Header", false);
    }

    /**
     * Creates options menu with SearchView.
     *
     * @param menu Menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        // Set query hint
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        // On search create new intent ACTION_SEARCH
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));

        return true;
    }

    /**
     * Calls handleIntent on new intent.
     *
     * @param intent intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /**
     * Handles intent.
     *
     * @param intent Intent
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            // Remove instructions and show spinner
            findViewById(R.id.viewSearchInstructions).setVisibility(View.GONE);
            findViewById(R.id.progressBarSearchBooks).setVisibility(View.VISIBLE);

            // Get query words
            this.query = intent.getStringExtra(SearchManager.QUERY);

            // Task for getting book search results
            DownloadXmlTask downloadXmlTask = new DownloadXmlTask();
            downloadXmlTask.callback = this;

            Uri uri = Uri.parse("http://www.goodreads.com/search/index")
                    .buildUpon()
                    .appendQueryParameter("key", BuildConfig.GOOD_READS_API_KEY)
                    .appendQueryParameter("q", this.query)
                    .build();

            try {
                downloadXmlTask.execute(new URL(uri.toString()));
            } catch (MalformedURLException ex) {
                displaySnackbar();
                ex.printStackTrace();
            }
        }
    }

    /**
     * Handles DownloadXmlTask results.
     *
     * @param result DownloadTask.Result contains document and status
     */
    @Override
    public void processFinish(DownloadXmlTask.Result result) {
        List<Work> works = new ArrayList<>();

        // Get results from xml document
        if (result.document != null && result.status == 200) {

            if (result.document.getElementsByTagName("total-results") != null) {
                works = parseResults(result.document);

            } else {
                displaySnackbar();
            }
        } else {
            displaySnackbar();
        }

        // Hide spinner
        findViewById(R.id.progressBarSearchBooks).setVisibility(View.GONE);

        // Show results
        if (works.size() > 0) {
            addAdapterAndListener(works);
        }
    }

    /**
     * Adds adapter and listener to ListView.
     *
     * @param works works to show
     */
    private void addAdapterAndListener(List<Work> works) {
        // Add adapter to ListView
        ListAdapter customAdapter = new BookSearchAdapter(works, this);
        listView.setAdapter(customAdapter);

        // Add listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obs! Header is at position 0 and footer at last position.
                int index = position - 1;
                if (index > -1 && index < works.size()) {
                    Intent intent = new Intent(getApplicationContext(), BookActivity.class);
                    intent.putExtra("work", works.get(index));
                    startActivity(intent);

                }
            }
        });
    }

    /**
     * Parses work results from document and shows result messages.
     *
     * @param doc Xml document
     * @return list of works
     */
    private List<Work> parseResults(Document doc) {
        List<Work> works = new ArrayList<>();

        // Find how many results
        NodeList nodeList = doc.getElementsByTagName("total-results");
        int totalResults = Integer.parseInt(nodeList.item(0).getTextContent());

        // Display amount of results for query
        displayTotalResultsMessage(totalResults);

        if (totalResults > 0) {
            works = BookSearchResultsParser.docToWorks(doc);
        }

        return works;
    }

    /**
     * Displays results message about search results.
     *
     * @param totalResults amount of results
     */
    private void displayTotalResultsMessage(int totalResults) {
        TextView textView = (TextView) findViewById(R.id.textViewSearchMessage);
        TextView goodReads = (TextView) findViewById(R.id.textViewGoodReadsData);
        String message = "";

        if (totalResults > 0) {

            if (totalResults < 20) {
                message = "Found " + totalResults
                        + " results for search \"" + this.query + "\".";
            } else {
                message = "Found " + totalResults
                        + " results for search \"" + this.query + "\"."
                        + " Showing 20 best results.";
            }

            // Add GoodReads attribution and link to data source
            String url = createGoodReadsAttribution();
            String attribution = "Results from <a href='" + url + "'>GoodReads</a>";
            goodReads.setClickable(true);
            goodReads.setMovementMethod(LinkMovementMethod.getInstance());
            goodReads.setText(Html.fromHtml(attribution));

        } else {
            message = "No results found for search \"" + this.query + "\".";
        }

        // Set message
        textView.setText(message);
    }

    /**
     * Creates good reads attribution link.
     *
     * @return String link
     */
    private String createGoodReadsAttribution() {
        Uri uri = Uri.parse("http://www.goodreads.com/search")
                .buildUpon()
                .appendQueryParameter("q", this.query)
                .build();
        return uri.toString();
    }

    /**
     * Displays Snacbar with error message.
     */
    private void displaySnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                getResources().getText(R.string.snackbar_load_error), Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
