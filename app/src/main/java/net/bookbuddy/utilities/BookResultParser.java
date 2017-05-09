package net.bookbuddy.utilities;

import net.bookbuddy.data.Author;
import net.bookbuddy.data.Book;

import org.joda.time.LocalDate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

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
        NodeList bookNodeList = doc.getElementsByTagName("book");

        if (bookNodeList != null && bookNodeList.getLength() > 0) {
            Element e = (Element) bookNodeList.item(0);
            return elementToBook(e);
        } else {
            return new Book();
        }
    }

    /**
     * Parses Element to Book.
     *
     * @param e Element
     * @return Book book
     */
    public static Book elementToBook(Element e) {
        String title = "";
        String url = "";
        String isbn = "";
        String isbnThirteen = "";
        String description = "";
        LocalDate publication = getPublicationDate(e);
        String publisher = "";
        String format = "";
        String pages = "";
        List<Author> authors = getAuthors(e);

        String widget = "";

        if (e.getElementsByTagName("publisher") != null
                && e.getElementsByTagName("publisher").getLength() > 0) {
            publisher = e.getElementsByTagName("publisher").item(0).getTextContent();
        }

        if (e.getElementsByTagName("title") != null
                && e.getElementsByTagName("title").getLength() > 0) {
            title = e.getElementsByTagName("title").item(0).getTextContent();
        }

        if (e.getElementsByTagName("url") != null
                && e.getElementsByTagName("url").getLength() > 0) {
            url = e.getElementsByTagName("url").item(0).getTextContent();
        }

        if (e.getElementsByTagName("isbn") != null
                && e.getElementsByTagName("isbn").getLength() > 0) {
            isbn = e.getElementsByTagName("isbn").item(0).getTextContent();
        }

        if (e.getElementsByTagName("isbn13") != null
                && e.getElementsByTagName("isbn13").getLength() > 0) {
            isbnThirteen = e.getElementsByTagName("isbn13").item(0).getTextContent();
        }

        if (e.getElementsByTagName("description") != null
                && e.getElementsByTagName("description").getLength() > 0) {
            description = e.getElementsByTagName("description").item(0).getTextContent();
        }

        if (e.getElementsByTagName("format") != null
                && e.getElementsByTagName("format").getLength() > 0) {
            format = e.getElementsByTagName("format").item(0).getTextContent();
        }

        if (e.getElementsByTagName("num_pages") != null
                && e.getElementsByTagName("num_pages").getLength() > 0) {
            pages = e.getElementsByTagName("num_pages").item(0).getTextContent();
        }

        if (e.getElementsByTagName("reviews_widget") != null
                && e.getElementsByTagName("reviews_widget").getLength() > 0) {
            widget = e.getElementsByTagName("reviews_widget").item(0).getTextContent();
        }

        return new Book(title, url, isbn, isbnThirteen, description, publication, publisher,
                format, pages, authors, widget);
    }

    /**
     * Parses LocalDate from element.
     *
     * @param e Element
     * @return LocalDate
     */
    private static LocalDate getPublicationDate(Element e) {
        if (e.getElementsByTagName("publication_year") != null
                && e.getElementsByTagName("publication_month") != null
                && e.getElementsByTagName("publication_day") != null) {

            if (!e.getElementsByTagName("publication_year")
                    .item(0).getTextContent().equals("")
                    && !e.getElementsByTagName("publication_month")
                    .item(0).getTextContent().equals("")
                    && !e.getElementsByTagName("publication_day")
                    .item(0).getTextContent().equals("")) {

                try {
                    int year = Integer.parseInt(e.getElementsByTagName("publication_year")
                            .item(0).getTextContent());
                    int month = Integer.parseInt(e.getElementsByTagName("publication_month")
                            .item(0).getTextContent());
                    int day = Integer.parseInt(e.getElementsByTagName("publication_day")
                            .item(0).getTextContent());
                    return new LocalDate(year, month, day);
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * Parses authors.
     *
     * @param e Element
     * @return ArrayList
     */
    private static List<Author> getAuthors(Element e) {
        ArrayList<Author> authors = new ArrayList();

        // Contains
        if (e.getElementsByTagName("authors") != null) {

            // Authors element
            NodeList authorsNodeList = e.getElementsByTagName("authors");
            Element authorsElement = (Element) authorsNodeList.item(0);

            // Iterate Author elements
            NodeList authorNodeList = authorsElement.getElementsByTagName("author");

            for (int i = 0; i < authorNodeList.getLength(); i++) {
                Element a = (Element) authorNodeList.item(i);
                String authorId = "";
                String authorName = "";
                String authorRole = "";

                if (a.getElementsByTagName("id") != null) {
                    authorId = a.getElementsByTagName("id").item(0).getTextContent();
                }

                if (a.getElementsByTagName("name") != null) {
                    authorName = a.getElementsByTagName("name").item(0).getTextContent();
                }

                if (a.getElementsByTagName("role") != null) {
                    authorRole = a.getElementsByTagName("role").item(0).getTextContent();
                }

                authors.add(new Author(authorId, authorName, authorRole));
            }
        }

        return authors;
    }
}
