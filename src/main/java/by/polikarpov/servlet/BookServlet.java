package by.polikarpov.servlet;

import by.polikarpov.dto.BooksDto;
import by.polikarpov.dto.LibraryDto;
import by.polikarpov.dto.ReadersDto;
import by.polikarpov.entity.Library;
import by.polikarpov.service.BookLendingService;
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

    private BooksService booksService;

    public BookServlet() {
        booksService = BooksService.getInstance();
    }

    public void setBooksService(BooksService booksService) {
        this.booksService = booksService;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            Long id = Long.parseLong(idParam);
            List<ReadersDto> readers = BookLendingService.getInstance().getByBookId(id);
            booksService.getById(id).ifPresentOrElse(
                    book -> {
                        req.setAttribute("book", book);
                        req.setAttribute("readers", readers);
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
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

            resp.sendRedirect(req.getContextPath() + "/libraries?id=" + libraryId);
        } else if (idParam != null && !idParam.isEmpty() && libraryId != null && !libraryId.isEmpty()) {
            doDelete(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book's data cannot be empty");
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("id") == null || req.getParameter("id").isEmpty()
            || req.getParameter("title") == null || req.getParameter("title").isEmpty()
            || req.getParameter("author") == null || req.getParameter("author").isEmpty()
            || req.getParameter("libraryId") == null || req.getParameter("libraryId").isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book's data not correct");
        } else {
            Long id = Long.valueOf(req.getParameter("id"));
            String title = req.getParameter("title");
            String author = req.getParameter("author");
            String libraryId = req.getParameter("libraryId");

            var bookDto = new BooksDto(id, title, author, getLibrary(libraryId, resp));
            booksService.update(bookDto);
            resp.sendRedirect(req.getContextPath() + "/libraries?id=" + libraryId);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("id") == null || req.getParameter("id").isEmpty() ||
            req.getParameter("libraryId") == null || req.getParameter("libraryId").isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Not exists book");
        } else {
            Long id = Long.valueOf(req.getParameter("id"));
            booksService.delete(id);
            resp.sendRedirect(req.getContextPath() + "/libraries?id=" + req.getParameter("libraryId"));
        }
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
