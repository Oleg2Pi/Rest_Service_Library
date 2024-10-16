package by.polikarpov.entity;

import java.util.Objects;

/**
 * Represents a reader with a unique identifier and a name.
 */
public class Readers {

    private Long id;
    private String readersName;

    /**
     * Constructs a Readers instance with the specified reader name.
     *
     * @param readersName the name of the reader
     */
    public Readers(String readersName) {
        this.readersName = readersName;
    }

    /**
     * Default constructor for creating a Readers instance without initial values.
     */
    public Readers() {
    }

    /**
     * Returns the unique identifier of the reader.
     *
     * @return the unique identifier of the reader
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this reader.
     *
     * @param id the unique identifier to set for this reader
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the reader.
     *
     * @return the name of the reader
     */
    public String getReadersName() {
        return readersName;
    }

    /**
     * Sets the name for this reader.
     *
     * @param readersName the name to set for this reader
     */
    public void setReadersName(String readersName) {
        this.readersName = readersName;
    }

    /**
     * Returns a string representation of the Readers instance, including the id and reader name.
     *
     * @return a string representation of the Readers instance
     */
    @Override
    public String toString() {
        return "Readers{" +
               "id=" + id +
               ", readersName='" + readersName + '\'' +
               '}';
    }

    /**
     * Compares this Readers instance to another object for equality.
     *
     * @param o the object to compare this Readers against
     * @return true if the given object is also a Readers and both have the same name
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Readers readers = (Readers) o;
        return Objects.equals(readersName, readers.readersName);
    }

    /**
     * Returns a hash code value for the Readers instance.
     *
     * @return a hash code value for this Readers
     */
    @Override
    public int hashCode() {
        return Objects.hash(readersName);
    }
}
