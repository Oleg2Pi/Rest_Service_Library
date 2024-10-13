package by.polikarpov.entity;

import java.util.Objects;

public class BookLending {
    private Readers reader;
    private Books book;

    public BookLending(Readers reader, Books book) {
        this.reader = reader;
        this.book = book;
    }

    public BookLending() {
    }

    public Readers getReader() {
        return reader;
    }

    public void setReader(Readers reader) {
        this.reader = reader;
    }

    public Books getBook() {
        return book;
    }

    public void setBook(Books book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return "BookLending{" +
               "reader=" + reader.getReadersName() +
               ", book=" + book.getTitle() +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookLending that = (BookLending) o;
        return Objects.equals(reader, that.reader) && Objects.equals(book, that.book);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reader, book);
    }
}
