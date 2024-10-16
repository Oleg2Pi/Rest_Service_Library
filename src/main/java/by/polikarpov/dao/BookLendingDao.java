package by.polikarpov.dao;

import by.polikarpov.entity.BookLending;
import by.polikarpov.entity.Books;
import by.polikarpov.entity.Readers;
import by.polikarpov.exception.DaoException;
import by.polikarpov.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) for managing information about book lendings
 * between readers and books. Provides methods for performing CRUD operations
 * (Create, Read, Update, Delete) and specific queries related to book lendings.
 */
public class BookLendingDao implements Dao<Long, BookLending> {

    private static final BookLendingDao INSTANCE = new BookLendingDao();

    private BookLendingDao() {
    }


    public static BookLendingDao getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean update(BookLending entity) {
        // Implementation will be not defined
        return false;
    }

    private static final String FIND_ALL_SQL = """
            SELECT reader_id, book_id
            FROM book_lending
            """;

    /**
     * Finds and returns a list of all book lending records.
     *
     * @return a list of BookLending objects
     * @throws DaoException if there is a data access error
     */
    @Override
    public List<BookLending> findAll() {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<BookLending> bookLending = new ArrayList<>();
            var result = statement.executeQuery();
            while (result.next()) {
                bookLending.add(builderBookLending(result));
            }
            return bookLending;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<BookLending> findById(Long id) {
        // Implementation will be not defined
        return Optional.empty();
    }

    /**
     * Builds a BookLending object from the result set.
     *
     * @param result the result set
     * @return a BookLending object
     * @throws SQLException if the reader or book cannot be found
     */
    private BookLending builderBookLending(ResultSet result) throws SQLException {
        Optional<Readers> optionalReaders = ReadersDao.getInstance().findById(result.getLong("reader_id"));
        if (optionalReaders.isEmpty()) {
            throw new SQLException("Not found reader");
        }
        Readers reader = optionalReaders.get();

        Optional<Books> optionalBook = BooksDao.getInstance().findById(result.getLong("book_id"));
        if (optionalBook.isEmpty()) {
            throw new SQLException("Not found book");
        }
        Books book = optionalBook.get();

        return new BookLending(
                reader,
                book
        );
    }

    private static final String FIND_BY_READER_ID_SQL = """
            SELECT book_id
            FROM book_lending
            WHERE reader_id = ?
            """;

    /**
     * Finds all books lent to a specific reader by their ID.
     *
     * @param id the reader's ID
     * @return a list of Books
     * @throws DaoException if there is a data access error
     */
    public List<Books> findByReaderId(Long id) {
        BooksDao booksDao = BooksDao.getInstance();
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_BY_READER_ID_SQL)) {
            statement.setLong(1, id);
            var result = statement.executeQuery();
            List<Books> books = new ArrayList<>();
            while (result.next()) {
                Optional<Books> optional = booksDao.findById(result.getLong("book_id"));
                if (optional.isEmpty()) {
                    throw new SQLException("Not found book");
                }
                books.add(optional.get());
            }
            return books;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static final String FIND_BY_NOT_READER_ID_SQL = """
            SELECT id
            FROM books
            WHERE id NOT IN (
                SELECT book_id
                FROM book_lending
                WHERE reader_id = ?
            );
            """;

    /**
     * Finds all books that have not been lent to a specific reader.
     *
     * @param id the reader's ID
     * @return a list of Books
     * @throws DaoException if there is a data access error
     */
    public List<Books> findByNotReaderId(Long id) {
        BooksDao booksDao = BooksDao.getInstance();
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_BY_NOT_READER_ID_SQL)) {
            statement.setLong(1, id);
            var result = statement.executeQuery();
            List<Books> books = new ArrayList<>();
            while (result.next()) {
                Optional<Books> optional = booksDao.findById(result.getLong("id"));
                if (optional.isEmpty()) {
                    throw new SQLException("Not found book");
                }
                books.add(optional.get());
            }
            return books;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static final String FIND_BY_BOOK_ID_SQL = """
            SELECT reader_id
            FROM book_lending
            WHERE book_id = ?
            """;

    /**
     * Finds all readers who borrowed a specific book by its ID.
     *
     * @param id the book's ID
     * @return a list of Readers
     * @throws DaoException if there is a data access error
     */
    public List<Readers> findByBookId(Long id) {
        ReadersDao readersDao = ReadersDao.getInstance();
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(FIND_BY_BOOK_ID_SQL)) {
            statement.setLong(1, id);
            var result = statement.executeQuery();
            List<Readers> readers = new ArrayList<>();
            while (result.next()) {
                Optional<Readers> optional = readersDao.findById(result.getLong("reader_id"));
                if (optional.isEmpty()) {
                    throw new SQLException("Not found book");
                }
                readers.add(optional.get());
            }
            return readers;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static final String SAVE_SQL = """
            INSERT INTO book_lending (reader_id, book_id)
            VALUES (?, ?)
            """;

    /**
     * Saves a book lending record for a reader in the database.
     *
     * @param entity the BookLending object to be saved
     * @return the saved BookLending object
     * @throws DaoException if there is a data access error
     */
    public BookLending save(BookLending entity) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(SAVE_SQL)) {
            statement.setLong(1, entity.getReader().getId());
            statement.setLong(2, entity.getBook().getId());
            statement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    private static final String DELETE_FROM_READER_SQL = """
            DELETE FROM book_lending
            WHERE reader_id = ?
            AND book_id = ?
            """;

    /**
     * Deletes a book lending record for a specific reader and book by their IDs.
     *
     * @param readerId the reader's ID
     * @param bookId   the book's ID
     * @return true if the record was successfully deleted; false otherwise
     * @throws DaoException if there is a data access error
     */
    @Override
    public boolean delete(Long readerId, Long bookId) {
        try (var connection = ConnectionManager.getConnection();
             var statement = connection.prepareStatement(DELETE_FROM_READER_SQL)) {
            statement.setLong(1, readerId);
            statement.setLong(2, bookId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
