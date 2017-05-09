package net.bookbuddy.adapters;

import android.text.Html;
import android.widget.BaseExpandableListAdapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import net.bookbuddy.R;
import net.bookbuddy.data.Review;

/**
 * Created by Jenni on 9.5.2017.
 */

public class ShelfItemAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Review> reviews;

    public ShelfItemAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public int getGroupCount() {
        return reviews.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Review getGroup(int groupPosition) {
        return reviews.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return reviews.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        Review review = getGroup(groupPosition);

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.expandable_list_item_book, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (isExpanded) {
            holder.arrow.setImageResource(R.drawable.ic_expand_less_black_24px);
        } else {
            holder.arrow.setImageResource(R.drawable.ic_expand_more_black_24px);
        }

        holder.title.setText(review.getBook().getTitle());
        holder.author.setText("By " + review.getBook().getAuthors().get(0).getName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        Review review = getGroup(groupPosition);

        ChildViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.expandable_list_item_book_child, parent, false);
            holder = new ChildViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        // Add shelves text
        if (review.getShelves().size() > 0) {
            String text = "Shelves: ";
            for (int i = 0; i < review.getShelves().size(); i++) {
                text += review.getShelves().get(i).getName();
                if (i < review.getShelves().size() - 1) {
                    text += ", ";
                }
            }
            holder.shelves.setText(text);
            holder.shelves.setVisibility(View.VISIBLE);
        }

        // Add stars to rating bar
        try {
            float rating = Float.valueOf(review.getRating());
            holder.rating.setRating(rating);
            holder.rating.setVisibility(View.VISIBLE);
        } catch (NumberFormatException ex) {
            holder.rating.setRating(0);
            ex.printStackTrace();
        }

        // Add dates
        setTextView(holder.started, "Started: ", review.getStartedAt());
        setTextView(holder.read, "Read: ", review.getReadAt());
        setTextView(holder.added, "Added: ", review.getDateAdded());
        setTextView(holder.updated, "Updated: ", review.getDateUpdated());

        // Only show update date if different than addition date
        if (review.getDateAdded().equals(review.getDateUpdated())) {
            holder.updated.setVisibility(View.GONE);
        }

        // Add review and review title
        if (review.getBody().trim().length() > 0) {
            System.out.println("Review:" + review.getBody());
            holder.reviewTitle.setVisibility(View.VISIBLE);
            holder.body.setText(Html.fromHtml(review.getBody()));
            holder.body.setVisibility(View.VISIBLE);
        } else {
            holder.body.setText(review.getBody());
            holder.body.setVisibility(View.GONE);
            holder.reviewTitle.setVisibility(View.GONE);
        }

        return convertView;
    }

    private void setTextView(TextView textView, String title, String value) {
        textView.setText(title + value);
        if (value.length() > 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
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
         * Shows small book image.
         */
        ImageView image;

        /**
         * Shows arrow.
         */
        ImageView arrow;

        /**
         * Creates ViewHolder.
         *
         * @param view View
         */
        public ViewHolder(View view) {
            this.title = (TextView) view.findViewById(R.id.expandableListItemBookTitle);
            this.author = (TextView) view.findViewById(R.id.expandableListItemBookAuthor);
            this.image = (ImageView) view.findViewById(R.id.expandableListItemImage);
            this.arrow = (ImageView) view.findViewById(R.id.expandableListExpand);
        }
    }

    private class ChildViewHolder {

        TextView shelves;
        TextView started;
        TextView read;
        TextView added;
        TextView updated;
        RatingBar rating;
        TextView reviewTitle;
        TextView body;

        public ChildViewHolder(View view) {
            this.shelves = (TextView) view.findViewById(R.id.textView_expandableShelves);
            this.started = (TextView) view.findViewById(R.id.textView_expandableStarted);
            this.read = (TextView) view.findViewById(R.id.textView_expandableRead);
            this.added = (TextView) view.findViewById(R.id.textView_expandableAdded);
            this.updated = (TextView) view.findViewById(R.id.textView_expandableUpdated);
            this.rating = (RatingBar) view.findViewById(R.id.ratingBar_reviewRating);
            this.reviewTitle = (TextView) view.findViewById(R.id.textView_reviewTitle);
            this.body = (TextView) view.findViewById(R.id.textView_expandableBody);
        }
    }
}
