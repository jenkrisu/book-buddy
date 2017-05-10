package net.bookbuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import net.bookbuddy.adapters.ShelfItemAdapter;
import net.bookbuddy.data.Review;
import net.bookbuddy.data.Shelf;
import net.bookbuddy.utilities.Global;
import net.bookbuddy.utilities.GoodreadsApi;
import net.bookbuddy.utilities.InputStreamParser;
import net.bookbuddy.utilities.ReviewResultParser;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays items on shelf.
 */
public class ShelfActivity extends BaseActivity {

    /**
     * Expandable lis view for showing books.
     */
    private ExpandableListView expandableListView;

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
     * Self name.
     */
    private String name;

    /**
     * Amount of books on shelf.
     */
    private String amount;

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

            addExpandableListView();

            Shelf shelf = (Shelf) intent.getSerializableExtra("shelf");
            String name = shelf.getName();
            if (name != null) {
                this.name = name;
                // Set title to activity
                setTitle(getTitle(name));

                // Find books
                getBooks(name);
            }
        }
    }

    /**
     * Adds ExpandableListView with header and footer.
     */
    private void addExpandableListView() {
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView_shelf);

        // Add header
        View header = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.expandable_list_item_book_header, null, false);

        expandableListView.addHeaderView(header, "Header", false);

        // Add footer
        View footer = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.expandable_list_item_book_footer, null, false);

        expandableListView.addFooterView(footer, "Footer", false);

        // Add footer text
        TextView goodreads = (TextView) findViewById(R.id.textViewGoodreadsDataShelf);
        Uri uri = Uri.parse("https://www.goodreads.com/review/list/" + userId)
                .buildUpon()
                .appendQueryParameter("shelf", this.name)
                .build();
        String attribution = "Shelf on <a href='" + uri.toString() + "'>Goodreads</a>";
        goodreads.setClickable(true);
        goodreads.setMovementMethod(LinkMovementMethod.getInstance());
        goodreads.setText(Html.fromHtml(attribution));
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

    /**
     * Fetches books if user is logged in.
     *
     * @param name String shelf name
     */
    private void getBooks(String name) {
        if (isLoggedIn() && containsIdAndToken()) {
            ReviewsTask reviewsTask = new ReviewsTask();
            reviewsTask.execute(name);
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

    private void processBooksTask(List<Review> reviews) {
        expandableListView.setAdapter(new ShelfItemAdapter(this, reviews));
        if (!this.amount.isEmpty()) {
            setTitle(this.name + " (" + this.amount + ")");
        }

        // Remove progress bar
        findViewById(R.id.progressBar_shelf).setVisibility(View.GONE);

        // Updates header text
        TextView textView = (TextView) findViewById(R.id.textView_shelfBookAmount);
        textView.setText("Showing " + reviews.size() + " of " + this.amount + " books.");

        // Sets edit text and lis view visible
        EditText editText = (EditText) findViewById(R.id.editText_shelfFilter);
        editText.setVisibility(View.VISIBLE);
        expandableListView.setVisibility(View.VISIBLE);

        // Sets listener to edit text
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (isLoggedIn() && containsIdAndToken()) {
                        ReviewsTask reviewsTask = new ReviewsTask();
                        reviewsTask.execute(name, v.getText().toString());
                        editText.setVisibility(View.INVISIBLE);
                        expandableListView.setVisibility(View.GONE);
                    }
                    handled = true;
                }
                return handled;
            }
        });
    }

    /**
     * Finds possible books on selected shelf.
     */
    private class ReviewsTask extends AsyncTask<String, Integer, List<Review>> {

        /**
         * Finds books on shelf.
         *
         * @param args array with book shelf name and query (query optional)
         * @return List<Review> reviews
         */
        @Override
        protected List<Review> doInBackground(String... args) {
            String name = args[0];
            String query = "";
            if (args.length > 1) {
                query = args[1];
            }

            List<Review> list = new ArrayList<Review>();

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

                if (query.length() > 0) {
                    uri = uri.buildUpon().appendQueryParameter("search[query]", query).build();
                }

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
                    if (amount == null) {
                        NodeList reviewsNodeList = doc.getElementsByTagName("reviews");
                        NamedNodeMap map = reviewsNodeList.item(0).getAttributes();
                        Node reviewsTotalAttributeNode = map.getNamedItem("total");
                        amount = reviewsTotalAttributeNode.getNodeValue();
                    }

                    list = ReviewResultParser.docToReviews(doc);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return list;
        }

        /**
         * Processes result.
         *
         * @param list List<Review>
         */
        @Override
        protected void onPostExecute(List<Review> list) {
            super.onPostExecute(list);
            processBooksTask(list);
        }
    }


}
