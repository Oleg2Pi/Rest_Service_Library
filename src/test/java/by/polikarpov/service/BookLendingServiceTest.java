package by.polikarpov.service;

import by.polikarpov.dao.BookLendingDao;
import by.polikarpov.dto.BookLendingDto;
import by.polikarpov.dto.BooksDto;
import by.polikarpov.dto.ReadersDto;
import by.polikarpov.entity.BookLending;
import by.polikarpov.entity.Books;
import by.polikarpov.entity.Library;
import by.polikarpov.entity.Readers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookLendingServiceTest {

    private BookLendingService bookLendingService;

    @Mock
    private BookLendingDao bookLendingDaoMock;

    @BeforeEach
    void setUp() {
        bookLendingService = BookLendingService.getInstance();
        bookLendingService.setBookLendingDao(bookLendingDaoMock);
    }

    @Test
    void getAll() {
        // Подготовка данных
        Library library = new Library("Library");
        Readers reader1 = new Readers("Reader One");
        reader1.setId(1L);
        Books book1 = new Books("Title 1", "Author", library);
        book1.setId(1L);

        Readers reader2 = new Readers("Reader Two");
        reader2.setId(2L);
        Books book2 = new Books("Title 2", "Author", library);
        book2.setId(1L);

        BookLending bookLending1 = new BookLending(reader1, book1);
        BookLending bookLending2 = new BookLending(reader2, book2);

        when(bookLendingDaoMock.findAll()).thenReturn(Arrays.asList(bookLending1, bookLending2));

        // Вызов метода
        List<BookLendingDto> result = bookLendingService.getAll();

        // Проверка результата
        assertEquals(2, result.size());
        assertEquals("Reader One", result.get(0).readers().readerName());
        assertEquals("Title 1", result.get(0).books().title());
        assertEquals("Reader Two", result.get(1).readers().readerName());
        assertEquals("Title 2", result.get(1).books().title());

        verify(bookLendingDaoMock).findAll();
    }

    @Test
    void getByReaderIdWhenExists() {
        // Подготовка данных
        Library library = new Library("Library");
        Books book1 = new Books("Title 1", "Author", library);
        book1.setId(1L);
        Books book2 = new Books("Title 2", "Author", library);
        book2.setId(1L);

        when(bookLendingDaoMock.findByReaderId(1L)).thenReturn(Arrays.asList(book1, book2));

        // Вызов метода
        List<BooksDto> result = bookLendingService.getByReaderId(1L);

        // Проверка результата
        assertEquals(2, result.size());
        assertEquals("Title 1", result.get(0).title());
        assertEquals("Title 2", result.get(1).title());

        verify(bookLendingDaoMock).findByReaderId(1L);
    }

    @Test
    void getByIdWhenReaderDoesNotExist() {
        when(bookLendingDaoMock.findByReaderId(1L)).thenReturn(new ArrayList<>());

        // Вызов метода
        List<BooksDto> result = bookLendingService.getByReaderId(1L);

        // Проверка результата
        assertEquals(0, result.size());
        verify(bookLendingDaoMock).findByReaderId(1L);
    }

    @Test
    void getByNotReaderId() {
        // Подготовка данных
        Library library = new Library("Library");
        Books book1 = new Books("Title 1", "Author", library);
        book1.setId(1L);
        Books book2 = new Books("Title 2", "Author", library);
        book2.setId(1L);

        when(bookLendingDaoMock.findByNotReaderId(1L)).thenReturn(Arrays.asList(book1, book2));

        // Вызов метода
        List<BooksDto> result = bookLendingService.getByNotReaderId(1L);

        // Проверка результата
        assertEquals(2, result.size());
        assertEquals("Title 1", result.get(0).title());
        assertEquals("Title 2", result.get(1).title());

        verify(bookLendingDaoMock).findByNotReaderId(1L);
    }

    @Test
    void getByBookIdWhenExists() {
        // Подготовка данных
        Readers reader1 = new Readers("Reader 1");
        reader1.setId(1L);
        Readers reader2 = new Readers("Reader 2");
        reader2.setId(1L);

        when(bookLendingDaoMock.findByBookId(1L)).thenReturn(Arrays.asList(reader1, reader2));

        // Вызов метода
        List<ReadersDto> result = bookLendingService.getByBookId(1L);

        // Проверка результата
        assertEquals(2, result.size());
        assertEquals("Reader 1", result.get(0).readerName());
        assertEquals("Reader 2", result.get(1).readerName());

        verify(bookLendingDaoMock).findByBookId(1L);
    }

    @Test
    void getByIdWhenBookDoesNotExist() {
        when(bookLendingDaoMock.findByBookId(1L)).thenReturn(new ArrayList<>());

        // Вызов метода
        List<ReadersDto> result = bookLendingService.getByBookId(1L);

        // Проверка результата
        assertEquals(0, result.size());
        verify(bookLendingDaoMock).findByBookId(1L);
    }

    @Test
    void addReader() {
        // Подготовка данных
        Library library = new Library("Library");
        ReadersDto reader = new ReadersDto(null, "New Reader");
        BooksDto book = new BooksDto(null, "New Title", "author", library);


        Readers reader1 = new Readers( "New Reader");
        Books book1 = new Books( "New Title", "author", library);

        BookLendingDto bookLendingDto = new BookLendingDto(reader, book);
        BookLending bookLending = new BookLending(reader1, book1);

        when(bookLendingDaoMock.save(bookLending)).thenReturn(bookLending);

        // Вызов метода
        bookLendingService.add(bookLendingDto);

        // Проверка, что readerDao.save был вызван
        verify(bookLendingDaoMock).save(any(BookLending.class));
    }

    @Test
    void deleteWhenExists() {
        Library library = new Library("Library");
        Long readerId = 1L;
        Readers reader = new Readers("Reader 1");
        Long bookId = 1L;
        Books book = new Books("New Title", "author", library);

        when(bookLendingDaoMock.findByReaderId(readerId)).thenReturn(List.of(book));
        when(bookLendingDaoMock.findByBookId(bookId)).thenReturn(List.of(reader));
        when(bookLendingDaoMock.delete(readerId, bookId)).thenReturn(true);

        // Вызов метода
        bookLendingService.delete(readerId, bookId);

        // Проверка, что readerDao.delete был вызван
        verify(bookLendingDaoMock).delete(readerId, bookId);
        verify(bookLendingDaoMock).findByReaderId(readerId);
        verify(bookLendingDaoMock).findByBookId(bookId);
    }

    @Test
    void deleteWhenReaderDoesNotExist() {
        Long readerId = 1L;
        Long bookId = 1L;
        when(bookLendingDaoMock.findByReaderId(readerId)).thenReturn(new ArrayList<>());

        // Проверка вызова IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bookLendingService.delete(readerId, bookId));

        assertEquals("Reader " + readerId + " does not exist", exception.getMessage());
    }
}
