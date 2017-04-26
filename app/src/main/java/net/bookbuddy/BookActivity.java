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
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.bookbuddy.utilities.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.List;


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
        // Hide spinner
        findViewById(R.id.progressBarSelectedBook).setVisibility(View.GONE);
        findViewById(R.id.selectedBookData).setVisibility(View.VISIBLE);
        System.out.println(book.getUrl());
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
