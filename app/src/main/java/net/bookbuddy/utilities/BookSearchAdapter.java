package net.bookbuddy.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.bookbuddy.R;

import java.util.List;

/**
 * Created by Jenni on 19.4.2017.
 */

/**
 * Populates custom list item.
 */
public class BookSearchAdapter extends ArrayAdapter<Work> {

    /**
     * Context of adapter.
     */
    private Context context;

    /**
     * Creates adapter.
     *
     * @param works   ArrayList
     * @param context context
     */
    public BookSearchAdapter(List<Work> works, Context context) {
        super(context, R.layout.list_item_work, works);
        this.context = context;
    }

    /**
     * Updates convertView with Work object information.
     *
     * @param position    integer
     * @param convertView View
     * @param parent      ViewGroup
     * @return View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater vi =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_item_work, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            // Prevent from showing old image when recycling convertView
            holder = (ViewHolder) convertView.getTag();
            holder.image.setTag(null);
            holder.image.setImageBitmap(null);
        }

        Work work = getItem(position);

        if (work != null) {
            // Show publication information only if publication year is found
            String year = work.getOriginalPublicationYear();
            if (year.equals("")) {
                holder.published.setText("");
            } else {
                holder.published.setText("Published " + year);
            }


            holder.title.setText(work.getBestBook().getTitle());
            holder.author.setText("By " + work.getBestBook().getAuthorName());
        }

        // Set book image or a placeholder, if image not available
        String placeholder = "50x75-a91bf249278a81aabab721ef782c4a74.png";
        String url = work.getBestBook().getSmallImageUrl();

        // Set image
        if (url != null && url.length() > 0 && !url.contains(placeholder)) {
            // Picasso Library fetches asynchronously and caches images
            Picasso.with(context).load(url).into(holder.image);
        } else {
            // Set own placeholder
            holder.image.setImageResource(R.drawable.ic_book_placeholder);
        }

        return convertView;
    }

    /**
     * Holds view of ListView item.
     */
    private class ViewHolder {

        /**
         * Holds book title.
         */
        TextView title;

        /**
         * Holds book author name.
         */
        TextView author;

        /**
         * Holds book orig publication year.
         */
        TextView published;

        /**
         * Shows small book image.
         */
        ImageView image;

        /**
         * Creates ViewHolder.
         *
         * @param view View
         */
        public ViewHolder(View view) {
            this.title = (TextView) view.findViewById(R.id.listItemBookTitle);
            this.author = (TextView) view.findViewById(R.id.listItemBookAuthor);
            this.published = (TextView) view.findViewById(R.id.listItemBookPublished);
            this.image = (ImageView) view.findViewById(R.id.listItemImage);
        }
    }

}