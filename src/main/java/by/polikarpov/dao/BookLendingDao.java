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

public class BookLendingDao implements Dao<Long, BookLending> {

    private static final BookLendingDao INSTANCE = new BookLendingDao();

    private BookLendingDao() {
    }

    public static BookLendingDao getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean update(BookLending entity) {
        return false;
    }

    private static final String FIND_ALL_SQL = """
            SELECT reader_id, book_id
            FROM book_lending
            """;

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
        return Optional.empty();
    }

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
