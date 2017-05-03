package net.bookbuddy;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.bookbuddy.data.Author;
import net.bookbuddy.data.Book;
import net.bookbuddy.data.Work;
import net.bookbuddy.utilities.*;

import org.joda.time.LocalDate;
import org.w3c.dom.Document;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class BookActivity extends BaseActivity {

    /**
     * Work to display.
     */
    private Work work;

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
            Task task = new Task();
            task.execute(work.getBestBook().getId());
        }
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
            // If image url is GoodReads placeholder, add own placeholder
            imageView.setImageResource(R.drawable.ic_book_placeholder);
        }
    }

    /**
     * Processes response from AsyncTask.
     *
     * @param book Book
     */
    private void processResponse(Book book) {
        // Add information depending on availability from GoodReads
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
        addGoodReadsAttribution(book);

        // Hide spinner
        findViewById(R.id.progressBarSelectedBook).setVisibility(View.GONE);
        findViewById(R.id.selectedBookData).setVisibility(View.VISIBLE);
    }

    /**
     * Checks if book contains edition details.
     *
     * @param book
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
     * @param book
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
     * @param book
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
     * @param book
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
     * @param title
     * @param content
     * @param view
     */
    private void addText(String title, String content, View view) {
        TextView textView = (TextView) view;
        if (content.length() > 0) {
            String text = title + content;
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        }
    }

    private void addRating() {
        TextView textView = (TextView) findViewById(R.id.textViewBookRatingAmount);
        String text = "";

        if (work.getRatingsCount().length() > 0) {
            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBarGoodReadsRating);
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
     * @param book
     */
    private void addGoodReadsAttribution(Book book) {
        TextView goodReads = (TextView) findViewById(R.id.textViewGoodReadsBookLink);
        String attribution = "Data from <a href='" + book.getUrl() + "'>GoodReads</a>";
        goodReads.setClickable(true);
        goodReads.setMovementMethod(LinkMovementMethod.getInstance());
        goodReads.setText(Html.fromHtml(attribution));
    }

    /**
     * Determines correct suffix for day digit.
     *
     * @param number day
     * @return String
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
     * Contacts server for search with AsyncTask.
     */
    private class Task extends AsyncTask<String, Integer, Book> {

        /**
         * Contacts server for search.
         *
         * @param args
         * @return Book book
         */
        @Override
        protected Book doInBackground(String... args) {
            String bookId = args[0];
            Book book = new Book();

            try {
                // Create uri with key and query parameters
                Uri uri = Uri.parse("http://www.goodreads.com/book/show")
                        .buildUpon()
                        .appendQueryParameter("key", BuildConfig.GOOD_READS_API_KEY)
                        .appendQueryParameter("id", bookId)
                        .appendQueryParameter("text_only", "true")
                        .build();

                // Open connection
                URL url = new URL(uri.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // If response ok
                if (urlConnection.getResponseCode() == 200) {

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    Document doc = InputStreamParser.streamToXmlDoc(in);

                    // If document parsed and has tag book
                    if (doc != null && doc.getElementsByTagName("book") != null) {
                        book = BookResultParser.docToBook(doc);
                    } else {
                        displaySnackBar(getResources().getString(R.string.results_loading_error));
                    }

                } else {
                    displaySnackBar(getResources().getString(R.string.http_books_search_error));
                }

                urlConnection.disconnect();

            } catch (Exception ex) {
                ex.printStackTrace();


            }

            // Return empty book
            return book;
        }

        /**
         * Sends response to activity.
         *
         * @param book Book
         */
        @Override
        protected void onPostExecute(Book book) {
            super.onPostExecute(book);
            processResponse(book);
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
    }

}
