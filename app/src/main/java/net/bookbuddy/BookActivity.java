package net.bookbuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.bookbuddy.R;
import net.bookbuddy.utilities.Work;

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

        // Get intent
        Intent intent = getIntent();
        if (intent.hasExtra("work")) {
            this.work = (Work) intent.getSerializableExtra("work");
            setTitle(work.getBestBook().getTitle());
        }

        // Add navigation drawer
        super.onCreateDrawer();
    }
}
