package net.bookbuddy.utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Created by Jenni on 26.4.2017.
 */

public class BookResultParser {

    /**
     * Parses document to book.
     *
     * @return Book book
     */
    public static Book docToBook(Document doc) {

        Book book = new Book();
        book.setUrl("moi");

        return book;
    }
}
