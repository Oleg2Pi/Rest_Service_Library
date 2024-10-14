package by.polikarpov.dto;

import by.polikarpov.entity.Books;
import by.polikarpov.entity.Readers;

public record BookLending(Readers readers, Books books) {
}
