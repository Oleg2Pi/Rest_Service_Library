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

public class LibraryDao implements Dao<Long, Library> {

    private static final LibraryDao INSTANCE = new LibraryDao();

    private LibraryDao() {
    }

    public static LibraryDao getInstance() {
        return INSTANCE;
    }

    private static final String UPDATE_SQL = """
            UPDATE library
            SET library_name = ?
            WHERE id = ?
            """;

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
        return false;
    }
}
