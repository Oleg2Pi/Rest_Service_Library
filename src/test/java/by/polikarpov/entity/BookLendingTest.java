package by.polikarpov.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookLendingTest {

    @Test
    void testConstructorWithParameters() {
        Readers reader = new Readers("John Doe");
        Books book = new Books("Effective Java", "Joshua Bloch", new Library("Central Library"));
        BookLending lending = new BookLending(reader, book);

        assertEquals(reader, lending.getReader());
        assertEquals(book, lending.getBook());
    }

    @Test
    void testDefaultConstructor() {
        BookLending lending = new BookLending();

        assertNull(lending.getReader());
        assertNull(lending.getBook());
    }

    @Test
    void testSettersAndGetters() {
        Readers reader = new Readers("John Doe");
        Books book = new Books("Effective Java", "Joshua Bloch", new Library("Central Library"));
        BookLending lending = new BookLending();

        lending.setReader(reader);
        lending.setBook(book);

        assertEquals(reader, lending.getReader());
        assertEquals(book, lending.getBook());
    }

    @Test
    void testEqualsAndHashCode() {
        Readers reader1 = new Readers("John Doe");
        Readers reader2 = new Readers("John Doe");
        Books book1 = new Books("Effective Java", "Joshua Bloch", new Library("Central Library"));
        Books book2 = new Books("Effective Java", "Joshua Bloch", new Library("Central Library"));

        BookLending lending1 = new BookLending(reader1, book1);
        BookLending lending2 = new BookLending(reader2, book2);
        BookLending lending3 = new BookLending(new Readers("Jane Doe"), book1);

        assertEquals(lending1, lending2);
        assertNotEquals(lending1, lending3);
        assertEquals(lending1.hashCode(), lending2.hashCode());
        assertNotEquals(lending1.hashCode(), lending3.hashCode());
    }

    @Test
    void testToString() {
        Readers reader = new Readers("John Doe");
        Books book = new Books("Effective Java", "Joshua Bloch", new Library("Central Library"));
        BookLending lending = new BookLending(reader, book);

        String expectedString = "BookLending{reader=John Doe, book=Effective Java}";
        assertEquals(expectedString, lending.toString());
    }
}
