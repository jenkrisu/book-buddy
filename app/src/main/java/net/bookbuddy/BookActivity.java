package net.bookbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.bookbuddy.utilities.Work;


public class BookActivity extends BaseActivity {

    /**
     * Work to display.
     */
    private Work work;

    /**
     * ProgressBar to display while loading content.
     */
    private ProgressBar progressBar;

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
        }

        // TODO: If no work extra

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

        // Add progress bar while loading content
        progressBar = (ProgressBar) findViewById(R.id.progressBarSelectedBook);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Loads book image to image view.
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
}
