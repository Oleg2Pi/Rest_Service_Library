package by.polikarpov.service;

import by.polikarpov.dao.BooksDao;
import by.polikarpov.dto.BooksDto;
import by.polikarpov.entity.Books;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class responsible for operations related to books.
 * It acts as a layer between the presentation layer and the data access layer.
 */
public class BooksService implements Service<Long, BooksDto> {

    private static final BooksService INSTANCE = new BooksService();

    private BooksDao booksDao;

    /**
     * Returns the singleton instance of the BooksService.
     *
     * @return the singleton instance of the BooksService
     */
    public static BooksService getInstance() {
        return INSTANCE;
    }

    private BooksService() {
        this.booksDao = BooksDao.getInstance();
    }

    /**
     * Sets the BooksDao to be used by the service.
     *
     * @param booksDao the BooksDao to set
     */
    public void setBooksDao(BooksDao booksDao) {
        this.booksDao = booksDao;
    }

    /**
     * Retrieves all books from the data access layer and transforms them into DTOs.
     *
     * @return a list of BooksDto representing all the books
     */
    @Override
    public List<BooksDto> getAll() {
        return booksDao.findAll().stream()
                .map(book -> new BooksDto(book.getId(), book.getTitle(), book.getAuthor(), book.getLibrary()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a book by its ID.
     *
     * @param id the ID of the book to retrieve
     * @return an Optional containing the BooksDto if found, otherwise an empty Optional
     */
    @Override
    public Optional<BooksDto> getById(Long id) {
        return booksDao.findById(id).map(
                book -> new BooksDto(book.getId(), book.getTitle(), book.getAuthor(), book.getLibrary())
        );
    }

    /**
     * Retrieves all books belonging to a specific library.
     *
     * @param id the ID of the library
     * @return a list of BooksDto representing all books in the library
     */
    public List<BooksDto> getAllByLibraryId(Long id) {
        return booksDao.findAllByLibraryId(id).stream()
                .map(book -> new BooksDto(book.getId(), book.getTitle(), book.getAuthor(), book.getLibrary()))
                .collect(Collectors.toList());
    }

    /**
     * Adds a new book to the database.
     *
     * @param entity the BooksDto representing the book to add
     */
    @Override
    public void add(BooksDto entity) {
        Books book = buildBook(entity);
        booksDao.save(book);
    }

    /**
     * Updates an existing book in the database.
     *
     * @param entity the BooksDto representing the book to update
     * @throws IllegalArgumentException if the book does not exist
     */
    @Override
    public void update(BooksDto entity) {
        if (entity.id() == null || booksDao.findById(entity.id()).isEmpty()) {
            throw new IllegalArgumentException("Books " + entity.title() + " does not exist");
        }
        Books book = buildBook(entity);
        book.setId(entity.id());
        booksDao.update(book);
    }

    /**
     * Deletes a book from the database by its ID.
     *
     * @param id the ID of the book to delete
     * @throws IllegalArgumentException if the book does not exist
     */
    @Override
    public void delete(Long id) {
        if (booksDao.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Books " + id + " does not exist");
        }
        booksDao.delete(id);
    }

    /**
     * Builds a Books entity from a BooksDto.
     *
     * @param entity the BooksDto to convert into an entity
     * @return the constructed Books entity
     */
    private Books buildBook(BooksDto entity) {
        Books book = new Books();
        book.setTitle(entity.title());
        book.setAuthor(entity.author());
        book.setLibrary(entity.library());
        return book;
    }
}
