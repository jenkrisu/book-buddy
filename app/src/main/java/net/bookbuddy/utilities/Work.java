package net.bookbuddy.utilities;

/**
 * Created by Jenni on 19.4.2017.
 */

public class Work {

    private String id;
    private String booksCount;
    private String ratingsCount;
    private String originalPublicationYer;
    private float averageRating;
    private Book bestBook;

    public Work() {}

    public Work(String id, String booksCount, String ratingsCount, String originalPublicationYer,
                float averageRating, Book bestBook) {
        this.id = id;
        this.booksCount = booksCount;
        this.ratingsCount = ratingsCount;
        this.originalPublicationYer = originalPublicationYer;
        this.averageRating = averageRating;
        this.bestBook = bestBook;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBooksCount() {
        return booksCount;
    }

    public void setBooksCount(String booksCount) {
        this.booksCount = booksCount;
    }

    public String getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(String ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public String getOriginalPublicationYer() {
        return originalPublicationYer;
    }

    public void setOriginalPublicationYer(String originalPublicationYer) {
        this.originalPublicationYer = originalPublicationYer;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public Book getBestBook() {
        return bestBook;
    }

    public void setBestBook(Book bestBook) {
        this.bestBook = bestBook;
    }
}
