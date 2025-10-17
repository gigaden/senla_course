package ebookstore.model;

import ebookstore.model.enums.BookStatus;

public class Book {

    private long id;
    private String title;
    private String author;
    private String description;
    private BookStatus status;

    public Book(String title, String author, String description) {
        this.title = title;
        this.author = author;
        this.description = description;
        status = BookStatus.AVAILABLE;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}