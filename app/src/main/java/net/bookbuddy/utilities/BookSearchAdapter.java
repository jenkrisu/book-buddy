package net.bookbuddy.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.bookbuddy.R;

import java.util.List;

/**
 * Created by Jenni on 19.4.2017.
 */

/**
 * Populates custom list item.
 */
public class BookSearchAdapter extends ArrayAdapter<Work> {

    public BookSearchAdapter(List<Work> works, Context context) {
        super(context, R.layout.list_item_work, works);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.list_item_work, null);
        }

        Work work = getItem(position);

        if (work != null) {
            TextView title = (TextView) view.findViewById(R.id.listItemBookTitle);
            TextView author = (TextView) view.findViewById(R.id.listItemBookAuthor);
            TextView published = (TextView) view.findViewById(R.id.listItemBookPublished);

            String year = work.getOriginalPublicationYer();
            if (year.equals("")) {
                year = "-";
            }

            title.setText(work.getBestBook().getTitle());
            author.setText("By " + work.getBestBook().getAuthorName());
            published.setText("Published " + year);
        }

        return view;
    }

}