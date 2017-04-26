package net.bookbuddy.utilities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Jenni on 19.4.2017.
 */

public class Book implements Serializable {

    /**
     * Id of book, from search.
     */
    private String id;

    /**
     * Title of book, from search.
     */
    private String title;

    /**
     * TODO: Change authorId and authorName to this
     */
    private Author author;

    /**
     * Author id of book, from search.
     */
    private String authorId;

    /**
     * Author name of book, from search.
     */
    private String authorName;

    /**
     * Image url of book, from search.
     */
    private String imageUrl;

    /**
     * Small image url of book, from search.
     */
    private String smallImageUrl;

    /**
     * Url of book on GoodReads, from book/show.
     */
    private String url;

    /**
     * ISBN10 of book, from book/show.
     */
    private String isbnTen;

    /**
     * ISBN13 of book, from book/show.
     */
    private String isbnThirteen;

    /**
     * Description of book, from book/show.
     */
    private String description;

    /**
     * Date of publication, from book/show.
     */
    private Date publication;

    /**
     * Publisher of book, from book/show.
     */
    private String publisher;

    /**
     * Format of book, from book/show.
     */
    private String format;

    /**
     * Page number of book, from book/show.
     */
    private String pages;

    /**
     * List of authors of book, from book/show.
     */
    private List<Author> authors;

    /**
     * Default constructor.
     */
    public Book() {
    }

    /**
     * Constructor.
     *
     * @param id            String
     * @param title         String
     * @param authorId      String
     * @param authorName    String
     * @param imageUrl      String
     * @param smallImageUrl String
     */
    public Book(String id, String title, String authorId, String authorName, String imageUrl,
                String smallImageUrl) {
        this.id = id;
        this.title = title;
        this.author = new Author(authorId, authorName);
        this.authorId = authorId;
        this.authorName = authorName;
        this.imageUrl = imageUrl;
        this.smallImageUrl = smallImageUrl;
    }

    /**
     * Gets id.
     *
     * @return String id
     */
    public String getId() {
        return id;
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
     * Gets description.
     *
     * @return String description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets author.
     *
     * @return Author author
     */
    public Author getAuthor() {
        return author;
    }

    /**
     * Gets authorId.
     *
     * @return String authorId
     */
    public String getAuthorId() {
        return authorId;
    }

    /**
     * Gets author name.
     *
     * @return String authorName
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * Gets image url.
     *
     * @return String imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Gets small image url.
     *
     * @return String smallImageUrl
     */
    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    /**
     * Gets ISBN10.
     *
     * @return String ISBN 10
     */
    public String getIsbnTen() {
        return isbnTen;
    }

    /**
     * Gets ISBN13.
     *
     * @return String ISBN13
     */
    public String getIsbnThirteen() {
        return isbnThirteen;
    }

    /**
     * Gets url of book on GoodReads.
     *
     * @return String url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets publication date.
     *
     * @return Date publication
     */
    public Date getPublication() {
        return publication;
    }

    /**
     * Gets publisher of book.
     *
     * @return String publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Gets format of book.
     *
     * @return String format
     */
    public String getFormat() {
        return format;
    }

    /**
     * Gets book page amount.
     *
     * @return String page amount
     */
    public String getPages() {
        return pages;
    }

    /**
     * Gets authors of book.
     *
     * @return List<Author> authors
     */
    public List<Author> getAuthors() {
        return authors;
    }

    /**
     * Sets url of book page in GoodReads.
     * @param url String
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Sets ISBN10 of book.

     * @param isbnTen String
     */
    public void setIsbnTen(String isbnTen) {
        this.isbnTen = isbnTen;
    }

    /**
     * Sets ISBN13 of book.

     * @param isbnThirteen String
     */
    public void setIsbnThirteen(String isbnThirteen) {
        this.isbnThirteen = isbnThirteen;
    }

    /**
     * Sets description of book.

     * @param description String
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets publication date of book.

     * @param publication String
     */
    public void setPublication(Date publication) {
        this.publication = publication;
    }

    /**
     * Sets publisher of book.
     * @param publisher String
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Sates format of book.

     * @param format String
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Sets pages number of book.

     * @param pages String
     */
    public void setPages(String pages) {
        this.pages = pages;
    }

    /**
     * Sets authors of book.

     * @param authors List<Author>
     */
    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }
}
