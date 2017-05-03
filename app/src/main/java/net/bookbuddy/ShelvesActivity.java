package net.bookbuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ShelvesActivity extends BaseActivity {

    /**
     * Creates activity
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelves);

        // Add navigation drawer
        super.onCreateDrawer();


    }
}
