package by.polikarpov.entity;

import java.util.Objects;

/**
 * Represents a record of a book lending transaction between a reader and a book.
 */
public class BookLending {
    private Readers reader;
    private Books book;

    /**
     * Constructs a BookLending instance with the specified reader and book.
     *
     * @param reader the reader who is lending the book
     * @param book   the book being lent
     */
    public BookLending(Readers reader, Books book) {
        this.reader = reader;
        this.book = book;
    }

    /**
     * Default constructor for creating a BookLending instance without initial values.
     */
    public BookLending() {
    }

    /**
     * Returns the reader associated with this book lending.
     *
     * @return the reader who is lending the book
     */
    public Readers getReader() {
        return reader;
    }

    /**
     * Sets the reader for this book lending.
     *
     * @param reader the reader who is lending the book
     */
    public void setReader(Readers reader) {
        this.reader = reader;
    }

    /**
     * Returns the book associated with this book lending.
     *
     * @return the book being lent
     */
    public Books getBook() {
        return book;
    }

    /**
     * Sets the book for this book lending.
     *
     * @param book the book being lent
     */
    public void setBook(Books book) {
        this.book = book;
    }

    /**
     * Returns a string representation of the BookLending instance, including the reader's name and the book's title.
     *
     * @return a string representation of the BookLending instance
     */
    @Override
    public String toString() {
        return "BookLending{" +
               "reader=" + reader.getReadersName() +
               ", book=" + book.getTitle() +
               '}';
    }

    /**
     * Compares this BookLending instance to another object for equality.
     *
     * @param o the object to compare this BookLending against
     * @return true if the given object is also a BookLending and both have the same reader and book
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookLending that = (BookLending) o;
        return Objects.equals(reader, that.reader) && Objects.equals(book, that.book);
    }

    /**
     * Returns a hash code value for the BookLending instance.
     *
     * @return a hash code value for this BookLending
     */
    @Override
    public int hashCode() {
        return Objects.hash(reader, book);
    }
}
