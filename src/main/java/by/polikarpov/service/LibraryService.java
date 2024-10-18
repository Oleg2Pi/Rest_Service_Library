package by.polikarpov.service;

import by.polikarpov.dao.LibraryDao;
import by.polikarpov.dto.LibraryDto;
import by.polikarpov.entity.Library;

import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for managing library operations.
 * It acts as a layer between the presentation layer and the data access layer.
 */
public class LibraryService implements Service<Long, LibraryDto> {

    private static final LibraryService INSTANCE = new LibraryService();

    private LibraryDao libraryDao;

    /**
     * Returns the singleton instance of the LibraryService.
     *
     * @return the singleton instance of LibraryService
     */
    public static LibraryService getInstance() {
        return INSTANCE;
    }

    private LibraryService() {
        this.libraryDao = LibraryDao.getInstance();
    }

    /**
     * Sets the LibraryDao to be used by the service.
     *
     * @param libraryDao the LibraryDao to set
     */
    public void setLibraryDao(LibraryDao libraryDao) {
        this.libraryDao = libraryDao;
    }

    /**
     * Retrieves all libraries from the data access layer and transforms them into DTOs.
     *
     * @return a list of LibraryDto representing all the libraries
     */
    @Override
    public List<LibraryDto> getAll() {
        return libraryDao.findAll().stream()
                .map(library -> new LibraryDto(library.getId(), library.getLibraryName()))
                .toList();
    }

    /**
     * Retrieves a library by its ID.
     *
     * @param id the ID of the library to retrieve
     * @return an Optional containing the LibraryDto if found, otherwise an empty Optional
     */
    @Override
    public Optional<LibraryDto> getById(Long id) {
        return libraryDao.findById(id).map(
                library -> new LibraryDto(library.getId(), library.getLibraryName())
        );
    }

    /**
     * Adds a new library to the database.
     *
     * @param entity the LibraryDto representing the library to add
     */
    @Override
    public void add(LibraryDto entity) {
        Library library = buildLibrary(entity);
        libraryDao.save(library);
    }

    /**
     * Updates an existing library in the database.
     *
     * @param entity the LibraryDto representing the library to update
     * @throws IllegalArgumentException if the library does not exist
     */
    @Override
    public void update(LibraryDto entity) {
        if (entity.id() == null || libraryDao.findById(entity.id()).isEmpty()) {
            throw new IllegalArgumentException("Library " + entity.libraryName() + " does not exist");
        }
        Library library = buildLibrary(entity);
        library.setId(entity.id());
        libraryDao.update(library);
    }

    /**
     * Deletes a library from the database by its ID.
     *
     * @param id the ID of the library to delete
     * @throws IllegalArgumentException if the library does not exist
     */
    @Override
    public void delete(Long id) {
        if (libraryDao.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Library " + id + " does not exist");
        }
        libraryDao.delete(id);
    }

    /**
     * Builds a Library entity from a LibraryDto.
     *
     * @param entity the LibraryDto to convert into an entity
     * @return the constructed Library entity
     */
    private Library buildLibrary(LibraryDto entity) {
        Library library = new Library();
        library.setLibraryName(entity.libraryName());
        return library;
    }
}
