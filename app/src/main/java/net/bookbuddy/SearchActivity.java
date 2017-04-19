package net.bookbuddy;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import net.bookbuddy.utilities.Book;
import net.bookbuddy.utilities.InputStreamParser;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        super.onCreateDrawer();
        handleIntent(getIntent());
    }

    /**
     * Creates options menu with SearchView.
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
            String query = intent.getStringExtra(SearchManager.QUERY);
            //New AsyncTask for searching books
            Task task = new Task();
            task.execute(query);
        }
    }

    /**
     * Processes response from AsyncTask.
     * @param books ArrayList
     */
    private void processResponse(List<Book> books) {
        if (!books.isEmpty()) {
            // Create ListView
        }
    }

    /**
     * Contacts server for search with AsyncTask.
     */
    private class Task extends AsyncTask<String, Integer, List<Book>> {

        private ArrayList<Book> books;

        /**
         * Contacts server for search.
         * @param args query parameter
         * @return ArrayList of books
         */
        @Override
        protected List<Book> doInBackground(String... args) {

            String query = args[0];
            books = new ArrayList<>();

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
            return books;
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            super.onPostExecute(books);
            processResponse(books);
        }

        private void parseResults(String query, Document doc) {
            // Find how many results
            NodeList nodeList = doc.getElementsByTagName("total-results");
            int totalResults = Integer.parseInt(nodeList.item(0).getTextContent());

            // Display amount of results for query
            displayTotalResultsMessage(totalResults, query);

            if (totalResults > 0) {
                // books = DocParser.docToBooks(doc);
                // if (books.isEmpty)
                // displaySnackbar("Unfortunately an error occurred loading results");
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

                if (totalResults == 0) {
                    message = "No results found for search \"" + query + "\".";
                } else if (totalResults < 20) {
                    message = "Showing " + totalResults + " results for search \"" + query + "\".";
                } else {
                    message = "Showing 19 results of " + totalResults
                            + " results for search \"" + query + "\".";
                }

                textView.setText(message);
                goodReads.setText(R.string.goodreads_text);

            });
        }
    }

}
