package net.bookbuddy.utilities;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jenni on 26.4.2017.
 */

public class Book implements Serializable {

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

    public Book(String url, String isbnTen, String isbnThirteen, String description,
                LocalDate publication, String publisher, String format, String pages,
                List<Author> authors, String reviewsWidgetHtml) {
        this.url = url;
        this.isbnTen = isbnTen;
        this.isbnThirteen = isbnThirteen;
        this.description = description;
        this.publication = publication;
        this.publisher = publisher;
        this.format = format;
        this. pages = pages;
        this.authors = authors;
        this.reviewsWidgetHtml = reviewsWidgetHtml;
    }
    public String getUrl() {
        return url;
    }

    public String getIsbnTen() {
        return isbnTen;
    }

    public String getIsbnThirteen() {
        return isbnThirteen;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getPublication() {
        return publication;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getFormat() {
        return format;
    }

    public String getPages() {
        return pages;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public String getReviewsWidgetHtml() {
        return reviewsWidgetHtml;
    }
}
