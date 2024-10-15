package by.polikarpov.dao;

import by.polikarpov.entity.BookLending;
import by.polikarpov.entity.Books;
import by.polikarpov.entity.Library;
import by.polikarpov.entity.Readers;
import by.polikarpov.exception.DaoException;
import by.polikarpov.util.ConnectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookLendingDaoTest {

    private BookLendingDao bookLendingDao;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() {
        bookLendingDao = BookLendingDao.getInstance();
    }

    @Test
    void testFindAll() {
        Library library = new Library("Main Library");
        library.setId(1L);
        Readers reader1 = new Readers("Reader 1");
        reader1.setId(1L);
        Readers reader2 = new Readers("Reader 2");
        reader2.setId(2L);
        Books book1 = new Books("Title 1", "Author 1", library);
        book1.setId(1L);
        Books book2 = new Books("Title 2", "Author 2", library);
        book2.setId(2L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class);
             MockedStatic<BooksDao> booksDaoMock = mockStatic(BooksDao.class);
             MockedStatic<ReadersDao> readerDaoMock = mockStatic(ReadersDao.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next())
                    .thenReturn(true)
                    .thenReturn(true)
                    .thenReturn(false);
            when(mockResultSet.getLong("reader_id")).thenReturn(1L, 2L);
            when(mockResultSet.getLong("book_id")).thenReturn(1L, 2L);

            BooksDao mockBooksDao = mock(BooksDao.class);
            booksDaoMock.when(BooksDao::getInstance).thenReturn(mockBooksDao);
            when(mockBooksDao.findById(book1.getId())).thenReturn(Optional.of(book1));
            when(mockBooksDao.findById(book2.getId())).thenReturn(Optional.of(book2));

            ReadersDao mockReadersDao = mock(ReadersDao.class);
            readerDaoMock.when(ReadersDao::getInstance).thenReturn(mockReadersDao);
            when(mockReadersDao.findById(reader1.getId())).thenReturn(Optional.of(reader1));
            when(mockReadersDao.findById(reader2.getId())).thenReturn(Optional.of(reader2));

            List<BookLending> bookLendings = bookLendingDao.findAll();

            assertNotNull(bookLendings);
            assertEquals(2, bookLendings.size());
            assertEquals(book1.getId(), bookLendings.get(0).getBook().getId());
            assertEquals(reader1.getId(), bookLendings.get(0).getReader().getId());
            assertEquals(book2.getId(), bookLendings.get(1).getBook().getId());
            assertEquals(reader2.getId(), bookLendings.get(1).getReader().getId());

            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).executeQuery();
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testFindAllFails() {
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            DaoException sqlException = assertThrows(DaoException.class, () -> bookLendingDao.findAll());

            assertEquals("Database error", sqlException.getCause().getMessage());

            verify(mockConnection).prepareStatement(anyString());
        } catch(SQLException e){
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testFindByReaderId() {
        Library library = new Library("Main Library");
        library.setId(1L);
        Readers reader = new Readers("Reader 1");
        reader.setId(1L);
        Books book1 = new Books("Title 1", "Author 1", library);
        book1.setId(1L);
        Books book2 = new Books("Title 2", "Author 2", library);
        book2.setId(2L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class);
             MockedStatic<BooksDao> booksDaoMock = mockStatic(BooksDao.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true, true, false);
            when(mockResultSet.getLong("book_id")).thenReturn(1L, 2L);

            BooksDao mockBooksDao = mock(BooksDao.class);
            booksDaoMock.when(BooksDao::getInstance).thenReturn(mockBooksDao);
            when(mockBooksDao.findById(book1.getId())).thenReturn(Optional.of(book1));
            when(mockBooksDao.findById(book2.getId())).thenReturn(Optional.of(book2));

            List<Books> books = bookLendingDao.findByReaderId(reader.getId());

            assertFalse(books.isEmpty());
            assertEquals(book1.getId(), books.get(0).getId());
            assertEquals(book1.getTitle(), books.get(0).getTitle());
            assertEquals(book2.getId(), books.get(1).getId());
            assertEquals(book2.getTitle(), books.get(1).getTitle());

            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).setLong(1, reader.getId());
            verify(mockStatement).executeQuery();

        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testFindByNotReaderId() {
        Library library = new Library("Main Library");
        library.setId(1L);
        Readers reader = new Readers("Reader 1");
        reader.setId(1L);
        Books book1 = new Books("Title 1", "Author 1", library);
        book1.setId(1L);
        Books book2 = new Books("Title 2", "Author 2", library);
        book2.setId(2L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class);
             MockedStatic<BooksDao> booksDaoMock = mockStatic(BooksDao.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true, true, false);
            when(mockResultSet.getLong("id")).thenReturn(1L, 2L);

            BooksDao mockBooksDao = mock(BooksDao.class);
            booksDaoMock.when(BooksDao::getInstance).thenReturn(mockBooksDao);
            when(mockBooksDao.findById(book1.getId())).thenReturn(Optional.of(book1));
            when(mockBooksDao.findById(book2.getId())).thenReturn(Optional.of(book2));

            List<Books> books = bookLendingDao.findByNotReaderId(2L);

            assertFalse(books.isEmpty());
            assertEquals(book1.getId(), books.get(0).getId());
            assertEquals(book1.getTitle(), books.get(0).getTitle());
            assertEquals(book2.getId(), books.get(1).getId());
            assertEquals(book2.getTitle(), books.get(1).getTitle());

            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).setLong(1, 2L);
            verify(mockStatement).executeQuery();

        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testFindByReaderIdFails() throws SQLException {
        long readerId = 1L;

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            DaoException daoException = assertThrows(DaoException.class, () -> bookLendingDao.findByReaderId(readerId));

            assertEquals("Database error", daoException.getCause().getMessage());

            verify(mockConnection).prepareStatement(anyString());
        }
    }

    @Test
    void testFindByBookId() {
        Library library = new Library("Main Library");
        library.setId(1L);
        Readers reader1 = new Readers("Reader 1");
        reader1.setId(1L);
        Readers reader2 = new Readers("Reader 2");
        reader2.setId(2L);
        Books book = new Books("Title 2", "Author 2", library);
        book.setId(1L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class);
             MockedStatic<ReadersDao> readerDaoMock = mockStatic(ReadersDao.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true, true, false);
            when(mockResultSet.getLong("reader_id")).thenReturn(1L, 2L);

            ReadersDao mockReadersDao = mock(ReadersDao.class);
            readerDaoMock.when(ReadersDao::getInstance).thenReturn(mockReadersDao);
            when(mockReadersDao.findById(reader1.getId())).thenReturn(Optional.of(reader1));
            when(mockReadersDao.findById(reader2.getId())).thenReturn(Optional.of(reader2));

            List<Readers> readers = bookLendingDao.findByBookId(book.getId());

            assertFalse(readers.isEmpty());
            assertEquals(reader1.getId(), readers.get(0).getId());
            assertEquals(reader1.getReadersName(), readers.get(0).getReadersName());
            assertEquals(reader2.getId(), readers.get(1).getId());
            assertEquals(reader2.getReadersName(), readers.get(1).getReadersName());

            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).setLong(1, book.getId());
            verify(mockStatement).executeQuery();

        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testFindByBookIdFails() throws SQLException {
        long bookId = 1L;

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            DaoException daoException = assertThrows(DaoException.class, () -> bookLendingDao.findByReaderId(bookId));

            assertEquals("Database error", daoException.getCause().getMessage());

            verify(mockConnection).prepareStatement(anyString());
        }
    }

    @Test
    void testSave() throws SQLException {
        Library library = new Library("Main Library");
        library.setId(1L);
        Readers reader = new Readers("Reader 1");
        reader.setId(1L);
        Books book = new Books("Title 1", "Author 1", library);
        book.setId(1L);
        BookLending bookLending = new BookLending(reader, book);

        // Мокаем ConnectionManager.getConnection()
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeUpdate()).thenReturn(1);

            // Запускаем метод
            BookLending savedBookLending = bookLendingDao.save(bookLending);

            // Проверяем результат
            assertNotNull(savedBookLending);
            assertEquals(book.getId(), savedBookLending.getBook().getId());
            assertEquals(reader.getId(), savedBookLending.getReader().getId());
            assertEquals(reader.getReadersName(), savedBookLending.getReader().getReadersName());

            // Проверяем, что все методы были вызваны ожидаемым образом
            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).setLong(1, reader.getId());
            verify(mockStatement).setLong(2, book.getId());
            verify(mockStatement).executeUpdate();
        }
    }

    @Test
    void testSaveFails() throws SQLException {
        Library library = new Library("Main Library");
        Readers reader = new Readers("Reader 1");
        reader.setId(1L);
        Books book = new Books("Title 1", "Author 1", library);
        book.setId(1L);
        BookLending bookLending = new BookLending(reader, book);

        // Мокаем ConnectionManager.getConnection()
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Проверяем, что DaoException взрывается
            DaoException daoException = assertThrows(DaoException.class, () -> bookLendingDao.save(bookLending));

            // Проверяем, что сообщение об ошибке соответствует ожиданиям
            assertEquals("Database error", daoException.getCause().getMessage());

            // Проверяем, что prepareStatement был вызван
            verify(mockConnection).prepareStatement(anyString());
        }
    }

    @Test
    void testDelete() throws SQLException {
        long readerId = 1L;
        long bookId = 1L;

        // Мокаем ConnectionManager.getConnection()
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeUpdate()).thenReturn(1); // Успешное удаление

            // Запускаем метод
            boolean result = bookLendingDao.delete(readerId, bookId);

            // Проверяем результат
            assertTrue(result);

            // Проверяем, что все методы были вызваны ожидаемым образом
            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).setLong(1, readerId);
            verify(mockStatement).setLong(1, bookId);
            verify(mockStatement).executeUpdate();
        }
    }

    @Test
    void testDeleteFails() throws SQLException {
        long readerId = 1L;
        long bookId = 1L;

        // Мокаем ConnectionManager.getConnection()
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Проверяем, что DaoException взрывается
            DaoException daoException = assertThrows(DaoException.class, () -> bookLendingDao.delete(readerId, bookId));

            // Проверяем, что сообщение об ошибке соответствует ожиданиям
            assertEquals("Database error", daoException.getCause().getMessage());

            // Проверяем, что prepareStatement был вызван
            verify(mockConnection).prepareStatement(anyString());
        }
    }
}
