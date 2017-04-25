package net.bookbuddy.utilities;

import java.io.Serializable;

/**
 * Created by Jenni on 19.4.2017.
 */

public class Book implements Serializable {

    /**
     * Id of book.
     */
    private String id;

    /**
     * Title of book.
     */
    private String title;

    /**
     * Author id of book.
     */
    private String authorId;

    /**
     * Author name of book.
     */
    private String authorName;

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

}
