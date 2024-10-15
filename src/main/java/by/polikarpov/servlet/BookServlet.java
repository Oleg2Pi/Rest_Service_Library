package by.polikarpov.servlet;

import by.polikarpov.dto.BooksDto;
import by.polikarpov.dto.LibraryDto;
import by.polikarpov.entity.Library;
import by.polikarpov.service.BooksService;
import by.polikarpov.service.LibraryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/books")
public class BookServlet extends HttpServlet {

    private static final BooksService booksService = BooksService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null) {
            Long id = Long.parseLong(idParam);
            booksService.getById(id).ifPresentOrElse(
                    book -> {
                        req.setAttribute("book", book);
                        try {
                            req.getRequestDispatcher("/WEB-INF/jsp/bookDetail.jsp").forward(req, resp);
                        } catch (ServletException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    () -> {
                        try {
                            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } else {
            List<BooksDto> books = booksService.getAll();
            req.setAttribute("books", books);
            req.getRequestDispatcher("WEB-INF/jsp/books.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String title = req.getParameter("title");
        String author = req.getParameter("author");
        String libraryId = req.getParameter("libraryId");

        if (idParam != null && !idParam.isEmpty() && title != null && !title.isEmpty()
            && author != null && !author.isEmpty() && libraryId != null && !libraryId.isEmpty()) {
            doPut(req, resp);
        } else if (title != null && !title.isEmpty() && author != null && !author.isEmpty()
                   && libraryId != null && !libraryId.isEmpty()) {

            BooksDto bookDto = new BooksDto(null, title, author, getLibrary(libraryId, resp));
            booksService.add(bookDto);

            resp.sendRedirect(req.getContextPath() + "/books");
        } else if (idParam != null && !idParam.isEmpty()) {
            doDelete(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Book's data cannot be empty");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = Long.valueOf(req.getParameter("id"));
        String title = req.getParameter("title");
        String author = req.getParameter("author");
        String libraryId = req.getParameter("libraryId");

        var bookDto = new BooksDto(id, title, author, getLibrary(libraryId, resp));
        booksService.update(bookDto);
        resp.sendRedirect(req.getContextPath() + "/books");

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = Long.valueOf(req.getParameter("id"));
        booksService.delete(id);
        resp.sendRedirect(req.getContextPath() + "/books");
    }

    private Library getLibrary(String libraryId, HttpServletResponse resp) throws IOException {
        Optional<LibraryDto> optional = LibraryService.getInstance().getById(Long.valueOf(libraryId));
        if (optional.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Library not found");
        }
        var libraryDto = optional.get();
        Library library = new Library(libraryDto.libraryName());
        library.setId(libraryDto.id());
        return library;
    }
}
