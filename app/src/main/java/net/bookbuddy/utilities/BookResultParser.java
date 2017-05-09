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
     * Gets string content from first Node of NodeList.
     *
     * @param e       Element
     * @param tagName String
     * @return String content
     */
    public static String getStringContent(Element e, String tagName) {
        if (e.getElementsByTagName(tagName) != null
                && e.getElementsByTagName(tagName).getLength() > 0) {
            return e.getElementsByTagName(tagName).item(0).getTextContent();
        } else {
            return "";
        }
    }

    /**
     * Parses Element to Book.
     *
     * @param e Element
     * @return Book book
     */
    public static Book elementToBook(Element e) {
        String title = getStringContent(e, "title");
        String url = getStringContent(e, "url");
        String isbn = getStringContent(e, "isbn");
        String isbnThirteen = getStringContent(e, "isbn13");
        String description = getStringContent(e, "description");
        LocalDate publication = getPublicationDate(e);
        String publisher = getStringContent(e, "publisher");
        String format = getStringContent(e, "format");
        String pages = getStringContent(e, "num_pages");
        List<Author> authors = getAuthors(e);
        String widget = getStringContent(e, "reviews_widget");
        String smallImageUrl = getStringContent(e, "small_image_url");

        return new Book(title, url, isbn, isbnThirteen, description, publication, publisher,
                format, pages, authors, widget, smallImageUrl);
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
                String authorId = getStringContent(a, "id");
                String authorName = getStringContent(a, "name");
                String authorRole = getStringContent(a, "role");
                authors.add(new Author(authorId, authorName, authorRole));
            }
        }

        return authors;
    }
}
