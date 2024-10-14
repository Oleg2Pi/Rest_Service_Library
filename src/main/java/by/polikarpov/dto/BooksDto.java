package by.polikarpov.dto;

import by.polikarpov.entity.Library;

public record BooksDto(Long id, String title, String author, Library library) {
}
