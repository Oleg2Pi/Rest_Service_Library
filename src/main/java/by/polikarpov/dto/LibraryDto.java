package by.polikarpov.dto;

/**
 * Data Transfer Object (DTO) representing a library.
 *
 * @param id the unique identifier of the library
 * @param libraryName the name of the library
 */
public record LibraryDto(Long id, String libraryName) {
}
