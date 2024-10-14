package by.polikarpov.dao;

import by.polikarpov.entity.Books;
import by.polikarpov.entity.Library;
import by.polikarpov.exception.DaoException;
import by.polikarpov.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BooksDao implements Dao<Long, Books> {

    private static final BooksDao INSTANCE = new BooksDao();

    private BooksDao() {
    }

    public static BooksDao getInstance() {
        return INSTANCE;
    }

    private static final String UPDATE_SQL = """
            UPDATE books
            SET title = ?,
                author = ?,
                library_id = ?
            WHERE id = ?
            """;

    @Override
    public boolean update(Books entity) {
        try (var connection = ConnectionManager.getConnection();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, entity.getTitle());
            preparedStatement.setString(2, entity.getAuthor());
            preparedStatement.setLong(3, entity.getLibrary().getId());
            preparedStatement.setLong(4, entity.getId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static final String FIND_ALL_SQL = """
            SELECT id, title, author, library_id
            FROM books
            ORDER BY id
            """;

    @Override
    public List<Books> findAll() {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<Books> books = new ArrayList<>();
            var result = statement.executeQuery();
            while (result.next()) {
                books.add(builderBook(result));
            }
            return books;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private Books builderBook(ResultSet result) throws SQLException {
        LibraryDao libraryDao = LibraryDao.getInstance();
        Library library;
        Books book;
        var libraryDB = libraryDao.findById(result.getLong("library_id"));
        if (libraryDB.isPresent()) {
            library = libraryDB.get();
             book = new Books(
                    result.getString("title"),
                    result.getString("author"),
                    library
            );
        } else {
            throw new SQLException("Library not found");
        }
        book.setId(result.getLong("id"));
        return book;
    }

    private static final String FIND_BY_ID_SQL = """
            SELECT id, title, author, library_id
            FROM books
            WHERE id = ?
            """;

    @Override
    public Optional<Books> findById(Long id) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            var result = statement.executeQuery();
            Optional<Books> book = Optional.empty();
            if (result.next()) {
                book = Optional.of(builderBook(result));
            }
            return book;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static final String SAVE_SQL = """
            INSERT INTO books (title, author, library_id)
            VALUES (?, ?, ?)
            """;

    @Override
    public Books save(Books entity) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getTitle());
            statement.setString(2, entity.getAuthor());
            statement.setLong(3, entity.getLibrary().getId());
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
            DELETE FROM books
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
