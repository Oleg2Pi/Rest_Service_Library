package by.polikarpov.dao;

import by.polikarpov.entity.Books;
import by.polikarpov.entity.Library;
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
class BooksDaoTest {

    private BooksDao booksDao;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() {
        booksDao = BooksDao.getInstance();
    }

    @Test
    void testUpdate() {
        Library library = new Library("Main Library");
        Books book = new Books("Title 1", "Author 1", library);
        library.setId(1L);
        book.setId(1L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            // Мокаем статический метод
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeUpdate()).thenReturn(1);

            // Выполняем тест
            boolean result = booksDao.update(book);

            // Проверяем результаты
            assertTrue(result);
            verify(mockStatement).setString(1, "Title 1");
            verify(mockStatement).setString(2, "Author 1");
            verify(mockStatement).setLong(3, 1L);
            verify(mockStatement).setLong(4, 1L);
            verify(mockStatement).executeUpdate();
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testUpdateFails() {
        Library library = new Library("Main Library");
        Books book = new Books("Title 1", "Author 1", library);
        library.setId(1L);
        book.setId(1L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeUpdate()).thenReturn(0);

            assertFalse(booksDao.update(book));
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testUpdateThrowsException() {
        Library library = new Library("Main Library");
        Books book = new Books("Title 1", "Author 1", library);
        library.setId(1L);
        book.setId(1L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DataBase error"));

            assertThrows(DaoException.class, () -> booksDao.update(book));
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testFindAll() {
        Library library = new Library("Main Library");
        library.setId(1L);
        Books book1 = new Books("Title 1", "Author 1", library);
        book1.setId(1L);
        Books book2 = new Books("Title 2", "Author 2", library);
        book2.setId(2L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class);
             MockedStatic<LibraryDao> libraryDaoMock = mockStatic(LibraryDao.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next())
                    .thenReturn(true)
                    .thenReturn(true)
                    .thenReturn(false);
            when(mockResultSet.getLong("id")).thenReturn(1L, 2L);
            when(mockResultSet.getString("title"))
                    .thenReturn("Title 1", "Title 2");
            when(mockResultSet.getString("author"))
                    .thenReturn("Author 1", "Author 2");
            when(mockResultSet.getLong("library_id")).thenReturn(1L);

            LibraryDao mockLibraryDao = mock(LibraryDao.class);
            libraryDaoMock.when(LibraryDao::getInstance).thenReturn(mockLibraryDao);
            when(mockLibraryDao.findById(library.getId())).thenReturn(Optional.of(library));

            List<Books> books = booksDao.findAll();

            assertNotNull(books);
            assertEquals(2, books.size());
            assertEquals(book1.getId(), books.get(0).getId());
            assertEquals(book1.getTitle(), books.get(0).getTitle());
            assertEquals(book1.getAuthor(), books.get(0).getAuthor());
            assertEquals(library, books.get(0).getLibrary());
            assertEquals(book2.getId(), books.get(1).getId());
            assertEquals(book2.getTitle(), books.get(1).getTitle());
            assertEquals(book2.getAuthor(), books.get(1).getAuthor());
            assertEquals(library, books.get(1).getLibrary());

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

            DaoException sqlException = assertThrows(DaoException.class, () -> booksDao.findAll());

            assertEquals("Database error", sqlException.getCause().getMessage());

            verify(mockConnection).prepareStatement(anyString());
        } catch(SQLException e){
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testFindById() {
        Library library = new Library("Main Library");
        library.setId(1L);
        Books expectedBook = new Books("Title 1", "Author 1", library);
        expectedBook.setId(1L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class);
             MockedStatic<LibraryDao> libraryDaoMock = mockStatic(LibraryDao.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong("id")).thenReturn(1L);
            when(mockResultSet.getString("title")).thenReturn("Title 1");
            when(mockResultSet.getString("author")).thenReturn("Author 1");
            when(mockResultSet.getLong("library_id")).thenReturn(1L);

            LibraryDao mockLibraryDao = mock(LibraryDao.class);
            libraryDaoMock.when(LibraryDao::getInstance).thenReturn(mockLibraryDao);
            when(mockLibraryDao.findById(library.getId())).thenReturn(Optional.of(library));

            Optional<Books> book = booksDao.findById(expectedBook.getId());

            assertTrue(book.isPresent());
            assertEquals(expectedBook.getId(), book.get().getId());
            assertEquals(expectedBook.getTitle(), book.get().getTitle());
            assertEquals(expectedBook.getAuthor(), book.get().getAuthor());
            assertEquals(expectedBook.getLibrary(), book.get().getLibrary());

            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).setLong(1, expectedBook.getId());
            verify(mockStatement).executeQuery();

        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testFindByIdFails() throws SQLException {
        long bookId = 1L;

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            DaoException daoException = assertThrows(DaoException.class, () -> booksDao.findById(bookId));

            assertEquals("Database error", daoException.getCause().getMessage());

            verify(mockConnection).prepareStatement(anyString());
        }
    }

    @Test
    void testSave() throws SQLException {
        Library library = new Library("Main Library");
        library.setId(1L);
        Books book = new Books("Title 1", "Author 1", library);
        long generatedId = 1L;

        // Мокаем ConnectionManager.getConnection()
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong("id")).thenReturn(generatedId);

            // Запускаем метод
            Books savedBook = booksDao.save(book);

            // Проверяем результат
            assertNotNull(savedBook);
            assertEquals(generatedId, savedBook.getId());
            assertEquals(book.getTitle(), savedBook.getTitle());
            assertEquals(book.getAuthor(), savedBook.getAuthor());
            assertEquals(library, book.getLibrary());

            // Проверяем, что все методы были вызваны ожидаемым образом
            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).setString(1, book.getTitle());
            verify(mockStatement).setString(2, book.getAuthor());
            verify(mockStatement).setLong(3, library.getId());
            verify(mockStatement).executeQuery();
        }
    }

    @Test
    void testSaveFails() throws SQLException {
        Library library = new Library("Main Library");
        library.setId(1L);
        Books book = new Books("Title 1", "Author 1", library);
        book.setId(1L);

        // Мокаем ConnectionManager.getConnection()
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Проверяем, что DaoException взрывается
            DaoException daoException = assertThrows(DaoException.class, () -> booksDao.save(book));

            // Проверяем, что сообщение об ошибке соответствует ожиданиям
            assertEquals("Database error", daoException.getCause().getMessage());

            // Проверяем, что prepareStatement был вызван
            verify(mockConnection).prepareStatement(anyString());
        }
    }

    @Test
    void testDelete() throws SQLException {
        long bookId = 1L;

        // Мокаем ConnectionManager.getConnection()
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeUpdate()).thenReturn(1); // Успешное удаление

            // Запускаем метод
            boolean result = booksDao.delete(bookId);

            // Проверяем результат
            assertTrue(result);

            // Проверяем, что все методы были вызваны ожидаемым образом
            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).setLong(1, bookId);
            verify(mockStatement).executeUpdate();
        }
    }

    @Test
    void testDeleteFails() throws SQLException {
        Long bookId = 1L;

        // Мокаем ConnectionManager.getConnection()
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Проверяем, что DaoException взрывается
            DaoException daoException = assertThrows(DaoException.class, () -> booksDao.delete(bookId));

            // Проверяем, что сообщение об ошибке соответствует ожиданиям
            assertEquals("Database error", daoException.getCause().getMessage());

            // Проверяем, что prepareStatement был вызван
            verify(mockConnection).prepareStatement(anyString());
        }
    }
}
