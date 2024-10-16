package by.polikarpov.service;

import by.polikarpov.dao.ReadersDao;
import by.polikarpov.dto.ReadersDto;
import by.polikarpov.entity.Readers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing reader operations.
 * It acts as a layer between the presentation layer and the data access layer.
 */
public class ReadersService implements Service<Long, ReadersDto> {

    private static final ReadersService INSTANCE = new ReadersService();

    private ReadersDao readersDao;

    /**
     * Returns the singleton instance of the ReadersService.
     *
     * @return the singleton instance of ReadersService
     */
    public static ReadersService getInstance() {
        return INSTANCE;
    }

    private ReadersService() {
        this.readersDao = ReadersDao.getInstance();
    }

    /**
     * Sets the ReadersDao to be used by the service.
     *
     * @param readersDao the ReadersDao to set
     */
    public void setReadersDao(ReadersDao readersDao) {
        this.readersDao = readersDao;
    }

    /**
     * Retrieves all readers from the data access layer and transforms them into DTOs.
     *
     * @return a list of ReadersDto representing all the readers
     */
    @Override
    public List<ReadersDto> getAll() {
        return readersDao.findAll().stream()
                .map(reader -> new ReadersDto(reader.getId(), reader.getReadersName()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a reader by its ID.
     *
     * @param id the ID of the reader to retrieve
     * @return an Optional containing the ReadersDto if found, otherwise an empty Optional
     */
    @Override
    public Optional<ReadersDto> getById(Long id) {
        return readersDao.findById(id).map(
                reader -> new ReadersDto(reader.getId(), reader.getReadersName())
        );
    }

    /**
     * Adds a new reader to the database.
     *
     * @param entity the ReadersDto representing the reader to add
     */
    @Override
    public void add(ReadersDto entity) {
        Readers reader = buildReaders(entity);
        readersDao.save(reader);
    }

    /**
     * Updates an existing reader in the database.
     *
     * @param entity the ReadersDto representing the reader to update
     * @throws IllegalArgumentException if the reader does not exist
     */
    @Override
    public void update(ReadersDto entity) {
        if (entity.id() == null || readersDao.findById(entity.id()).isEmpty()) {
            throw new IllegalArgumentException("Readers " + entity.readerName() + " does not exist");
        }
        Readers reader = buildReaders(entity);
        reader.setId(entity.id());
        readersDao.update(reader);
    }

    /**
     * Deletes a reader from the database by its ID.
     *
     * @param id the ID of the reader to delete
     * @throws IllegalArgumentException if the reader does not exist
     */
    @Override
    public void delete(Long id) {
        if (readersDao.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Readers " + id + " does not exist");
        }
        readersDao.delete(id);
    }

    /**
     * Builds a Readers entity from a ReadersDto.
     *
     * @param entity the ReadersDto to convert into an entity
     * @return the constructed Readers entity
     */
    private Readers buildReaders(ReadersDto entity) {
        Readers reader = new Readers();
        reader.setReadersName(entity.readerName());
        return reader;
    }
}
