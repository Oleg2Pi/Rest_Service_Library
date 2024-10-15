package by.polikarpov.service;

import by.polikarpov.dao.BooksDao;
import by.polikarpov.dto.BooksDto;
import by.polikarpov.entity.Books;
import by.polikarpov.entity.Library;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BooksServiceTest {

    private BooksService booksService;

    @Mock
    private BooksDao booksDaoMock;

    @BeforeEach
    void setUp() {
        booksService = BooksService.getInstance();
        booksService.setBooksDao(booksDaoMock);
    }

    @Test
    void getAll() {
        // Подготовка данных
        Library library = new Library("Library");
        library.setId(1L);
        Books book1 = new Books("Title 1", "Author 1", library);
        book1.setId(1L);

        Books book2 = new Books("Title 2", "Author 2", library);
        book2.setId(2L);

        when(booksDaoMock.findAll()).thenReturn(Arrays.asList(book1, book2));

        // Вызов метода
        List<BooksDto> result = booksService.getAll();

        // Проверка результата
        assertEquals(2, result.size());
        assertEquals("Title 1", result.get(0).title());
        assertEquals("Title 2", result.get(1).title());

        verify(booksDaoMock).findAll();
    }

    @Test
    void getByIdWhenExists() {
        // Подготовка данных
        Library library = new Library("Library");
        library.setId(1L);
        Books book = new Books("Title", "Author", library);
        book.setId(1L);

        when(booksDaoMock.findById(1L)).thenReturn(Optional.of(book));

        // Вызов метода
        Optional<BooksDto> result = booksService.getById(1L);

        // Проверка результата
        assertTrue(result.isPresent());
        assertEquals("Title", result.get().title());

        verify(booksDaoMock).findById(1L);
    }

    @Test
    void getByIdWhenBookDoesNotExist() {
        when(booksDaoMock.findById(1L)).thenReturn(Optional.empty());

        // Вызов метода
        Optional<BooksDto> result = booksService.getById(1L);

        // Проверка результата
        assertFalse(result.isPresent());
        verify(booksDaoMock).findById(1L);
    }

    @Test
    void getByLibraryId() {
        // Подготовка данных
        Library library = new Library("Library");
        library.setId(1L);
        Books book1 = new Books("Title 1", "Author", library);
        book1.setId(1L);
        Books book2 = new Books("Title 2", "Author", library);
        book2.setId(2L);

        when(booksDaoMock.findAllByLibraryId(1L)).thenReturn(Arrays.asList(book1, book2));

        // Вызов метода
        List<BooksDto> result = booksService.getAllByLibraryId(library.getId());

        // Проверка результата
        assertEquals(2, result.size());
        assertEquals("Title 1", result.get(0).title());
        assertEquals("Title 2", result.get(1).title());

        verify(booksDaoMock).findAllByLibraryId(library.getId());
    }

    @Test
    void addBook() {
        // Подготовка данных
        Library library = new Library("Library");
        library.setId(1L);
        BooksDto newBook = new BooksDto(null, "New Book", "New Author", library);
        Books book = new Books("New Book", "New Author", library);

        when(booksDaoMock.save(book)).thenReturn(book);

        // Вызов метода
        booksService.add(newBook);

        // Проверка, что bookDao.save был вызван
        verify(booksDaoMock).save(any(Books.class));
    }

    @Test
    void updateWhenExists() {
        // Подготовка данных
        Library library = new Library("Library");
        library.setId(1L);
        BooksDto existingBook = new BooksDto(1L, "Updated Book", "Author", library);
        Books book = new Books("Updated Book", "Author", library);
        book.setId(1L);

        when(booksDaoMock.findById(existingBook.id())).thenReturn(Optional.of(book));
        when(booksDaoMock.update(book)).thenReturn(true);

        // Вызов метода
        booksService.update(existingBook);

        // Проверка, что bookDao.update был вызван
        verify(booksDaoMock).update(any(Books.class));
        verify(booksDaoMock).findById(1L);
    }

    @Test
    void updateWhenBookDoesNotExist() {
        Library library = new Library("Library");
        library.setId(1L);
        BooksDto nonExistingReader = new BooksDto(1L, "Non-existing Book", "Author", library);
        when(booksDaoMock.findById(nonExistingReader.id())).thenReturn(Optional.empty());

        // Проверка вызова IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            booksService.update(nonExistingReader);
        });

        assertEquals("Books Non-existing Book does not exist", exception.getMessage());
    }

    @Test
    void deleteWhenExists() {
        Library library = new Library("Library");
        library.setId(1L);
        Long bookId = 1L;
        Books book = new Books("Book One", "Author", library);
        book.setId(bookId);

        when(booksDaoMock.findById(bookId)).thenReturn(Optional.of(book));
        when(booksDaoMock.delete(bookId)).thenReturn(true);

        // Вызов метода
        booksService.delete(bookId);

        // Проверка, что bookDao.delete был вызван
        verify(booksDaoMock).delete(bookId);
    }

    @Test
    void deleteWhenDoesNotExist() {
        Long bookId = 1L;
        when(booksDaoMock.findById(bookId)).thenReturn(Optional.empty());

        // Проверка вызова IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            booksService.delete(bookId);
        });

        assertEquals("Books " + bookId + " does not exist", exception.getMessage());
    }
}
