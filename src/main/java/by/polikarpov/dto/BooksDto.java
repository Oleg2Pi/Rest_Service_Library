package by.polikarpov.dto;

import by.polikarpov.entity.Library;

/**
 * Data Transfer Object (DTO) representing a book.
 *
 * @param id      the unique identifier of the book
 * @param title   the title of the book
 * @param author  the author of the book
 * @param library the library to which the book belongs
 */
public record BooksDto(Long id, String title, String author, Library library) {
}
