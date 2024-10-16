package by.polikarpov.dao;

import by.polikarpov.entity.Library;
import by.polikarpov.exception.DaoException;
import by.polikarpov.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) class for managing library entities in the database.
 * This class provides methods for performing CRUD operations (Create, Read, Update, Delete)
 * and specific queries related to libraries.
 */
public class LibraryDao implements Dao<Long, Library> {

    private static final LibraryDao INSTANCE = new LibraryDao();

    private LibraryDao() {
    }

    /**
     * Returns a singleton instance of the LibraryDao class.
     *
     * @return an instance of LibraryDao
     */
    public static LibraryDao getInstance() {
        return INSTANCE;
    }

    private static final String UPDATE_SQL = """
            UPDATE library
            SET library_name = ?
            WHERE id = ?
            """;

    /**
     * Updates an existing library record in the database.
     *
     * @param entity the Library object containing updated fields
     * @return true if the update was successful; false otherwise
     * @throws DaoException if there is a data access error
     */
    @Override
    public boolean update(Library entity) {
        try (var connection = ConnectionManager.getConnection();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, entity.getLibraryName());
            preparedStatement.setLong(2, entity.getId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static final String FIND_ALL_SQL = """
            SELECT id, library_name
            FROM library
            ORDER BY id
            """;

    /**
     * Retrieves all library records from the database.
     *
     * @return a list of Library objects
     * @throws DaoException if there is a data access error
     */
    @Override
    public List<Library> findAll() {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<Library> libraries = new ArrayList<>();
            var result = statement.executeQuery();
            while (result.next()) {
                libraries.add(builderLibrary(result));
            }
            return libraries;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    /**
     * Builds a Library object from the result set.
     *
     * @param result the result set containing library data
     * @return a Library object
     * @throws SQLException if there is an error accessing the result set
     */
    private Library builderLibrary(ResultSet result) throws SQLException {
        Library library = new Library(
                result.getString("library_name")
        );
        library.setId(result.getLong("id"));
        return library;
    }

    private static final String FIND_BY_ID_SQL = """
            SELECT id, library_name
            FROM library
            WHERE id = ?
            """;

    /**
     * Finds a library by its ID.
     *
     * @param id the library's ID
     * @return an Optional containing the found Library object or empty if not found
     * @throws DaoException if there is a data access error
     */
    @Override
    public Optional<Library> findById(Long id) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            var result = statement.executeQuery();
            Optional<Library> library = Optional.empty();
            if (result.next()) {
                library = Optional.of(builderLibrary(result));
            }
            return library;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static final String SAVE_SQL = """
            INSERT INTO library (library_name)
            VALUES (?)
            """;

    /**
     * Saves a new library record in the database.
     *
     * @param entity the Library object to be saved
     * @return the saved Library object with the generated ID
     * @throws DaoException if there is a data access error
     */
    @Override
    public Library save(Library entity) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getLibraryName());
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
            DELETE FROM library
            WHERE id = ?
            """;

    /**
     * Deletes a library record from the database by its ID.
     *
     * @param id the library's ID
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
