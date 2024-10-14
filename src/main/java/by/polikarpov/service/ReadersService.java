package by.polikarpov.service;

import by.polikarpov.dao.ReadersDao;
import by.polikarpov.dto.ReadersDto;
import by.polikarpov.entity.Readers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReadersService implements Service<Long, ReadersDto> {

    private static final ReadersService INSTANCE = new ReadersService();

    private ReadersDao readersDao;

    public static ReadersService getInstance() {
        return INSTANCE;
    }

    private ReadersService() { this.readersDao = ReadersDao.getInstance();}

    public void setReadersDao(ReadersDao readersDao) {
        this.readersDao = readersDao;
    }

    @Override
    public List<ReadersDto> getAll() {
        return readersDao.findAll().stream()
                .map(reader -> new ReadersDto(reader.getId(), reader.getReadersName()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReadersDto> getById(Long id) {
        return readersDao.findById(id).map(
                reader -> new ReadersDto(reader.getId(), reader.getReadersName())
        );
    }

    @Override
    public void add(ReadersDto entity) {
        Readers reader = buildReaders(entity);
        readersDao.save(reader);
    }

    @Override
    public void update(ReadersDto entity) {
        if (entity.id() == null || readersDao.findById(entity.id()).isEmpty()) {
            throw new IllegalArgumentException("Readers " + entity.readerName() + " does not exist");
        }
        Readers reader = buildReaders(entity);
        reader.setId(entity.id());
        readersDao.update(reader);
    }

    @Override
    public void delete(Long id) {
        if (readersDao.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Readers " + id + " does not exist");
        }
        readersDao.delete(id);
    }

    private Readers buildReaders(ReadersDto entity) {
        Readers reader = new Readers();
        reader.setReadersName(entity.readerName());
        return reader;
    }
}
