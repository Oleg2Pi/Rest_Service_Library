package by.polikarpov.dto;

/**
 * Data Transfer Object (DTO) representing a book lending transaction.
 * This record associates a reader with a book they are lending.
 *
 * @param readers the details of the readerDto involved in the lending
 * @param books the details of the bookDto being lent
 */
public record BookLendingDto(ReadersDto readers, BooksDto books) {
}
