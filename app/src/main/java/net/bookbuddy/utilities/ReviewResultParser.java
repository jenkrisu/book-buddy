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
        NodeList reviewsNodeList = doc.getElementsByTagName("review");

        if (reviewsNodeList != null) {

            for (int i = 0; i < reviewsNodeList.getLength(); i++) {

                Element e = (Element) reviewsNodeList.item(i);

                String id = "";
                Book book = null;
                String rating = "";
                String startedAt = "";
                String readAt = "";
                String dateAdded = "";
                String dateUpdated = "";
                String body = "";

                if (e.getElementsByTagName("id") != null) {
                    id = e.getElementsByTagName("id").item(0).getTextContent();
                }

                if (e.getElementsByTagName("book") != null) {
                    NodeList nodeList = e.getElementsByTagName("book");
                    Element b = (Element) nodeList.item(0);
                    book = BookResultParser.elementToBook(b);
                    System.out.println(book.getTitle());
                }



                System.out.println("IIDEE: " + id);

            }
        }

        return new ArrayList<Review>();
    }


}
