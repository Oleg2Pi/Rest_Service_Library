package by.polikarpov.service;

import by.polikarpov.dao.BookLendingDao;
import by.polikarpov.dto.BookLendingDto;
import by.polikarpov.dto.BooksDto;
import by.polikarpov.dto.ReadersDto;
import by.polikarpov.entity.BookLending;

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
                .map(bookLending -> new BookLendingDto(bookLending.getReader(), bookLending.getBook()))
                .collect(Collectors.toList());
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
    }

    @Override
    public void delete(Long id) {
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

    private BookLending buildBookLending(BookLendingDto entity) {
        BookLending bookLending = new BookLending();
        bookLending.setReader(entity.readers());
        bookLending.setBook(entity.books());
        return bookLending;
    }
}
