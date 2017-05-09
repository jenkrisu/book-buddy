package net.bookbuddy.adapters;

import android.widget.BaseExpandableListAdapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        return convertView;
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
        public ChildViewHolder(View view) {
        }
    }
}
