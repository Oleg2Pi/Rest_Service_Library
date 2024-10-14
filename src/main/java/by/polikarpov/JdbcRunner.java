package by.polikarpov;

import by.polikarpov.dao.LibraryDao;
import by.polikarpov.entity.Library;

import java.util.Optional;

public class JdbcRunner {
    public static void main(String[] args) {
        LibraryDao libraryDao = LibraryDao.getInstance();
        Library library = new Library("Main Library");
        System.out.println(libraryDao.save(library));
    }
}
