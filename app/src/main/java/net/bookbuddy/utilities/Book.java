package net.bookbuddy.utilities;

/**
 * Created by Jenni on 19.4.2017.
 */

public class Book {

    private String id;
    private String title;
    private String authorId;
    private String authorName;
    private String imageUrl;
    private String smallImageUrl;

    public Book() {}

    public Book(String id, String title, String authorId, String authorName, String imageUrl,
                String smallImageUrl) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.authorName = authorName;
        this.imageUrl = imageUrl;
        this.smallImageUrl = smallImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }
}
