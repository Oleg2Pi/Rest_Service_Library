package by.polikarpov.dto;

/**
 * Data Transfer Object (DTO) representing a reader.
 *
 * @param id the unique identifier of the reader
 * @param readerName the name of the reader
 */
public record ReadersDto(Long id, String readerName) {
}
