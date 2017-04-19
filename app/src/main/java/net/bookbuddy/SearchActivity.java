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
import android.view.Menu;
import android.view.View;
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
     * Creates activity
     *
     * @param savedInstanceState bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Add navigation drawer
        super.onCreateDrawer();

        // Handle search
        handleIntent(getIntent());
    }

    /**
     * Creates options menu with SearchView.
     *
     * @param menu menu
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
     * @param intent intent
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

        if (!works.isEmpty()) {
            // Create ListView
            for (Work w : works) {
                System.out.println(w.getBestBook().getAuthorName() + ", " + w.getBestBook().getTitle());
            }
        }
    }

    /**
     * Contacts server for search with AsyncTask.
     */
    private class Task extends AsyncTask<String, Integer, List<Work>> {

        private List<Work> works;

        /**
         * Contacts server for search.
         *
         * @param args query parameter
         * @return ArrayList of works
         */
        @Override
        protected List<Work> doInBackground(String... args) {
            String query = args[0];
            works = new ArrayList<>();

            try {
                // Create uri with key and query parameters
                Uri uri = Uri.parse("http://www.goodreads.com/search/index.xml?")
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
                        displaySnackBar("Unfortunately an error occurred loading results");
                    }

                } else {
                    displaySnackBar("Unfortunately an error occurred searching books");
                }

                urlConnection.disconnect();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Return empty arrayList
            return works;
        }

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
                works = DocumentParser.docToWorks(doc);
                if (works.isEmpty()) {
                    displaySnackBar("Unfortunately an error occurred loading results");
                }

            }
        }

        private void displaySnackBar(String message) {
            runOnUiThread(() -> {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                        message, Snackbar.LENGTH_LONG);
                snackbar.show();
            });
        }

        private void displayTotalResultsMessage(int totalResults, String query) {
            runOnUiThread(() -> {
                TextView textView = (TextView) findViewById(R.id.textViewSearchMessage);
                TextView goodReads = (TextView) findViewById(R.id.textViewGoodReadsData);
                String message = "";

                if (totalResults > 0) {

                    if (totalResults < 20) {
                        message = "Showing " + totalResults
                                + " results for search \"" + query + "\".";
                    } else {
                        message = "Showing 20 of " + totalResults
                                + " results for search \"" + query + "\".";
                    }

                    // Add GoodReads attribution
                    goodReads.setText(R.string.goodreads_text);

                } else {
                    message = "No results found for search \"" + query + "\".";
                }

                // Set message
                textView.setText(message);
            });
        }
    }

}
