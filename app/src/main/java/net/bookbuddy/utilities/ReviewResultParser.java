package net.bookbuddy.utilities;

import net.bookbuddy.data.Book;
import net.bookbuddy.data.Review;

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

    public static List<Review> docToReviews(Document doc) {
        List<Review> list = new ArrayList<Review>();
        NodeList reviewsNodeList = doc.getElementsByTagName("review");

        if (reviewsNodeList != null) {

            for (int i = 0; i < reviewsNodeList.getLength(); i++) {

                Element e = (Element) reviewsNodeList.item(i);

                String id = BookResultParser.getStringContent(e, "id");
                Book book = null;
                String rating = BookResultParser.getStringContent(e, "rating");
                String startedAt = BookResultParser.getStringContent(e, "startedAt");
                String readAt = BookResultParser.getStringContent(e, "readAt");
                String dateAdded = BookResultParser.getStringContent(e, "dateAdded");
                String dateUpdated = BookResultParser.getStringContent(e, "dateUpdated");
                String body = BookResultParser.getStringContent(e, "body");

                if (e.getElementsByTagName("book") != null
                        && e.getElementsByTagName("book").getLength() > 0) {
                    NodeList nodeList = e.getElementsByTagName("book");
                    Element b = (Element) nodeList.item(0);
                    book = BookResultParser.elementToBook(b);
                }

                list.add(new Review(id, book, rating, startedAt, readAt, dateAdded, dateUpdated,
                        body));
            }
        }

        return list;
    }
}
