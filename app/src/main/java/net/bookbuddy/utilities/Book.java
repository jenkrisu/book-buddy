package net.bookbuddy.utilities;

import java.io.Serializable;
import java.util.Date;
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
    private Date publication;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIsbnTen() {
        return isbnTen;
    }

    public void setIsbnTen(String isbnTen) {
        this.isbnTen = isbnTen;
    }

    public String getIsbnThirteen() {
        return isbnThirteen;
    }

    public void setIsbnThirteen(String isbnThirteen) {
        this.isbnThirteen = isbnThirteen;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPublication() {
        return publication;
    }

    public void setPublication(Date publication) {
        this.publication = publication;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public String getReviewsWidgetHtml() {
        return reviewsWidgetHtml;
    }

    public void setReviewsWidgetHtml(String reviewsWidgetHtml) {
        this.reviewsWidgetHtml = reviewsWidgetHtml;
    }
}
