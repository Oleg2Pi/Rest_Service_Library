package by.polikarpov.dao;

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
class ReadersDaoTest {

    private ReadersDao readersDao;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() {
        readersDao = ReadersDao.getInstance();
    }

    @Test
    void testUpdate() {
        Readers readers = new Readers("Readers 1");
        readers.setId(1L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            // Мокаем статический метод
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeUpdate()).thenReturn(1);

            // Выполняем тест
            boolean result = readersDao.update(readers);

            // Проверяем результаты
            assertTrue(result);
            verify(mockStatement).setString(1, "Readers 1");
            verify(mockStatement).setLong(2, 1L);
            verify(mockStatement).executeUpdate();
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testUpdateFails () {
        Readers readers = new Readers("Readers 1");
        readers.setId(1L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeUpdate()).thenReturn(0);

            assertFalse(readersDao.update(readers));
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testUpdateThrowsException() {
        Readers readers = new Readers("Readers 1");
        readers.setId(1L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DataBase error"));

            assertThrows(DaoException.class, () -> readersDao.update(readers));
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testFindAll() {
        Readers expectedReaders1 = new Readers("Readers 1");
        expectedReaders1.setId(1L);
        Readers expectedReaders2 = new Readers("Readers 2");
        expectedReaders2.setId(2L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next())
                    .thenReturn(true)
                    .thenReturn(true)
                    .thenReturn(false);
            when(mockResultSet.getLong("id")).thenReturn(1L, 2L);
            when(mockResultSet.getString("readers_name")).thenReturn("Readers 1", "Readers 2");

            List<Readers> libraries = readersDao.findAll();

            assertNotNull(libraries);
            assertEquals(2, libraries.size());
            assertEquals(expectedReaders1.getId(), libraries.get(0).getId());
            assertEquals(expectedReaders1.getReadersName(), libraries.get(0).getReadersName());
            assertEquals(expectedReaders2.getId(), libraries.get(1).getId());
            assertEquals(expectedReaders2.getReadersName(), libraries.get(1).getReadersName());

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

            DaoException sqlException = assertThrows(DaoException.class, () -> readersDao.findAll());

            assertEquals("Database error", sqlException.getCause().getMessage());

            verify(mockConnection).prepareStatement(anyString());
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testFindById() {
        Readers expectedReaders = new Readers("Readers 1");
        expectedReaders.setId(1L);

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong("id")).thenReturn(1L);
            when(mockResultSet.getString("readers_name")).thenReturn("Readers 1");

            Optional<Readers> readers = readersDao.findById(expectedReaders.getId());

            assertTrue(readers.isPresent());
            assertEquals(expectedReaders.getId(), readers.get().getId());
            assertEquals(expectedReaders.getReadersName(), readers.get().getReadersName());

            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).setLong(1, readers.get().getId());
            verify(mockStatement).executeQuery();

        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void testFindByIdFails() throws SQLException {
        Long readersId = 1L;

        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            DaoException daoException = assertThrows(DaoException.class, () -> readersDao.findById(readersId));

            assertEquals("Database error", daoException.getCause().getMessage());

            verify(mockConnection).prepareStatement(anyString());
        }
    }

    @Test
    void testSave() throws SQLException {
        Readers readersToSave = new Readers("Readers 1");
        long generatedId = 1L;

        // Мокаем ConnectionManager.getConnection()
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getLong("id")).thenReturn(generatedId);

            // Запускаем метод
            Readers savedReaders = readersDao.save(readersToSave);

            // Проверяем результат
            assertNotNull(savedReaders);
            assertEquals(generatedId, savedReaders.getId());
            assertEquals(readersToSave.getReadersName(), savedReaders.getReadersName());

            // Проверяем, что все методы были вызваны ожидаемым образом
            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).setString(1, readersToSave.getReadersName());
            verify(mockStatement).executeQuery();
        }
    }

    @Test
    void testSaveFails() throws SQLException {
        Readers readersToSave = new Readers("Readers 1");

        // Мокаем ConnectionManager.getConnection()
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Проверяем, что DaoException взрывается
            DaoException daoException = assertThrows(DaoException.class, () -> readersDao.save(readersToSave));

            // Проверяем, что сообщение об ошибке соответствует ожиданиям
            assertEquals("Database error", daoException.getCause().getMessage());

            // Проверяем, что prepareStatement был вызван
            verify(mockConnection).prepareStatement(anyString());
        }
    }

    @Test
    void testDelete() throws SQLException {
        long readersId = 1L;

        // Мокаем ConnectionManager.getConnection()
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeUpdate()).thenReturn(1); // Успешное удаление

            // Запускаем метод
            boolean result = readersDao.delete(readersId);

            // Проверяем результат
            assertTrue(result);

            // Проверяем, что все методы были вызваны ожидаемым образом
            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).setLong(1, readersId);
            verify(mockStatement).executeUpdate();
        }
    }

    @Test
    void testDeleteFails() throws SQLException {
        Long readersId = 1L;

        // Мокаем ConnectionManager.getConnection()
        try (MockedStatic<ConnectionManager> connectionManagerMock = mockStatic(ConnectionManager.class)) {
            connectionManagerMock.when(ConnectionManager::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            // Проверяем, что DaoException взрывается
            DaoException daoException = assertThrows(DaoException.class, () -> readersDao.delete(readersId));

            // Проверяем, что сообщение об ошибке соответствует ожиданиям
            assertEquals("Database error", daoException.getCause().getMessage());

            // Проверяем, что prepareStatement был вызван
            verify(mockConnection).prepareStatement(anyString());
        }
    }
}
