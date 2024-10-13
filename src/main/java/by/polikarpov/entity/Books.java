package by.polikarpov.entity;

import java.util.Objects;

public class Books {
    private Long id;
    private String title;
    private String author;
    private Library library;

    public Books(Long id, String title, String author, Library library) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.library = library;
    }

    public Books() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    @Override
    public String toString() {
        return "Books{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", author='" + author + '\'' +
               ", library=" + library.getLibraryName() +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Books books = (Books) o;
        return Objects.equals(id, books.id) && Objects.equals(title, books.title) && Objects.equals(author, books.author) && Objects.equals(library, books.library);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, library);
    }
}
