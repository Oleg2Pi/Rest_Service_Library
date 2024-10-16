package by.polikarpov.entity;

import java.util.Objects;

/**
 * Represents a book with a title, author, associated library, and an identifier.
 */
public class Books {
    private Long id;
    private String title;
    private String author;
    private Library library;

    /**
     * Constructs a Books instance with the specified title, author, and associated library.
     *
     * @param title   the title of the book
     * @param author  the author of the book
     * @param library the library to which the book belongs
     */
    public Books(String title, String author, Library library) {
        this.title = title;
        this.author = author;
        this.library = library;

    }

    /**
     * Default constructor for creating a Books instance without initial values.
     */
    public Books() {
    }

    /**
     * Returns the unique identifier of the book.
     *
     * @return the unique identifier of the book
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this book.
     *
     * @param id the unique identifier to set for this book
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the title of the book.
     *
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title for this book.
     *
     * @param title the title to set for this book
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the author of the book.
     *
     * @return the author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author for this book.
     *
     * @param author the author to set for this book
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Returns the library associated with this book.
     *
     * @return the library to which this book belongs
     */
    public Library getLibrary() {
        return library;
    }

    /**
     * Sets the library associated with this book.
     *
     * @param library the library to set for this book
     */
    public void setLibrary(Library library) {
        this.library = library;
    }

    /**
     * Returns a string representation of the Books instance, including the id, title, author, and library name.
     *
     * @return a string representation of the Books instance
     */
    @Override
    public String toString() {
        return "Books{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", author='" + author + '\'' +
               ", library=" + library.getLibraryName() +
               '}';
    }

    /**
     * Compares this Books instance to another object for equality.
     *
     * @param o the object to compare this Books against
     * @return true if the given object is also a Books and both have the same title, author, and library
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Books books = (Books) o;
        return Objects.equals(title, books.title) && Objects.equals(author, books.author) && Objects.equals(library, books.library);
    }

    /**
     * Returns a hash code value for the Books instance.
     *
     * @return a hash code value for this Books
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, author, library);
    }
}
