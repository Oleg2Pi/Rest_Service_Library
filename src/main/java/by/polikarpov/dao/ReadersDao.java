package by.polikarpov.dao;

import by.polikarpov.entity.Readers;
import by.polikarpov.exception.DaoException;
import by.polikarpov.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) class for managing reader entities in the database.
 * This class implements CRUD operations (Create, Read, Update, Delete)
 * for reader records.
 */
public class ReadersDao implements Dao<Long, Readers> {

    private static final ReadersDao INSTANCE = new ReadersDao();

    private ReadersDao() {
    }

    /**
     * Returns a singleton instance of the ReadersDao class.
     *
     * @return an instance of ReadersDao
     */
    public static ReadersDao getInstance() {
        return INSTANCE;
    }

    private static final String UPDATE_SQL = """
            UPDATE readers
            SET readers_name = ?
            WHERE id = ?
            """;

    /**
     * Updates an existing reader record in the database.
     *
     * @param entity the Readers object containing updated fields
     * @return true if the update was successful; false otherwise
     * @throws DaoException if there is a data access error
     */
    @Override
    public boolean update(Readers entity) {
        try (var connection = ConnectionManager.getConnection();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, entity.getReadersName());
            preparedStatement.setLong(2, entity.getId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static final String FIND_ALL_SQL = """
            SELECT id, readers_name
            FROM readers
            ORDER BY id
            """;

    /**
     * Retrieves all reader records from the database.
     *
     * @return a list of Readers objects
     * @throws DaoException if there is a data access error
     */
    @Override
    public List<Readers> findAll() {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<Readers> readers = new ArrayList<>();
            var result = statement.executeQuery();
            while (result.next()) {
                readers.add(builderReaders(result));
            }
            return readers;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    /**
     * Builds a Readers object from the result set.
     *
     * @param result the result set containing reader data
     * @return a Readers object
     * @throws SQLException if there is an error accessing the result set
     */
    private Readers builderReaders(ResultSet result) throws SQLException {
        Readers readers = new Readers(
                result.getString("readers_name")
        );
        readers.setId(result.getLong("id"));
        return readers;
    }

    private static final String FIND_BY_ID_SQL = """
            SELECT id, readers_name
            FROM readers
            WHERE id = ?
            """;

    /**
     * Finds a reader by its ID.
     *
     * @param id the reader's ID
     * @return an Optional containing the found Readers object or empty if not found
     * @throws DaoException if there is a data access error
     */
    @Override
    public Optional<Readers> findById(Long id) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            var result = statement.executeQuery();
            Optional<Readers> readers = Optional.empty();
            if (result.next()) {
                readers = Optional.of(builderReaders(result));
            }
            return readers;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static final String SAVE_SQL = """
            INSERT INTO readers (readers_name)
            VALUES (?)
            """;

    /**
     * Saves a new reader record in the database.
     *
     * @param entity the Readers object to be saved
     * @return the saved Readers object with the generated ID
     * @throws DaoException if there is a data access error
     */
    @Override
    public Readers save(Readers entity) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getReadersName());
            statement.executeUpdate();

            var keys = statement.getGeneratedKeys();
            if (keys.next()) {
                entity.setId(keys.getLong("id"));
            }
            return entity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static final String DELETE_SQL = """
            DELETE FROM readers
            WHERE id = ?
            """;

    /**
     * Deletes a reader record from the database by its ID.
     *
     * @param id the reader's ID
     * @return true if the deletion was successful; false otherwise
     * @throws DaoException if there is a data access error
     */
    @Override
    public boolean delete(Long id) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Long readerId, Long bookId) {
        // Method not implemented
        return false;
    }
}
