
package net.bookbuddy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.bookbuddy.R;
import net.bookbuddy.data.Shelf;

import java.util.List;

/**
 * Created by Jenni on 4.5.2017.
 */

/**
 * Populates custom list item.
 */
public class BookShelfAdapter extends ArrayAdapter<Shelf> {

    /**
     * Context of adapter.
     */
    private Context context;

    /**
     * Creates adapter.
     *
     * @param shelves ArrayList
     * @param context context
     */
    public BookShelfAdapter(List<Shelf> shelves, Context context) {
        super(context, R.layout.list_item_shelf, shelves);
        this.context = context;
    }

    /**
     * Updates convertView with Shelf object information.
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_shelf, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Shelf shelf = getItem(position);
        String name = shelf.getName();

        switch(name) {
            case "to-read":
                name = "To Read";
                break;
            case "currently-reading":
                name = "Currently Reading";
                break;
            case "read":
                name = "Read";
                break;
            default:
                break;
        }

        holder.name.setText(name);
        String amountText = "Contains " + shelf.getBookAmount() + " books";
        holder.bookAmount.setText(amountText);

        return convertView;
    }

    /**
     * Holds view of ListView item.
     */
    private class ViewHolder {

        /**
         * Holds shelf name.
         */
        TextView name;

        /**
         * Holds shelf book amount.
         */
        TextView bookAmount;

        /**
         * Creates ViewHolder.
         *
         * @param view View
         */
        public ViewHolder(View view) {
            this.name = (TextView) view.findViewById(R.id.listItemShelfName);
            this.bookAmount = (TextView) view.findViewById(R.id.listItemShelfBookAmount);
        }
    }

}