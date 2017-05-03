package net.bookbuddy.data;

/**
 * Created by Jenni on 3.5.2017.
 */

public class Shelf {

    /**
     * Name of shelf.
     */
    private String name;

    /**
     * Id of shelf.
     */
    private String id;

    /**
     * Amount of books on shelf.
     */
    private String bookAmount;

    /**
     * Creates shelf.
     *
     * @param name String name
     * @param id   String id
     */
    public Shelf(String name, String id, String bookAmount) {
        this.name = name;
        this.id = id;
        this.bookAmount = bookAmount;
    }

    /**
     * Gets name.
     *
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name String name
     */
    public void setName(String name) {
        this.name = name;
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
     * Sets id.
     *
     * @param id String id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets book amount.
     *
     * @return String book amount
     */
    public String getBookAmount() {
        return bookAmount;
    }

    /**
     * Sets book amount.
     *
     * @param bookAmount
     */
    public void setBookAmount(String bookAmount) {
        this.bookAmount = bookAmount;
    }
}
