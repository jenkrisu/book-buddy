package net.bookbuddy.data;

import java.io.Serializable;

/**
 * Created by Jenni on 19.4.2017.
 */

public class Work implements Serializable {

    /**
     * Id of work.
     */
    private String id;

    /**
     * Books count of work.
     */
    private String booksCount;

    /**
     * Ratings count of work.
     */
    private String ratingsCount;

    /**
     * Original publication year of work.
     */
    private String originalPublicationYear;

    /**
     * Average rating of work.
     */
    private float averageRating;

    /**
     * Best book to represent this work.
     */
    private BestBook bestBook;

    /**
     * Default constructor for work.
     */
    public Work() {
    }

    /**
     * Constructor for work.
     *
     * @param id                     String
     * @param booksCount             String
     * @param ratingsCount           String
     * @param originalPublicationYer String
     * @param averageRating          Float
     * @param bestBook               BestBook
     */
    public Work(String id, String booksCount, String ratingsCount, String originalPublicationYer,
                float averageRating, BestBook bestBook) {
        this.id = id;
        this.booksCount = booksCount;
        this.ratingsCount = ratingsCount;
        this.originalPublicationYear = originalPublicationYer;
        this.averageRating = averageRating;
        this.bestBook = bestBook;
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
     * Gets books count.
     *
     * @return String books count
     */
    public String getBooksCount() {
        return booksCount;
    }

    /**
     * Gets ratings count.
     *
     * @return String ratings count
     */
    public String getRatingsCount() {
        return ratingsCount;
    }


    /**
     * Gets orig publication year.
     *
     * @return String orig publication year
     */
    public String getOriginalPublicationYear() {
        return originalPublicationYear;
    }


    /**
     * Gets avg rating.
     *
     * @return float average rating
     */
    public float getAverageRating() {
        return averageRating;
    }

    /**
     * Gets best book representation of this work.
     *
     * @return BestBook best book
     */
    public BestBook getBestBook() {
        return bestBook;
    }

}
