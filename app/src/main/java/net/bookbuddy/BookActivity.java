package net.bookbuddy;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
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
import java.net.MalformedURLException;
import java.net.URL;


public class BookActivity extends BaseActivity implements DownloadCallback {

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
            addSpinner();
            fetchBook();
        }
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

    private void addSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_shelves);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.shelves_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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
     * @param title title
     * @param content content
     * @param view view
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

}
