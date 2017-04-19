package net.bookbuddy.utilities;

/**
 * Created by Jenni on 19.4.2017.
 */

public class Work {

    private int id;
    private int booksCount;
    private int ratingsCount;
    private int originalPublicationYer;
    private float averageRating;
    private Book bestBook;

    public Work(int id, int booksCount, int ratingsCount, int originalPublicationYer,
                float averageRating, Book bestBook) {
        this.id = id;
        this.booksCount = booksCount;
        this.ratingsCount = ratingsCount;
        this.originalPublicationYer = originalPublicationYer;
        this.averageRating = averageRating;
        this.bestBook = bestBook;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBooksCount() {
        return booksCount;
    }

    public void setBooksCount(int booksCount) {
        this.booksCount = booksCount;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(int ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public int getOriginalPublicationYer() {
        return originalPublicationYer;
    }

    public void setOriginalPublicationYer(int originalPublicationYer) {
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
