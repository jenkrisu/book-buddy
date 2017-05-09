package net.bookbuddy.data;

/**
 * Created by Jenni on 9.5.2017.
 */

public class Review {

    private String id;

    private Book book;

    private String rating;

    private String startedAt;

    private String readAt;

    private String dateAdded;

    private String dateUpdated;

    private String body;

    public Review() {
    }

    public Review(String id, Book book, String rating, String startedAt, String readAt,
                  String dateAdded, String dateUpdated, String body) {
        this.id = id;
        this.book = book;
        this.rating = rating;
        this.startedAt = startedAt;
        this.readAt = readAt;
        this.dateAdded = dateAdded;
        this.dateUpdated = dateUpdated;
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public String getRating() {
        return rating;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public String getReadAt() {
        return readAt;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public String getBody() {
        return body;
    }
}
