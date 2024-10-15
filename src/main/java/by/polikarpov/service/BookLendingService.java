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
import java.util.stream.Collectors;

public class BookLendingService implements Service<Long, BookLendingDto> {

    private static final BookLendingService INSTANCE = new BookLendingService();

    private BookLendingDao bookLendingDao;

    public static BookLendingService getInstance() {
        return INSTANCE;
    }

    private BookLendingService() { this.bookLendingDao = BookLendingDao.getInstance(); }

    public void setBookLendingDao(BookLendingDao bookLendingDao) {
        this.bookLendingDao = bookLendingDao;
    }

    @Override
    public List<BookLendingDto> getAll() {
        return bookLendingDao.findAll().stream()
                .map(bookLending -> new BookLendingDto(
                        createReaderDto(bookLending.getReader()),
                        createBookDto(bookLending.getBook()))
                ).collect(Collectors.toList());
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
        return Optional.empty();
    }

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

    public void delete(Long readerId, Long bookId) {
        if (bookLendingDao.findByReaderId(readerId).isEmpty()) {
            throw new IllegalArgumentException("Reader " + readerId + " does not exist");
        }
        if (bookLendingDao.findByBookId(bookId).isEmpty()) {
            throw new IllegalArgumentException("Book " + bookId + " does not exist");
        }
        bookLendingDao.delete(readerId, bookId);
    }

    public List<BooksDto> getByReaderId(Long id) {
        return bookLendingDao.findByReaderId(id).stream()
                .map(book -> new BooksDto(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getLibrary()
                ))
                .collect(Collectors.toList());
    }

    public List<ReadersDto> getByBookId(Long id) {
        return bookLendingDao.findByBookId(id).stream()
                .map(reader -> new ReadersDto(
                        reader.getId(),
                        reader.getReadersName()
                )).collect(Collectors.toList());
    }

    public List<BooksDto> getByNotReaderId(Long id) {
        return bookLendingDao.findByNotReaderId(id).stream()
                .map(book -> new BooksDto(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getLibrary()
                ))
                .collect(Collectors.toList());
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
