package by.polikarpov.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import by.polikarpov.dao.ReadersDao;
import by.polikarpov.dto.ReadersDto;
import by.polikarpov.entity.Readers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ReadersServiceTest {

    private ReadersService readersService;

    @Mock
    private ReadersDao readersDaoMock;

    @BeforeEach
    void setUp() {
        readersService = ReadersService.getInstance();
        readersService.setReadersDao(readersDaoMock);
    }

    @Test
    void getAll() {
        // Подготовка данных
        Readers reader1 = new Readers("Reader One");
        reader1.setId(1L);

        Readers reader2 = new Readers("Reader Two");
        reader2.setId(2L);

        when(readersDaoMock.findAll()).thenReturn(Arrays.asList(reader1, reader2));

        // Вызов метода
        List<ReadersDto> result = readersService.getAll();

        // Проверка результата
        assertEquals(2, result.size());
        assertEquals("Reader One", result.get(0).readerName());
        assertEquals("Reader Two", result.get(1).readerName());

        verify(readersDaoMock).findAll();
    }

    @Test
    void getByIdWhenExists() {
        // Подготовка данных
        Readers reader = new Readers("Reader One");
        reader.setId(1L);

        when(readersDaoMock.findById(1L)).thenReturn(Optional.of(reader));

        // Вызов метода
        Optional<ReadersDto> result = readersService.getById(1L);

        // Проверка результата
        assertTrue(result.isPresent());
        assertEquals("Reader One", result.get().readerName());

        verify(readersDaoMock).findById(1L);
    }

    @Test
    void getByIdWhenReaderDoesNotExist() {
        when(readersDaoMock.findById(1L)).thenReturn(Optional.empty());

        // Вызов метода
        Optional<ReadersDto> result = readersService.getById(1L);

        // Проверка результата
        assertFalse(result.isPresent());
        verify(readersDaoMock).findById(1L);
    }

    @Test
    void addReader() {
        // Подготовка данных
        ReadersDto newReader = new ReadersDto(null, "New Reader");
        Readers reader = new Readers("New Reader");

        when(readersDaoMock.save(reader)).thenReturn(reader);

        // Вызов метода
        readersService.add(newReader);

        // Проверка, что readerDao.save был вызван
        verify(readersDaoMock).save(any(Readers.class));
    }

    @Test
    void updateWhenExists() {
        // Подготовка данных
        ReadersDto existingReader = new ReadersDto(1L, "Updated Reader");
        Readers reader = new Readers("Updated Reader");
        reader.setId(1L);

        when(readersDaoMock.findById(existingReader.id())).thenReturn(Optional.of(reader));
        when(readersDaoMock.update(reader)).thenReturn(true);

        // Вызов метода
        readersService.update(existingReader);

        // Проверка, что readerDao.update был вызван
        verify(readersDaoMock).update(any(Readers.class));
        verify(readersDaoMock).findById(1L);
    }

    @Test
    void updateWhenReaderDoesNotExist() {
        ReadersDto nonExistingReader = new ReadersDto(1L, "Non-existing Reader");
        when(readersDaoMock.findById(nonExistingReader.id())).thenReturn(Optional.empty());

        // Проверка вызова IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            readersService.update(nonExistingReader);
        });

        assertEquals("Readers Non-existing Reader does not exist", exception.getMessage());
    }

    @Test
    void deleteWhenExists() {
        Long readerId = 1L;
        Readers reader = new Readers("Reader One");
        reader.setId(readerId);

        when(readersDaoMock.findById(readerId)).thenReturn(Optional.of(reader));
        when(readersDaoMock.delete(readerId)).thenReturn(true);

        // Вызов метода
        readersService.delete(readerId);

        // Проверка, что readerDao.delete был вызван
        verify(readersDaoMock).delete(readerId);
    }

    @Test
    void deleteWhenReaderDoesNotExist() {
        Long readerId = 1L;
        when(readersDaoMock.findById(readerId)).thenReturn(Optional.empty());

        // Проверка вызова IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            readersService.delete(readerId);
        });

        assertEquals("Readers " + readerId + " does not exist", exception.getMessage());
    }
}