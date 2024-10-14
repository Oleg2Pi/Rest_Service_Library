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

public class ReadersDao implements Dao<Long, Readers> {

    private static final ReadersDao INSTANCE = new ReadersDao();

    private ReadersDao() {
    }

    public static ReadersDao getInstance() {
        return INSTANCE;
    }

    private static final String UPDATE_SQL = """
            UPDATE readers
            SET readers_name = ?
            WHERE id = ?
            """;

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
