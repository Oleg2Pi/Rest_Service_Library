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

/**
 * Data Access Object (DAO) class for managing book entities in the database.
 * This class provides methods for performing CRUD operations (Create, Read, Update, Delete)
 * and specific queries related to books.
 */
public class BooksDao implements Dao<Long, Books> {

    private static final BooksDao INSTANCE = new BooksDao();

    private BooksDao() {
    }

    /**
     * Returns a singleton instance of the BooksDao class.
     *
     * @return an instance of BooksDao
     */
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

    /**
     * Updates an existing book record in the database.
     *
     * @param entity the Books object containing updated fields
     * @return true if the update was successful; false otherwise
     * @throws DaoException if there is a data access error
     */
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

    /**
     * Retrieves all book records from the database.
     *
     * @return a list of Books objects
     * @throws DaoException if there is a data access error
     */
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

    /**
     * Builds a Books object from the result set.
     *
     * @param result the result set containing book data
     * @return a Books object
     * @throws SQLException if the library associated with the book cannot be found
     */
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

    /**
     * Finds a book by its ID.
     *
     * @param id the book's ID
     * @return an Optional containing the found Books object or empty if not found
     * @throws DaoException if there is a data access error
     */
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

    private static final String FIND_ALL_BY_LIBRARY_ID_SQL = """
            SELECT id, title, author, library_id
            FROM books
            WHERE library_id = ?
            """;

    /**
     * Finds all books in a specific library by its ID.
     *
     * @param libraryId the library's ID
     * @return a list of Books objects belonging to the specified library
     * @throws DaoException if there is a data access error
     */
    public List<Books> findAllByLibraryId(Long libraryId) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_ALL_BY_LIBRARY_ID_SQL)) {
            statement.setLong(1, libraryId);
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

    private static final String SAVE_SQL = """
            INSERT INTO books (title, author, library_id)
            VALUES (?, ?, ?)
            """;

    /**
     * Saves a new book record in the database.
     *
     * @param entity the Books object to be saved
     * @return the saved Books object with the generated ID
     * @throws DaoException if there is a data access error
     */
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

    /**
     * Deletes a book record from the database by its ID.
     *
     * @param id the book's ID
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
