package net.bookbuddy.data;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jenni on 26.4.2017.
 */

public class Book implements Serializable {

    /**
     * Title of book.
     */
    private String title;

    /**
     * Url of book on GoodReads.
     */
    private String url;

    /**
     * ISBN10 of book.
     */
    private String isbnTen;

    /**
     * ISBN13 of book.
     */
    private String isbnThirteen;

    /**
     * Description of book.
     */
    private String description;

    /**
     * Date of publication.
     */
    private LocalDate publication;

    /**
     * Publisher of book.
     */
    private String publisher;

    /**
     * Format of book.
     */
    private String format;

    /**
     * Page number of book.
     */
    private String pages;

    /**
     * List of authors of book.
     */
    private List<Author> authors;

    /**
     * Reviews widget HTML.
     */
    private String reviewsWidgetHtml;

    /**
     * Default constructor.
     */
    public Book() {
    }

    /**
     * Constructs book.
     *
     * @param title             String
     * @param url               String
     * @param isbnTen           String
     * @param isbnThirteen      String
     * @param description       String
     * @param publication       String
     * @param publisher         String
     * @param format            String
     * @param pages             String
     * @param authors           String
     * @param reviewsWidgetHtml String
     */
    public Book(String title, String url, String isbnTen, String isbnThirteen, String description,
                LocalDate publication, String publisher, String format, String pages,
                List<Author> authors, String reviewsWidgetHtml) {
        this.title = title;
        this.url = url;
        this.isbnTen = isbnTen;
        this.isbnThirteen = isbnThirteen;
        this.description = description;
        this.publication = publication;
        this.publisher = publisher;
        this.format = format;
        this.pages = pages;
        this.authors = authors;
        this.reviewsWidgetHtml = reviewsWidgetHtml;
    }

    /**
     * Gets title.
     *
     * @return String title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets url.
     *
     * @return String url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets ISBN ten.
     *
     * @return String ISBN ten
     */
    public String getIsbnTen() {
        return isbnTen;
    }

    /**
     * Gets ISBN thirteen.
     *
     * @return String ISBN thirteen
     */
    public String getIsbnThirteen() {
        return isbnThirteen;
    }

    /**
     * Gets description of book.
     *
     * @return String description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets publication date.
     *
     * @return LocalDate publication date
     */
    public LocalDate getPublication() {
        return publication;
    }

    /**
     * Gets publisher.
     *
     * @return String publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Gets book format (hardcover, e-book, etc).
     *
     * @return String format
     */
    public String getFormat() {
        return format;
    }

    /**
     * Gets amount of pages.
     *
     * @return String page amount
     */
    public String getPages() {
        return pages;
    }

    /**
     * Gets list of authors and their roles.
     *
     * @return List<Author> authors
     */
    public List<Author> getAuthors() {
        return authors;
    }
}
