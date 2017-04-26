package net.bookbuddy.utilities;

import java.io.Serializable;

/**
 * Created by Jenni on 26.4.2017.
 */

public class Author implements Serializable {

    /**
     * Id of author, from book/search.
     */
    private String id;

    /**
     * Name of author, from book/search.
     */
    private String name;

    /**
     * Role of author, from book/search.
     */
    private String role;

    /**
     * Creates author.
     *
     * @param id   String
     * @param name String
     */
    public Author(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Creates author.
     *
     * @param id   String
     * @param name String
     * @param role String
     */
    public Author(String id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

}
