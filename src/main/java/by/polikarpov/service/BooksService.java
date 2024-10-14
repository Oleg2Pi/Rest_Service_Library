package by.polikarpov.service;

import by.polikarpov.dao.BooksDao;
import by.polikarpov.dto.BooksDto;
import by.polikarpov.entity.Books;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BooksService implements Service<Long, BooksDto>{

    private static final BooksService INSTANCE = new BooksService();

    private BooksDao booksDao;

    public static BooksService getInstance() {
        return INSTANCE;
    }

    private BooksService() { this.booksDao = BooksDao.getInstance(); }

    public void setBooksDao(BooksDao booksDao) {
        this.booksDao = booksDao;
    }

    @Override
    public List<BooksDto> getAll() {
        return booksDao.findAll().stream()
                .map(book -> new BooksDto(book.getId(), book.getTitle(), book.getAuthor(), book.getLibrary()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BooksDto> getById(Long id) {
        return booksDao.findById(id).map(
                book -> new BooksDto(book.getId(), book.getTitle(), book.getAuthor(), book.getLibrary())
        );
    }

    @Override
    public void add(BooksDto entity) {
        Books book = buildBook(entity);
        booksDao.save(book);
    }

    @Override
    public void update(BooksDto entity) {
        if (entity.id() == null || booksDao.findById(entity.id()).isEmpty()) {
            throw new IllegalArgumentException("Books " + entity.title() + " does not exist");
        }
        Books book = buildBook(entity);
        book.setId(entity.id());
        booksDao.update(book);
    }

    @Override
    public void delete(Long id) {
        if (booksDao.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Books " + id + " does not exist");
        }
        booksDao.delete(id);
    }

    private Books buildBook(BooksDto entity) {
        Books book = new Books();
        book.setTitle(entity.title());
        book.setAuthor(entity.author());
        book.setLibrary(entity.library());
        return book;
    }
}
