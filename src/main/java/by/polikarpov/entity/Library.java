package by.polikarpov.entity;

import java.util.Objects;

public class Library {
    private Long id;
    private String LibraryName;

    public Library(String libraryName) {
        LibraryName = libraryName;
    }

    public Library() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibraryName() {
        return LibraryName;
    }

    public void setLibraryName(String libraryName) {
        LibraryName = libraryName;
    }

    @Override
    public String toString() {
        return "Library{" +
               "id=" + id +
               ", LibraryName='" + LibraryName + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Library library = (Library) o;
        return Objects.equals(LibraryName, library.LibraryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(LibraryName);
    }
}
