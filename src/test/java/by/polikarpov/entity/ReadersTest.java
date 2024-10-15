package by.polikarpov.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReadersTest {

    @Test
    void testConstructorWithParameters() {
        Readers reader = new Readers("John Doe");
        reader.setId(1L);
        assertEquals(1L, reader.getId());
        assertEquals("John Doe", reader.getReadersName());
    }

    @Test
    void testDefaultConstructor() {
        Readers reader = new Readers();
        assertNull(reader.getId());
        assertNull(reader.getReadersName());
    }

    @Test
    void testSettersAndGetters() {
        Readers reader = new Readers();

        reader.setId(2L);
        reader.setReadersName("Jane Doe");

        assertEquals(2L, reader.getId());
        assertEquals("Jane Doe", reader.getReadersName());
    }

    @Test
    void testEqualsAndHashCode() {
        Readers reader1 = new Readers("John Doe");
        Readers reader2 = new Readers("John Doe");
        Readers reader3 = new Readers("Jane Doe");

        assertEquals(reader1, reader2);
        assertNotEquals(reader1, reader3);
        assertEquals(reader1.hashCode(), reader2.hashCode());
        assertNotEquals(reader1.hashCode(), reader3.hashCode());
    }

    @Test
    void testToString() {
        Readers reader = new Readers("John Doe");
        reader.setId(1L);
        String expectedString = "Readers{id=1, readersName='John Doe'}";
        assertEquals(expectedString, reader.toString());
    }
}
