package by.polikarpov.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BooksTest {

    @Test
    void testConstructorWithParameters() {
        Library library = new Library(1L, "Central Library");
        Books book = new Books(1L, "Effective Java", "Joshua Bloch", library);

        assertEquals(1L, book.getId());
        assertEquals("Effective Java", book.getTitle());
        assertEquals("Joshua Bloch", book.getAuthor());
        assertEquals(library, book.getLibrary());
    }

    @Test
    void testDefaultConstructor() {
        Books book = new Books();

        assertNull(book.getId());
        assertNull(book.getTitle());
        assertNull(book.getAuthor());
        assertNull(book.getLibrary());
    }

    @Test
    void testSettersAndGetters() {
        Library library = new Library(1L, "Central Library");
        Books book = new Books();

        book.setId(3L);
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setLibrary(library);

        assertEquals(3L, book.getId());
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert C. Martin", book.getAuthor());
        assertEquals(library, book.getLibrary());
    }

    @Test
    void testEqualsAndHashCode() {
        Library library1 = new Library(1L, "Central Library");
        Library library2 = new Library(1L, "Central Library");
        Books book1 = new Books(1L, "Effective Java", "Joshua Bloch", library1);
        Books book2 = new Books(1L, "Effective Java", "Joshua Bloch", library2);
        Books book3 = new Books(2L, "Clean Code", "Robert C. Martin", library1);

        assertEquals(book1, book2); // должны быть равными
        assertNotEquals(book1, book3); // не равны
        assertEquals(book1.hashCode(), book2.hashCode()); // должны иметь одинаковый hashCode
        assertNotEquals(book1.hashCode(), book3.hashCode()); // должны иметь разные hashCode
    }

    @Test
    void testToString() {
        Library library = new Library(1L, "Central Library");
        Books book = new Books(1L, "Effective Java", "Joshua Bloch", library);

        String expectedString = "Books{id=1, title='Effective Java', author='Joshua Bloch', library=Central Library}";
        assertEquals(expectedString, book.toString());
    }
}
