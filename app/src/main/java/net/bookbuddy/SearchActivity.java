package net.bookbuddy;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import net.bookbuddy.utilities.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {

    /**
     * Shows list items.
     */
    private ListView listView;

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

        // Add header to ListView
        View footer = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.list_item_work_footer, null, false);

        listView.addFooterView(footer);

        // Add footer to ListView
        View header = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.list_item_work_header, null, false);

        listView.addHeaderView(header);
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

            String query = intent.getStringExtra(SearchManager.QUERY);
            //New AsyncTask for searching books
            Task task = new Task();
            task.execute(query);
        }
    }

    /**
     * Processes response from AsyncTask.
     *
     * @param works ArrayList
     */
    private void processResponse(List<Work> works) {
        // Hide spinner
        findViewById(R.id.progressBarSearchBooks).setVisibility(View.GONE);

        // Add adapter to ListView
        ListAdapter customAdapter = new BookSearchAdapter(works, this);
        listView.setAdapter(customAdapter);

        // Add listener
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            // Obs! Header is at position 0 and footer at last position.
            int index = position - 1;
            if (index > -1 && index < works.size()) {
                Intent intent = new Intent(this, BookActivity.class);
                intent.putExtra("work", works.get(index));
                startActivity(intent);

            }
        });
    }

    /**
     * Contacts server for search with AsyncTask.
     */
    private class Task extends AsyncTask<String, Integer, List<Work>> {

        private List<Work> works;
        private String query;

        /**
         * Contacts server for search.
         *
         * @param args String[]
         * @return List<Work>
         */
        @Override
        protected List<Work> doInBackground(String... args) {
            query = args[0];
            works = new ArrayList<>();

            try {
                // Create uri with key and query parameters
                Uri uri = Uri.parse("http://www.goodreads.com/search/index")
                        .buildUpon()
                        .appendQueryParameter("key", BuildConfig.GOOD_READS_API_KEY)
                        .appendQueryParameter("q", query)
                        .build();

                // Open connection
                URL url = new URL(uri.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // If response ok
                if (urlConnection.getResponseCode() == 200) {

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    Document doc = InputStreamParser.streamToXmlDoc(in);

                    // If document parsed and has tag total-results
                    if (doc != null && doc.getElementsByTagName("total-results") != null) {
                        parseResults(query, doc);

                    } else {
                        displaySnackBar(getResources().getString(R.string.results_loading_error));
                    }

                } else {
                    displaySnackBar(getResources().getString(R.string.http_books_search_error));
                }

                urlConnection.disconnect();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Return empty arrayList
            return works;
        }

        /**
         * Sends response to activity.
         *
         * @param works List<Work>
         */
        @Override
        protected void onPostExecute(List<Work> works) {
            super.onPostExecute(works);
            processResponse(works);
        }

        private void parseResults(String query, Document doc) {
            // Find how many results
            NodeList nodeList = doc.getElementsByTagName("total-results");
            int totalResults = Integer.parseInt(nodeList.item(0).getTextContent());

            // Display amount of results for query
            displayTotalResultsMessage(totalResults, query);

            if (totalResults > 0) {
                works = BookSearchResultsParser.docToWorks(doc);
                if (works.isEmpty()) {
                    displaySnackBar(getResources().getString(R.string.results_loading_error));
                }

            }
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

        /**
         * Sets message showing results.
         *
         * @param totalResults integer
         * @param query        String
         */
        private void displayTotalResultsMessage(int totalResults, String query) {
            runOnUiThread(() -> {
                TextView textView = (TextView) findViewById(R.id.textViewSearchMessage);
                TextView goodReads = (TextView) findViewById(R.id.textViewGoodReadsData);
                String message = "";

                if (totalResults > 0) {

                    if (totalResults < 20) {
                        message = "Found " + totalResults
                                + " results for search \"" + query + "\".";
                    } else {
                        message = "Found " + totalResults
                                + " results for search \"" + query + "\"."
                                + " Showing 20 best results.";
                    }

                    // Add GoodReads attribution and link to data source
                    String url = createGoodReadsAttribution();
                    String attribution = "Data from <a href='" + url + "'>GoodReads</a>";
                    goodReads.setClickable(true);
                    goodReads.setMovementMethod(LinkMovementMethod.getInstance());
                    goodReads.setText(Html.fromHtml(attribution));

                } else {
                    message = "No results found for search \"" + query + "\".";
                }

                // Set message
                textView.setText(message);
            });
        }

        /**
         * Creates link to search results on GoodReads.
         *
         * @return String search results url
         */
        private String createGoodReadsAttribution() {
            Uri uri = Uri.parse("http://www.goodreads.com/search")
                    .buildUpon()
                    .appendQueryParameter("q", query)
                    .build();
            return uri.toString();
        }
    }

}
