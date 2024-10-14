package by.polikarpov.service;

import by.polikarpov.dao.LibraryDao;
import by.polikarpov.dto.LibraryDto;
import by.polikarpov.entity.Library;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    private LibraryService libraryService;

    @Mock
    private LibraryDao libraryDaoMock;

    @BeforeEach
    void setUp() {
        libraryService = LibraryService.getInstance();
        libraryService.setLibraryDao(libraryDaoMock);
    }

    @Test
    void getAllLibrary() {
        Library library1 = new Library("Main Library");
        library1.setId(1L);
        Library library2 = new Library("Main Library");
        library2.setId(1L);
        List<Library> libraries = new ArrayList<>();
        libraries.add(library1);
        libraries.add(library2);

        when(libraryDaoMock.findAll()).thenReturn(libraries);

        var result = libraryService.getAll();

        verify(libraryDaoMock).findAll();
        assertEquals(2, result.size());
    }

    @Test
    void getByIdReturnsLibraryWhenExists() {
        // Подготовка данных
        Long libraryId = 1L;
        Library library = new Library("Main Library");
        library.setId(libraryId);

        // Настройка мока
        when(libraryDaoMock.findById(libraryId)).thenReturn(Optional.of(library));

        // Вызов метода
        Optional<LibraryDto> result = libraryService.getById(libraryId);

        // Проверка результатов
        assertTrue(result.isPresent(), "Expected to find a library by ID.");
        assertEquals(libraryId, result.get().id(), "Library ID should match.");
        assertEquals("Main Library", result.get().libraryName(), "Library names should match.");

        // Проверка вызова метода findById
        verify(libraryDaoMock).findById(libraryId);
    }

    @Test
    void getByIdReturnsEmptyWhenNotExists() {
        // Подготовка данных
        Long libraryId = 2L;

        // Настройка мока
        when(libraryDaoMock.findById(libraryId)).thenReturn(Optional.empty());

        // Вызов метода
        Optional<LibraryDto> result = libraryService.getById(libraryId);

        // Проверка результатов
        assertFalse(result.isPresent(), "Expected no library to be found by ID.");

        // Проверка вызова метода findById
        verify(libraryDaoMock).findById(libraryId);
    }

    @Test
    void addLibrary() {
        // Подготовка данных
        LibraryDto libraryDto = new LibraryDto(null, "Main Library");

        // Метод buildLibrary должен быть реализован для преобразования LibraryDto в Library.
        Library library = new Library("Main Library");

        when(libraryDaoMock.save(library)).thenReturn(library);

        // Вызов метода
        libraryService.add(libraryDto);

        // Проверка, что метод save был вызван с правильным объектом
        verify(libraryDaoMock).save(library);
    }

    @Test
    void updateLibraryWhenExists() {
        // Подготовка данных
        Long libraryId = 1L;
        LibraryDto libraryDto = new LibraryDto(libraryId, "Updated Library Name");
        Library library = new Library("Updated Library Name");
        library.setId(libraryId);

        // Настройка мока
        when(libraryDaoMock.findById(libraryId)).thenReturn(Optional.of(library));
        when(libraryDaoMock.update(library)).thenReturn(true); // Используем для эмуляции

        // Вызов метода
        libraryService.update(libraryDto);

        // Проверка, что библиотека была обновлена
        verify(libraryDaoMock).update(library);
    }

    @Test
    void updateThrowsExceptionWhenLibraryDoesNotExist() {
        // Подготовка данных
        Long libraryId = 1L;
        LibraryDto libraryDto = new LibraryDto(libraryId, "Library 1");

        // Настройка мока
        when(libraryDaoMock.findById(libraryId)).thenReturn(Optional.empty());

        // Проверка, что метод вызывает IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            libraryService.update(libraryDto);
        });

        assertEquals("Library Library 1 does not exist", exception.getMessage());
    }

    @Test
    void deleteLibraryWhenExists() {
        // Подготовка данных
        Long libraryId = 1L;

        // Настройка мока
        when(libraryDaoMock.findById(libraryId)).thenReturn(Optional.of(new Library("Existing Library")));
        when(libraryDaoMock.delete(libraryId)).thenReturn(true);

        // Вызов метода
        libraryService.delete(libraryId);

        // Проверка, что метод delete был вызван в LibraryDao с правильным ID
        verify(libraryDaoMock).delete(libraryId);
    }

    @Test
    void delete_ThrowsException_WhenLibraryDoesNotExist() {
        // Подготовка данных
        Long libraryId = 1L;

        // Настройка мока
        when(libraryDaoMock.findById(libraryId)).thenReturn(Optional.empty());

        // Проверка, что метод вызывает IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            libraryService.delete(libraryId);
        });

        assertEquals("Library 1 does not exist", exception.getMessage());
    }
}