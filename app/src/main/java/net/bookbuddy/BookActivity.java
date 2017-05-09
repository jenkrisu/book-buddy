package net.bookbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.squareup.picasso.Picasso;

import net.bookbuddy.data.Author;
import net.bookbuddy.data.Book;
import net.bookbuddy.data.Shelf;
import net.bookbuddy.data.Work;
import net.bookbuddy.utilities.*;

import android.widget.AdapterView.OnItemSelectedListener;

import org.joda.time.LocalDate;
import org.w3c.dom.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class BookActivity extends BaseActivity implements DownloadCallback, OnItemSelectedListener {

    /**
     * Work to display.
     */
    private Work work;

    /**
     * Access token for OAuth.
     */
    private OAuth1AccessToken accessToken;

    /**
     * User id for get requests.
     */
    private String userId;

    /**
     * Book shelf spinner.
     */
    private Spinner spinner;

    /**
     * Prevents firing onItemSelectedListener after selection set programmatically on view create.
     */
    private int initCheck;

    /**
     * Creates activity.
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        // Add navigation drawer
        super.onCreateDrawer();

        // Get intent
        Intent intent = getIntent();
        if (intent.hasExtra("work")) {
            work = (Work) intent.getSerializableExtra("work");
            addInitialData();
            addSpinner();
            fetchShelves();
            fetchBook();
        }
    }

    /**
     * Makes sure that shelf is "Add to Self" if not logged in.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (!isLoggedIn()) {
            spinner.setSelection(0);
        }
    }

    /**
     * Fetches shelves if user is logged in.
     */
    private void fetchShelves() {
        if (isLoggedIn()) {
            if (containsIdAndToken()) {
                BookShelvesTask task = new BookShelvesTask();
                task.execute();
            }
        }
    }

    /**
     * Handles book shelf spinner item selections.
     *
     * @param parent AdapterView
     * @param view   View
     * @param pos    Integer position
     * @param id     long Id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if (initCheck > 0) {
            if (pos != 0) {
                if (isLoggedIn()) {
                    findViewById(R.id.spinner_shelves).setVisibility(View.GONE);
                    findViewById(R.id.progressBar_spinnerLoad).setVisibility(View.VISIBLE);

                    // Self selected, add book to that self
                    AddOrRemoveTask addOrRemoveTask = new AddOrRemoveTask();
                    addOrRemoveTask.execute(getShelfName(pos), work.getBestBook().getId(), "");
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }
            }
        }
        initCheck++;

    }

    /**
     * Finds name of shelf.
     *
     * @param pos position of spinner item
     * @return String name
     */

    private String getShelfName(int pos) {
        switch (pos) {
            case 1:
                return "to-read";
            case 2:
                return "currently-reading";
            case 3:
                return "read";
            default:
                return "";
        }
    }

    /**
     * Handles nothing selected from spinner.
     *
     * @param parent AdapterView
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
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
     * Fetches book data.
     */
    private void fetchBook() {
        DownloadXmlTask downloadXmlTask = new DownloadXmlTask();
        downloadXmlTask.callback = this;

        Uri uri = Uri.parse("http://www.goodreads.com/book/show")
                .buildUpon()
                .appendQueryParameter("key", BuildConfig.GOOD_READS_API_KEY)
                .appendQueryParameter("id", work.getBestBook().getId())
                .appendQueryParameter("text_only", "true")
                .build();

        try {
            downloadXmlTask.execute(new URL(uri.toString()));
        } catch (MalformedURLException ex) {
            displaySnackbar();
            ex.printStackTrace();
        }
    }

    /**
     * Handles result of fetching the best book data.
     *
     * @param result DownloadTask.Result with document and status
     */
    @Override
    public void processFinish(DownloadXmlTask.Result result) {
        Book book;

        // If document parsed and has tag book
        if (result.document != null && result.document.getElementsByTagName("book") != null) {
            book = BookResultParser.docToBook(result.document);

            if (book != null) {
                renderBookData(book);
            } else {
                displaySnackbar();
            }
        } else {
            displaySnackbar();
        }
    }

    /**
     * Adds spinner while loading.
     */
    private void addSpinner() {
        String[] array = new String[]{"Add to Shelf", "Want to Read", "Currently Reading", "Read"};

        spinner = (Spinner) findViewById(R.id.spinner_shelves);

        ArrayAdapter adapter =
                new ArrayAdapter<String>(this, R.layout.spinner_item, array) {
                    // Show Add to Shelf selection only if book is on no shelf
                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View v = null;

                        // If this is the initial dummy entry, make it hidden
                        if (position == 0) {
                            TextView tv = new TextView(getContext());
                            tv.setHeight(0);
                            tv.setVisibility(View.GONE);
                            v = tv;
                        } else {
                            // Pass convertView as null to prevent reuse of special case views
                            v = super.getDropDownView(position, null, parent);
                        }

                        return v;
                    }
                };

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }

    /**
     * Add title, image and progressbar.
     */
    private void addInitialData() {
        // Load book image
        loadBookImage();

        // Set title
        ((TextView) findViewById(R.id.textViewBookTitle))
                .setText(work.getBestBook().getTitle());
    }

    /**
     * Loads bestBook image to image view.
     */
    private void loadBookImage() {
        ImageView imageView = (ImageView) findViewById(R.id.imageViewBook);
        String placeholder = "111x148-bcc042a9c91a29c1d680899eff700a03.png";
        String url = work.getBestBook().getImageUrl();

        if (url != null && url.length() > 0 && !url.contains(placeholder)) {
            // Picasso Library fetches asynchronously and caches images
            Picasso.with(this).load(url).into(imageView);
        } else {
            // If image url is Goodreads placeholder, add own placeholder
            imageView.setImageResource(R.drawable.ic_book_placeholder);
        }
    }

    /**
     * Renders book data to view.
     *
     * @param book Book
     */
    private void renderBookData(Book book) {
        // Add information depending on availability from Goodreads
        addAuthorsText(book);

        addText("Published ",
                work.getOriginalPublicationYear(),
                findViewById(R.id.textViewBookOrigPublication));

        addDescriptionText(book);

        if (containsEditionDetails(book)) {
            findViewById(R.id.textViewBookEditionDetails).setVisibility(View.VISIBLE);
            addPublicationText(book);
            addText("Publisher: ", book.getPublisher(), findViewById(R.id.textViewBookPublisher));
            addText("Format: ", book.getFormat(), findViewById(R.id.textViewBookFormat));
            addText("Pages: ", book.getPages(), findViewById(R.id.textViewBookPages));
            addText("ISBN10: ", book.getIsbnTen(), findViewById(R.id.textViewBookIsbnTen));
            addText("ISBN13: ", book.getIsbnThirteen(), findViewById(R.id.textViewBookIsbnThirteen));
        }

        addRating();
        addGoodreadsAttribution(book);

        // Hide spinner
        findViewById(R.id.progressBarSelectedBook).setVisibility(View.GONE);
        findViewById(R.id.selectedBookData).setVisibility(View.VISIBLE);
    }

    /**
     * Checks if book contains edition details.
     *
     * @param book Book
     * @return boolean
     */
    private boolean containsEditionDetails(Book book) {
        return book.getPublication() != null
                || book.getPublisher().length() > 0
                || book.getFormat().length() > 0
                || book.getPages().length() > 0
                || book.getIsbnTen().length() > 0
                || book.getIsbnThirteen().length() > 0;
    }

    /**
     * Sets authors.
     *
     * @param book Book
     */
    private void addAuthorsText(Book book) {
        TextView authors = (TextView) findViewById(R.id.textViewBookAuthors);
        int size = book.getAuthors().size();

        if (size == 1) {
            String text = "By " + book.getAuthors().get(0).getName();
            authors.setText(text);

        } else if (size > 1) {
            String text = "By ";
            for (int i = 0; i < size; i++) {
                Author author = book.getAuthors().get(i);
                text += author.getName();
                if (author.getRole().length() > 0) {
                    text += " (";
                    text += author.getRole();
                    text += ")";
                }
                if (i < size - 1) {
                    text += ", ";
                }
            }
            authors.setText(text);
        }
    }


    /**
     * Add publication date of this edition,
     *
     * @param book Book
     */
    private void addPublicationText(Book book) {
        LocalDate publication = book.getPublication();

        if (publication != null) {
            TextView textView = (TextView) findViewById(R.id.textViewBookPublication);
            textView.setVisibility(View.VISIBLE);
            int day = publication.getDayOfMonth();
            String suffix = getLastDigitSuffix(day);
            String text = "Published: "
                    + publication.toString("MMMM d'" + suffix + "' YYYY");
            textView.setText(text);
        }
    }

    /**
     * Sets description.
     *
     * @param book Book
     */
    private void addDescriptionText(Book book) {
        String descriptionHtml = book.getDescription();

        if (descriptionHtml.length() > 0) {
            TextView description = (TextView) findViewById(R.id.textViewBookDescription);
            description.setVisibility(View.VISIBLE);
            description.setText(Html.fromHtml(descriptionHtml));
        }
    }

    /**
     * Adds text to TextView if text is available and makes TextView visible.
     *
     * @param title   title
     * @param content content
     * @param view    view
     */
    private void addText(String title, String content, View view) {
        TextView textView = (TextView) view;
        if (content.length() > 0) {
            String text = title + content;
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        }
    }

    /**
     * Adds rating.
     */
    private void addRating() {
        TextView textView = (TextView) findViewById(R.id.textViewBookRatingAmount);
        String text;

        if (work.getRatingsCount().length() > 0) {
            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBarGoodreadsRating);
            ratingBar.setRating(work.getAverageRating());
            text = "Average " + work.getAverageRating() + " (" + work.getRatingsCount() + " ratings)";
        } else {
            text = "Average 0.0 (0 ratings)";
        }

        textView.setText(text);
    }

    /**
     * Creates link to search results on GoodReads.
     *
     * @param book Book
     */
    private void addGoodreadsAttribution(Book book) {
        TextView goodreads = (TextView) findViewById(R.id.textViewGoodreadsBookLink);
        String attribution = "Data from <a href='" + book.getUrl() + "'>Goodreads</a>";
        goodreads.setClickable(true);
        goodreads.setMovementMethod(LinkMovementMethod.getInstance());
        goodreads.setText(Html.fromHtml(attribution));
    }

    /**
     * Determines correct suffix for day digit.
     *
     * @param number day
     * @return String suffix
     */
    private String getLastDigitSuffix(int number) {
        switch ((number < 20) ? number : number % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    /**
     * Displays load error in snackbar.
     */
    private void displaySnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                getResources().getText(R.string.snackbar_load_error), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    /**
     * Handles BookShelvesTask result.
     *
     * @param list shelves of book
     */
    private void processBookShelvesTask(List<Shelf> list) {
        // Set spinner selection to Want to Read, Currently Reading or Read
        // if book is found on one of those exclusive shelves
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equals("to-read")) {
                spinner.setSelection(1);
                break;
            } else if (list.get(i).getName().equals("currently-reading")) {
                spinner.setSelection(2);
                break;
            } else if (list.get(i).getName().equals("read")) {
                spinner.setSelection(3);
                break;
            }
        }
    }

    /**
     * Finds possible book shelves that the selected book is on.
     */
    private class BookShelvesTask extends AsyncTask<Void, Integer, List<Shelf>> {

        /**
         * Gets user id and possibly user name (optional) from GoodReads.
         *
         * @param args array (empty)
         * @return List<Shelf> shelves
         */
        @Override
        protected List<Shelf> doInBackground(Void... args) {
            List<Shelf> list = new ArrayList<Shelf>();

            OAuth10aService service = new ServiceBuilder()
                    .apiKey(BuildConfig.GOOD_READS_API_KEY)
                    .apiSecret(BuildConfig.GOOD_READS_API_SECRET)
                    .build(GoodreadsApi.instance());

            try {
                Uri uri = Uri.parse("https://www.goodreads.com/review/list/" + userId + ".xml")
                        .buildUpon()
                        .appendQueryParameter("v", "2")
                        .appendQueryParameter("id", userId)
                        .appendQueryParameter("search[query]", work.getBestBook().getTitle())
                        .appendQueryParameter("key", BuildConfig.GOOD_READS_API_KEY)
                        .build();

                OAuthRequest request = new OAuthRequest(Verb.GET,
                        uri.toString(),
                        service);

                service.signRequest(accessToken, request);

                Response response = request.send();

                Document doc = null;

                if (response.getBody().contains("GoodreadsResponse")) {
                    doc = InputStreamParser.stringToDoc(response.getBody());
                }

                if (doc != null) {
                    list = BookShelfParser.docToShelvesOfBook(doc);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return list;
        }

        /**
         * Processes result.
         *
         * @param list List<Shelf>
         */
        @Override
        protected void onPostExecute(List<Shelf> list) {
            super.onPostExecute(list);
            processBookShelvesTask(list);
        }
    }

    /**
     * Handles shelf update.
     *
     * @param status Integer status
     */
    private void processShelfUpdate(int status) {
        findViewById(R.id.progressBar_spinnerLoad).setVisibility(View.GONE);
        findViewById(R.id.spinner_shelves).setVisibility(View.VISIBLE);
        Snackbar snackbar;

        switch (status) {
            case 200:
                snackbar = Snackbar.make(findViewById(android.R.id.content),
                        getResources().getText(R.string.snackbar_removed), Snackbar.LENGTH_LONG);
                snackbar.show();
                break;
            case 201:
                snackbar = Snackbar.make(findViewById(android.R.id.content),
                        getResources().getText(R.string.snackbar_added), Snackbar.LENGTH_LONG);
                snackbar.show();
                break;
            default:
                snackbar = Snackbar.make(findViewById(android.R.id.content),
                        getResources().getText(R.string.snackbar_error), Snackbar.LENGTH_LONG);
                snackbar.show();
                break;
        }
    }

    /**
     * Adds or removes book from shelf.
     */
    private class AddOrRemoveTask extends AsyncTask<String, Integer, Integer> {

        /**
         * Adds book to shelf.
         * <p>
         * In case of exclusive shelves (to-read, currently-reading, read) book can only be
         * transferred from shelf to another with the Goodreads API, not removed completely
         * after it has been initially added. If book on exclusive shelf is "removed",
         * it is actually moved to shelf read.
         * <p>
         * In case of non-exlusive shelves the book can be removed from shelf.
         *
         * @param args name of shelf, id of book, add ("") or remove ("remove")
         * @return Integer status
         */
        @Override
        protected Integer doInBackground(String... args) {
            String name = args[0];
            String bookId = args[1];
            String addOrRemove = args[2];
            String url = "https://www.goodreads.com/shelf/add_to_shelf.xml";
            int status = 0;

            OAuth10aService service = new ServiceBuilder()
                    .apiKey(BuildConfig.GOOD_READS_API_KEY)
                    .apiSecret(BuildConfig.GOOD_READS_API_SECRET)
                    .build(GoodreadsApi.instance());

            try {
                OAuthRequest request = new OAuthRequest(Verb.POST,
                        url,
                        service);

                request.addBodyParameter("name", name);
                request.addBodyParameter("book_id", bookId);
                request.addBodyParameter("a", addOrRemove);

                service.signRequest(accessToken, request);

                Response response = request.send();

                status = response.getCode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return status;
        }

        /**
         * Handles shelf update.
         *
         * @param status
         */
        @Override
        protected void onPostExecute(Integer status) {
            super.onPostExecute(status);
            processShelfUpdate(status);
        }
    }
}