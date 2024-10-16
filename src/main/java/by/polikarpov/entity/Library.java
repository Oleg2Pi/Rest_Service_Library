package by.polikarpov.entity;

import java.util.Objects;

/**
 * Represents a library with a unique identifier and a name.
 */
public class Library {
    private Long id;
    private String LibraryName;

    /**
     * Constructs a Library instance with the specified library name.
     *
     * @param libraryName the name of the library
     */
    public Library(String libraryName) {
        LibraryName = libraryName;
    }

    /**
     * Default constructor for creating a Library instance without initial values.
     */
    public Library() {
    }

    /**
     * Returns the unique identifier of the library.
     *
     * @return the unique identifier of the library
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this library.
     *
     * @param id the unique identifier to set for this library
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the library.
     *
     * @return the name of the library
     */
    public String getLibraryName() {
        return LibraryName;
    }

    /**
     * Sets the name for this library.
     *
     * @param libraryName the name to set for this library
     */
    public void setLibraryName(String libraryName) {
        LibraryName = libraryName;
    }

    /**
     * Returns a string representation of the Library instance, including the id and library name.
     *
     * @return a string representation of the Library instance
     */
    @Override
    public String toString() {
        return "Library{" +
               "id=" + id +
               ", LibraryName='" + LibraryName + '\'' +
               '}';
    }

    /**
     * Compares this Library instance to another object for equality.
     *
     * @param o the object to compare this Library against
     * @return true if the given object is also a Library and both have the same name
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Library library = (Library) o;
        return Objects.equals(LibraryName, library.LibraryName);
    }

    /**
     * Returns a hash code value for the Library instance.
     *
     * @return a hash code value for this Library
     */
    @Override
    public int hashCode() {
        return Objects.hash(LibraryName);
    }
}
