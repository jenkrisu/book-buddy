package net.bookbuddy.utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jenni on 19.4.2017.
 */

public class BookSearchResultsParser {

    /**
     * Parses document to ArrayList of Work objects.
     *
     * @param doc Document
     * @return List<Work>
     */
    public static List<Work> docToWorks(Document doc) {
        NodeList worksNodeList = doc.getElementsByTagName("work");
        ArrayList<Work> works = new ArrayList<>();

        if (worksNodeList != null) {

            for (int i = 0; i < worksNodeList.getLength(); i++) {
                Element workElement = (Element) worksNodeList.item(i);
                NodeList bookNodeList = workElement.getElementsByTagName("best_book");
                Element bookElement = (Element) bookNodeList.item(0);

                BestBook bestBook = elementToBook(bookElement);

                if (bestBook != null) {
                    Work work = elementToWork(workElement, bestBook);
                    if (work != null) {
                        works.add(work);
                    }
                }
            }
        }

        return works;
    }

    /**
     * Creates BestBook object from Element.
     *
     * @param e Element
     * @return BestBook
     */
    private static BestBook elementToBook(Element e) {
        // BestBook information from node
        if (hasBookTags(e)) {
            String id = e.getElementsByTagName("id").item(0).getTextContent();
            String title = e.getElementsByTagName("title").item(0).getTextContent();
            String imageUrl = e.getElementsByTagName("image_url").item(0).getTextContent();
            String smallImageUrl =
                    e.getElementsByTagName("small_image_url").item(0).getTextContent();
            String authorId = "";
            String authorName = "Unknown author";

            if (e.getElementsByTagName("author") != null) {
                // Author information from book's child node
                NodeList authorNodeList = e.getElementsByTagName("author");
                Element a = (Element) authorNodeList.item(0);

                if (hasAuthorTags(a)) {
                    authorId = a.getElementsByTagName("id").item(0).getTextContent();
                    authorName = a.getElementsByTagName("name").item(0).getTextContent();
                }
            }

            return new BestBook(id, title, authorId, authorName, imageUrl, smallImageUrl);
        } else {
            return null;
        }
    }

    /**
     * Creates Work object from Element.
     *
     * @param e Element
     * @param b BestBook
     * @return Work
     */
    private static Work elementToWork(Element e, BestBook b) {
        if (hasWorkTags(e)) {
            String id = e.getElementsByTagName("id").item(0).getTextContent();
            String booksCount = e.getElementsByTagName("books_count").item(0).getTextContent();
            String ratingsCount = e
                    .getElementsByTagName("ratings_count").item(0).getTextContent();
            String originalPublicationYer = e
                    .getElementsByTagName("original_publication_year").item(0).getTextContent();
            float averageRating = 0;
            try {
                averageRating = Float.parseFloat(e
                        .getElementsByTagName("average_rating").item(0).getTextContent());
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
            return new Work(id, booksCount, ratingsCount, originalPublicationYer, averageRating, b);
        } else {
            return null;
        }
    }

    /**
     * Checks element for necessary tags.
     *
     * @param e Element
     * @return boolean
     */
    private static boolean hasWorkTags(Element e) {
        return e.getElementsByTagName("id") != null
                && e.getElementsByTagName("books_count") != null
                && e.getElementsByTagName("ratings_count") != null
                && e.getElementsByTagName("original_publication_year") != null
                && e.getElementsByTagName("average_rating") != null;
    }

    /**
     * Checks element for necessary tags.
     *
     * @param e Element
     * @return boolean
     */
    private static boolean hasBookTags(Element e) {
        return e.getElementsByTagName("id") != null
                && e.getElementsByTagName("title") != null
                && e.getElementsByTagName("image_url") != null
                && e.getElementsByTagName("small_image_url") != null;
    }

    /**
     * Checks element for necessary tags.
     *
     * @param e Element
     * @return boolean
     */
    private static boolean hasAuthorTags(Element e) {
        return e.getElementsByTagName("id") != null
                && e.getElementsByTagName("name") != null;
    }
}
