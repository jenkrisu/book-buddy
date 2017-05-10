package net.bookbuddy.data;

import java.util.List;

/**
 * Holds review data.
 * <p>
 * Created by Jenni on 9.5.2017.
 */
public class Review {

    /**
     * Id of review.
     */
    private String id;

    /**
     * Book.
     */
    private Book book;

    /**
     * Rating of book connected to this review.
     */
    private String rating;

    /**
     * Date when started reading book.
     */
    private String startedAt;

    /**
     * Date when finished reading book.
     */
    private String readAt;

    /**
     * Date when book added to shelves.
     */
    private String dateAdded;

    /**
     * Date when book updated.
     */
    private String dateUpdated;

    /**
     * Book review.
     */
    private String body;

    /**
     * Shelves book connected to this review is on.
     */
    private List<Shelf> shelves;

    /**
     * Default constructor.
     */
    public Review() {
    }

    /**
     * Creates review.
     *
     * @param id          String book id
     * @param book        Book book
     * @param rating      String rating of book
     * @param startedAt   String date when started reading book
     * @param readAt      String date when finished reading book
     * @param dateAdded   String date when book added
     * @param dateUpdated String date when book updated
     * @param body        String review body
     * @param shelves     List shelves
     */
    public Review(String id, Book book, String rating, String startedAt, String readAt,
                  String dateAdded, String dateUpdated, String body, List<Shelf> shelves) {
        this.id = id;
        this.book = book;
        this.rating = rating;
        this.startedAt = startedAt;
        this.readAt = readAt;
        this.dateAdded = dateAdded;
        this.dateUpdated = dateUpdated;
        this.body = body;
        this.shelves = shelves;
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
     * Gets book.
     *
     * @return Book book
     */
    public Book getBook() {
        return book;
    }

    /**
     * Gets rating.
     *
     * @return String rating
     */
    public String getRating() {
        return rating;
    }

    /**
     * Gets startedAt.
     *
     * @return String starting date
     */
    public String getStartedAt() {
        return startedAt;
    }

    /**
     * Gets readAt.
     *
     * @return String reading date
     */
    public String getReadAt() {
        return readAt;
    }

    /**
     * Gets dateAdded.
     *
     * @return String date added
     */
    public String getDateAdded() {
        return dateAdded;
    }

    /**
     * Gets dateUpdated.
     *
     * @return String date updated
     */
    public String getDateUpdated() {
        return dateUpdated;
    }

    /**
     * Gets review text.
     *
     * @return String review text
     */
    public String getBody() {
        return body;
    }

    public List<Shelf> getShelves() {
        return shelves;
    }
}
