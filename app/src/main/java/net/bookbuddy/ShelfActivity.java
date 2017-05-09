package net.bookbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import net.bookbuddy.data.Book;
import net.bookbuddy.data.Shelf;
import net.bookbuddy.utilities.Global;
import net.bookbuddy.utilities.GoodreadsApi;
import net.bookbuddy.utilities.InputStreamParser;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays items on shelf.
 */
public class ShelfActivity extends BaseActivity {

    /**
     * Saves userId.
     */
    private String userId;

    /**
     * Saves access token.
     */
    private OAuth1AccessToken accessToken;

    /**
     * Sorting of books.
     */
    private String sort = "date_added";

    /**
     * Order of books, a for ascending or d for descending.
     */
    private String order = "d";

    /**
     * Creates activity.
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf);

        // Add navigation drawer
        super.onCreateDrawer();
        findViewById(R.id.progressBar_shelf).setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        if (intent.hasExtra("shelf")) {
            Shelf shelf = (Shelf) intent.getSerializableExtra("shelf");
            String name = shelf.getName();
            if (name != null) {
                // Set title to activity
                setTitle(getTitle(name));

                // Find books
                getBooks(name);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sort = "date_added";
        order = "d";
    }

    /**
     * Sets title.
     *
     * @param name String
     * @return String title
     */
    private String getTitle(String name) {
        switch (name) {
            case "to-read":
                return "To Read";
            case "currently-reading":
                return "Currently Reading";
            case "read":
                return "Read";
            default:
                return name;
        }
    }

    private void getBooks(String name) {

        if (isLoggedIn() && containsIdAndToken()) {

            BooksTask booksTask = new BooksTask();
            booksTask.execute(name, "date_added", "d");

        }

    }

    /**
     * Checks if user has logged in.
     *
     * @return boolean logged in or not
     */
    private boolean isLoggedIn() {
        SharedPreferences preferences =
                getApplicationContext().getSharedPreferences(Global.MY_PREFS_NAME, MODE_PRIVATE);

        return preferences.contains("loggedIn") && preferences.getBoolean("loggedIn", false);
    }

    /**
     * Checks if user id, access token and access token secret in shared preferences.
     *
     * @return boolean found or not
     */

    private boolean containsIdAndToken() {
        SharedPreferences preferences =
                getApplicationContext().getSharedPreferences(Global.MY_PREFS_NAME, MODE_PRIVATE);

        if (preferences.contains("userId")) {
            this.userId = preferences.getString("userId", "");
        }

        if (preferences.contains("accessToken") && preferences.contains("accessTokenSecret")) {
            accessToken = new OAuth1AccessToken(preferences.getString("accessToken", ""),
                    preferences.getString("accessTokenSecret", ""));
        }

        return (this.userId != null && this.accessToken != null);
    }

    private void processBooksTask(List<Book> books) {

    }

    /**
     * Finds possible books on selected shelf.
     */
    private class BooksTask extends AsyncTask<String, Integer, List<Book>> {

        /**
         * Gets user id and possibly user name (optional) from GoodReads.
         *
         * @param args array with book shelf name
         * @return List<Book> books
         */
        @Override
        protected List<Book> doInBackground(String... args) {
            String name = args[0];

            List<Book> list = new ArrayList<Book>();

            OAuth10aService service = new ServiceBuilder()
                    .apiKey(BuildConfig.GOOD_READS_API_KEY)
                    .apiSecret(BuildConfig.GOOD_READS_API_SECRET)
                    .build(GoodreadsApi.instance());

            try {
                Uri uri = Uri.parse("https://www.goodreads.com/review/list/" + userId + ".xml")
                        .buildUpon()
                        .appendQueryParameter("v", "2")
                        .appendQueryParameter("shelf", name)
                        .appendQueryParameter("sort", sort)
                        .appendQueryParameter("order", order)
                        .appendQueryParameter("page", "1")
                        .appendQueryParameter("per_page", "50")
                        .appendQueryParameter("id", userId)
                        .appendQueryParameter("key", BuildConfig.GOOD_READS_API_KEY)
                        .build();

                OAuthRequest request = new OAuthRequest(Verb.GET,
                        uri.toString(),
                        service);

                service.signRequest(accessToken, request);

                Response response = request.send();

                System.out.println(response.getBody());

                Document doc = null;

                if (response.getBody().contains("GoodreadsResponse")) {
                    doc = InputStreamParser.stringToDoc(response.getBody());
                }

                if (doc != null) {

                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return list;
        }

        /**
         * Processes result.
         *
         * @param list List<Book>
         */
        @Override
        protected void onPostExecute(List<Book> list) {
            super.onPostExecute(list);
            processBooksTask(list);
        }
    }


}