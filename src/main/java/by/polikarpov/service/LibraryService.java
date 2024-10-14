package by.polikarpov.service;

import by.polikarpov.dao.LibraryDao;
import by.polikarpov.dto.LibraryDto;
import by.polikarpov.entity.Library;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LibraryService implements Service<Long, LibraryDto> {

    private static final LibraryService INSTANCE = new LibraryService();

    private LibraryDao libraryDao;

    public static LibraryService getInstance() {
        return INSTANCE;
    }

    private LibraryService() {
        this.libraryDao = LibraryDao.getInstance();
    }

    public void setLibraryDao(LibraryDao libraryDao) {
        this.libraryDao = libraryDao;
    }

    @Override
    public List<LibraryDto> getAll() {
        return libraryDao.findAll().stream()
                .map(library -> new LibraryDto(library.getId(), library.getLibraryName()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<LibraryDto> getById(Long id) {
        return libraryDao.findById(id).map(
                library -> new LibraryDto(library.getId(), library.getLibraryName())
        );
    }

    @Override
    public void add(LibraryDto entity) {
        Library library = buildLibrary(entity);
        libraryDao.save(library);
    }

    @Override
    public void update(LibraryDto entity) {
        if (entity.id() == null || libraryDao.findById(entity.id()).isEmpty()) {
            throw new IllegalArgumentException("Library " + entity.libraryName() + " does not exist");
        }
        Library library = buildLibrary(entity);
        library.setId(entity.id());
        libraryDao.update(library);
    }

    @Override
    public void delete(Long id) {
        if (libraryDao.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Library " + id + " does not exist");
        }
        libraryDao.delete(id);
    }

    private Library buildLibrary(LibraryDto entity) {
        Library library = new Library();
        library.setLibraryName(entity.libraryName());
        return library;
    }
}
