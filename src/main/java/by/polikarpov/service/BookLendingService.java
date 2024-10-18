package by.polikarpov.service;

import by.polikarpov.dao.BookLendingDao;
import by.polikarpov.dto.BookLendingDto;
import by.polikarpov.dto.BooksDto;
import by.polikarpov.dto.ReadersDto;
import by.polikarpov.entity.BookLending;
import by.polikarpov.entity.Books;
import by.polikarpov.entity.Readers;

import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for managing book lending operations.
 * It coordinates data access and transformations between the DAO layer and DTOs.
 */
public class BookLendingService implements Service<Long, BookLendingDto> {

    private static final BookLendingService INSTANCE = new BookLendingService();

    private BookLendingDao bookLendingDao;

    /**
     * Returns the singleton instance of the BookLendingService.
     *
     * @return the singleton instance of the BookLendingService
     */
    public static BookLendingService getInstance() {
        return INSTANCE;
    }

    private BookLendingService() {
        this.bookLendingDao = BookLendingDao.getInstance();
    }

    /**
     * Sets the BookLendingDao to be used by the service.
     *
     * @param bookLendingDao the BookLendingDao to set
     */
    public void setBookLendingDao(BookLendingDao bookLendingDao) {
        this.bookLendingDao = bookLendingDao;
    }

    /**
     * Retrieves all book lending records from the DAO and transforms them into DTOs.
     *
     * @return a list of BookLending DTOs
     */
    @Override
    public List<BookLendingDto> getAll() {
        return bookLendingDao.findAll().stream()
                .map(bookLending -> new BookLendingDto(
                        createReaderDto(bookLending.getReader()),
                        createBookDto(bookLending.getBook()))
                ).toList();
    }

    private BooksDto createBookDto(Books book) {
        return new BooksDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getLibrary()
        );
    }

    private ReadersDto createReaderDto(Readers reader) {
        return new ReadersDto(
                reader.getId(),
                reader.getReadersName()
        );
    }


    @Override
    public Optional<BookLendingDto> getById(Long id) {
        // Method not implemented
        return Optional.empty();
    }

    /**
     * Adds a new book lending record.
     *
     * @param entity the BookLendingDto to add
     */
    @Override
    public void add(BookLendingDto entity) {
        BookLending bookLending = buildBookLending(entity);
        bookLendingDao.save(bookLending);
    }


    @Override
    public void update(BookLendingDto entity) {
        // don't use because can't update BookLending
    }

    @Override
    public void delete(Long id) {
        // don't use because can't delete row in BookLending
    }

    /**
     * Deletes a book lending record by the reader ID and book ID.
     *
     * @param readerId the ID of the reader
     * @param bookId   the ID of the book
     * @throws IllegalArgumentException if the reader or book does not exist
     */
    public void delete(Long readerId, Long bookId) {
        if (bookLendingDao.findByReaderId(readerId).isEmpty()) {
            throw new IllegalArgumentException("Reader " + readerId + " does not exist");
        }
        if (bookLendingDao.findByBookId(bookId).isEmpty()) {
            throw new IllegalArgumentException("Book " + bookId + " does not exist");
        }
        bookLendingDao.delete(readerId, bookId);
    }

    /**
     * Retrieves all books lent by a specific reader.
     *
     * @param id the ID of the reader
     * @return a list of BooksDto associated with the reader
     */
    public List<BooksDto> getByReaderId(Long id) {
        return bookLendingDao.findByReaderId(id).stream()
                .map(book -> new BooksDto(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getLibrary()
                ))
                .toList();
    }

    /**
     * Retrieves all readers who have borrowed a specific book.
     *
     * @param id the ID of the book
     * @return a list of ReadersDto associated with the book
     */
    public List<ReadersDto> getByBookId(Long id) {
        return bookLendingDao.findByBookId(id).stream()
                .map(reader -> new ReadersDto(
                        reader.getId(),
                        reader.getReadersName()
                )).toList();
    }

    /**
     * Retrieves all books that are not currently lent to a specific reader.
     *
     * @param id the ID of the reader
     * @return a list of BooksDto that are not lent to the reader
     */
    public List<BooksDto> getByNotReaderId(Long id) {
        return bookLendingDao.findByNotReaderId(id).stream()
                .map(book -> new BooksDto(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getLibrary()
                ))
                .toList();
    }

    private BookLending buildBookLending(BookLendingDto entity) {
        BookLending bookLending = new BookLending();
        bookLending.setReader(createReader(entity.readers()));
        bookLending.setBook(createBook(entity.books()));
        return bookLending;
    }

    private Books createBook(BooksDto entity) {
        Books book = new Books(entity.title(), entity.author(), entity.library());
        book.setId(entity.id());
        return book;
    }

    private Readers createReader(ReadersDto entity) {
        Readers reader = new Readers(entity.readerName());
        reader.setId(entity.id());
        return reader;
    }
}
