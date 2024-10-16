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

/**
 * Servlet for handling book-related operations, including retrieving,
 * adding, updating, and deleting books in a library system.
 */
@WebServlet("/books")
public class BookServlet extends HttpServlet {

    private BooksService booksService;

    /**
     * Initializes the servlet and retrieves the BooksService instance.
     */
    public BookServlet() {
        booksService = BooksService.getInstance();
    }

    /**
     * Sets the BooksService instance to be used by this servlet.
     *
     * @param booksService the BooksService instance to set
     */
    public void setBooksService(BooksService booksService) {
        this.booksService = booksService;
    }

    /**
     * Handles GET requests to retrieve book details or a list of books.
     *
     * @param req the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if an error occurs during request processing
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Handles POST requests for adding new books or for updating/deleting existing books.
     *
     * @param req the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if an error occurs during request processing
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Handles PUT requests for updating existing books.
     *
     * @param req the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if an error occurs during request processing
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Handles DELETE requests for removing a book by its ID.
     *
     * @param req the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if an error occurs during request processing
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Retrieves a Library object based on the provided library ID.
     *
     * @param libraryId the ID of the library to retrieve
     * @param resp the HttpServletResponse object for sending errors
     * @return a Library object representing the library
     * @throws IOException if the library is not found
     */
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
