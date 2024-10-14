package by.polikarpov.dao;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryDaoTest {

    private LibraryDao libraryDao;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() {
        libraryDao = LibraryDao.getInstance();
    }

    @Test
    void testUpdate() {
        Library library = new Library("Main Library");
        library.setId(1L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            // Мокаем статический метод
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeUpdate()).thenReturn(1);

            // Выполняем тест
            boolean result = libraryDao.update(library);

            // Проверяем результаты
            assertTrue(result);
            verify(mockStatement).setString(1, "Main Library");
            verify(mockStatement).setLong(2, 1L);
            verify(mockStatement).executeUpdate();
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testUpdateFails () {
        Library library = new Library("Main Library");
        library.setId(1L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeUpdate()).thenReturn(0);

            assertFalse(libraryDao.update(library));
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testUpdateThrowsException() {
        Library library = new Library("Main Library");
        library.setId(1L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DataBase error"));

            assertThrows(DaoException.class, () -> libraryDao.update(library));
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testFindAll() {
        Library expectedLibrary1 = new Library("Main Library");
        expectedLibrary1.setId(1L);
        Library expectedLibrary2 = new Library("University Library");
        expectedLibrary2.setId(2L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next())
                    .thenReturn(true)
                    .thenReturn(true)
                    .thenReturn(false);
            when(mockResultSet.getLong("id")).thenReturn(1L, 2L);
            when(mockResultSet.getString("library_name")).thenReturn("Main Library", "University Library");

            List<Library> libraries = libraryDao.findAll();

            assertNotNull(libraries);
            assertEquals(2, libraries.size());
            assertEquals(expectedLibrary1.getId(), libraries.get(0).getId());
            assertEquals(expectedLibrary1.getLibraryName(), libraries.get(0).getLibraryName());
            assertEquals(expectedLibrary2.getId(), libraries.get(1).getId());
            assertEquals(expectedLibrary2.getLibraryName(), libraries.get(1).getLibraryName());

            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).executeQuery();
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testFindAllFails () {
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            DaoException sqlException = assertThrows(DaoException.class, () -> libraryDao.findAll());

            assertEquals("Database error", sqlException.getCause().getMessage());

            verify(mockConnection).prepareStatement(anyString());
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testFindById() {
        Library expectedLibrary = new Library("Main Library");
        expectedLibrary.setId(1L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong("id")).thenReturn(1L);
            when(mockResultSet.getString("library_name")).thenReturn("Main Library");

            Optional<Library> library = libraryDao.findById(expectedLibrary.getId());
            
            assertTrue(library.isPresent());
            assertEquals(expectedLibrary.getId(), library.get().getId());
            assertEquals(expectedLibrary.getLibraryName(), library.get().getLibraryName());

            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).setLong(1, library.get().getId());
            verify(mockStatement).executeQuery();

        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testFindByIdFails() throws SQLException {
        Long libraryId = 1L;

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            DaoException daoException = assertThrows(DaoException.class, () -> libraryDao.findById(libraryId));

            assertEquals("Database error", daoException.getCause().getMessage());

            verify(mockConnection).prepareStatement(anyString());
        }
    }

    @Test
    void testSave() throws SQLException {
        Library libraryToSave = new Library("Main Library");
        long generatedId = 1L;

        // Мокаем ConnectionManager.getConnection()
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong("id")).thenReturn(generatedId);

            // Запускаем метод
            Library savedLibrary = libraryDao.save(libraryToSave);

            // Проверяем результат
            assertNotNull(savedLibrary);
            assertEquals(generatedId, savedLibrary.getId());
            assertEquals(libraryToSave.getLibraryName(), savedLibrary.getLibraryName());

            // Проверяем, что все методы были вызваны ожидаемым образом
            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).setString(1, libraryToSave.getLibraryName());
            verify(mockStatement).executeQuery();
        }
    }

    @Test
    void testSaveFails() throws SQLException {
        Library libraryToSave = new Library("Main Library");

        // Мокаем ConnectionManager.getConnection()
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Проверяем, что DaoException взрывается
            DaoException daoException = assertThrows(DaoException.class, () -> libraryDao.save(libraryToSave));

            // Проверяем, что сообщение об ошибке соответствует ожиданиям
            assertEquals("Database error", daoException.getCause().getMessage());

            // Проверяем, что prepareStatement был вызван
            verify(mockConnection).prepareStatement(anyString());
        }
    }

    @Test
    void testDelete() throws SQLException {
        long libraryId = 1L;

        // Мокаем ConnectionManager.getConnection()
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeUpdate()).thenReturn(1); // Успешное удаление

            // Запускаем метод
            boolean result = libraryDao.delete(libraryId);

            // Проверяем результат
            assertTrue(result);

            // Проверяем, что все методы были вызваны ожидаемым образом
            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).setLong(1, libraryId);
            verify(mockStatement).executeUpdate();
        }
    }

    @Test
    void testDeleteFails() throws SQLException {
        Long libraryId = 1L;

        // Мокаем ConnectionManager.getConnection()
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Проверяем, что DaoException взрывается
            DaoException daoException = assertThrows(DaoException.class, () -> libraryDao.delete(libraryId));

            // Проверяем, что сообщение об ошибке соответствует ожиданиям
            assertEquals("Database error", daoException.getCause().getMessage());

            // Проверяем, что prepareStatement был вызван
            verify(mockConnection).prepareStatement(anyString());
        }
    }
}
