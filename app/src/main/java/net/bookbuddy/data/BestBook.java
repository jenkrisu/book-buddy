package net.bookbuddy.data;

import java.io.Serializable;

/**
 * Holds data of best book.
 * <p>
 * Created by Jenni on 19.4.2017.
 */
public class BestBook implements Serializable {

    /**
     * Id of book.
     */
    private String id;

    /**
     * Title of book.
     */
    private String title;

    /**
     * TODO: Change authorId and authorName to this
     */
    private Author author;

    /**
     * Image url of book.
     */
    private String imageUrl;

    /**
     * Small image url of book.
     */
    private String smallImageUrl;

    /**
     * Default constructor.
     */
    public BestBook() {
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
    public BestBook(String id, String title, String authorId, String authorName, String imageUrl,
                    String smallImageUrl) {
        this.id = id;
        this.title = title;
        this.author = new Author(authorId, authorName);
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
     * Gets author.
     *
     * @return Author author
     */
    public Author getAuthor() {
        return author;
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

}
