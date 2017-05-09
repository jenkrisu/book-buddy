package net.bookbuddy.utilities;

import net.bookbuddy.data.Book;
import net.bookbuddy.data.Review;
import net.bookbuddy.data.Shelf;

import org.joda.time.LocalDate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jenni on 9.5.2017.
 */

public class ReviewResultParser {

    /**
     * Parses document to list of shelves.
     *
     * @param doc Document
     * @return Review
     */
    public static List<Review> docToReviews(Document doc) {
        List<Review> list = new ArrayList<Review>();
        NodeList reviewsNodeList = doc.getElementsByTagName("review");

        if (reviewsNodeList != null) {

            for (int i = 0; i < reviewsNodeList.getLength(); i++) {

                Element e = (Element) reviewsNodeList.item(i);

                String id = BookResultParser.getStringContent(e, "id");
                Book book = new Book();
                String rating = BookResultParser.getStringContent(e, "rating");
                String startedAt = BookResultParser.getStringContent(e, "started_at");
                String readAt = BookResultParser.getStringContent(e, "read_at");
                String dateAdded = BookResultParser.getStringContent(e, "date_added");
                String dateUpdated = BookResultParser.getStringContent(e, "date_updated");
                String body = BookResultParser.getStringContent(e, "body");
                List<Shelf> shelves = new ArrayList<Shelf>();

                startedAt = beautifyDateString(startedAt);
                readAt = beautifyDateString(readAt);
                dateAdded = beautifyDateString(dateAdded);
                dateUpdated = beautifyDateString(dateUpdated);

                if (e.getElementsByTagName("book") != null
                        && e.getElementsByTagName("book").getLength() > 0) {
                    NodeList nodeList = e.getElementsByTagName("book");
                    Element b = (Element) nodeList.item(0);
                    book = BookResultParser.elementToBook(b);
                }

                if (e.getElementsByTagName("shelf") != null
                        && e.getElementsByTagName("shelf").getLength() > 0) {
                    NodeList nodeList = e.getElementsByTagName("shelf");
                    shelves = BookShelfParser.nodeListToShelves(nodeList);
                }

                list.add(new Review(id, book, rating, startedAt, readAt, dateAdded, dateUpdated,
                        body, shelves));
            }
        }

        return list;
    }

    /**
     * Strips week day, time and timezone information from date string.
     *
     * @param string String to modify
     * @return String modified string
     */
    private static String beautifyDateString(String string) {
        if (string.length() < 1) {
            return "";
        }

        String[] array = string.split(" ");
        String beautified = "";

        // Example string: Mon Nov 09 00:00:00 -0800 2016
        // Result: Nov 9th 2016

        if (array.length == 6) {
            String dayWithSuffix = array[2];
            if (dayWithSuffix.substring(0,1).equals("0")) {
                dayWithSuffix = dayWithSuffix.substring(1);
            }
            try {
                dayWithSuffix += getLastDigitSuffix(Integer.parseInt(dayWithSuffix));
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
            beautified = array[1] + " " + dayWithSuffix + " " + array[5];
        }

        return beautified;
    }

    /**
     * Determines correct suffix for day digit.
     *
     * @param number day
     * @return String suffix
     */
    public static String getLastDigitSuffix(int number) {
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
}
