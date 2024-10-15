package by.polikarpov;

import by.polikarpov.dto.BooksDto;
import by.polikarpov.service.BookLendingService;

import java.util.List;

public class JdbcRunner {
    public static void main(String[] args) {
        List<BooksDto> books = BookLendingService.getInstance().getByReaderId(1L);
        System.out.println(books);
    }
}
