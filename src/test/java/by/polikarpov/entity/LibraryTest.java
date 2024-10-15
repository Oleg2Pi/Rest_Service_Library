package by.polikarpov.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    @Test
    void testConstructorWithParameters() {
        Library library = new Library("Main Library");
        library.setId(1L);
        assertEquals(1L, library.getId());
        assertEquals("Main Library", library.getLibraryName());
    }

    @Test
    void testDefaultConstructor() {
        Library library = new Library();
        assertNull(library.getId());
        assertNull(library.getLibraryName());
    }

    @Test
    void testSettersAndGetters() {
        Library library = new Library();
        library.setId(2L);
        library.setLibraryName("Community Library");

        assertEquals(2L, library.getId());
        assertEquals("Community Library", library.getLibraryName());
    }

    @Test
    void testEqualsAndHashCode() {
        Library library1 = new Library("Main Library");
        Library library2 = new Library("Main Library");
        Library library3 = new Library("Other Library");

        assertEquals(library1, library2);
        assertNotEquals(library1, library3);
        assertEquals(library1.hashCode(), library2.hashCode());
        assertNotEquals(library1.hashCode(), library3.hashCode());
    }

    @Test
    void testToString() {
        Library library = new Library("Main Library");
        library.setId(1L);
        String expectedString = "Library{id=1, LibraryName='Main Library'}";
        assertEquals(expectedString, library.toString());
    }
}
