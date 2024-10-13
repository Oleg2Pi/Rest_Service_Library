package by.polikarpov.entity;

import java.util.Objects;

public class Readers {

    private Long id;
    private String readersName;

    public Readers(String readersName) {
        this.readersName = readersName;
    }

    public Readers() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReadersName() {
        return readersName;
    }

    public void setReadersName(String readersName) {
        this.readersName = readersName;
    }

    @Override
    public String toString() {
        return "Readers{" +
               "id=" + id +
               ", readersName='" + readersName + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Readers readers = (Readers) o;
        return Objects.equals(readersName, readers.readersName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(readersName);
    }
}
