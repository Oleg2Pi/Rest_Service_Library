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
import org.apache.commons.lang3.StringUtils;

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
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if an error occurs during request processing
     * @throws IOException      if an I/O error occurs
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (StringUtils.isNotBlank(idParam)) {
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
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if an error occurs during request processing
     * @throws IOException      if an I/O error occurs
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String title = req.getParameter("title");
        String author = req.getParameter("author");
        String libraryId = req.getParameter("libraryId");

        if (isUpdateRequest(idParam, title, author, libraryId)) {
            doPut(req, resp);
        } else if (isCreateRequest(title, author, libraryId)) {
            handleCreateRequest(title, author, libraryId, req, resp);
        } else if (isDeleteRequest(idParam, libraryId)) {
            doDelete(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book's data cannot be empty");
        }
    }

    /**
     * Check method for Handle PUT Request
     *
     * @param idParam
     * @param title
     * @param author
     * @param libraryId
     * @return boolean
     */
    private boolean isUpdateRequest(String idParam, String title, String author, String libraryId) {
        return StringUtils.isNotBlank(idParam) && StringUtils.isNotBlank(title)
               && StringUtils.isNotBlank(author) && StringUtils.isNotBlank(libraryId);
    }

    /**
     * Check method for Handle POST Request
     *
     * @param title
     * @param author
     * @param libraryId
     * @return  boolean
     */
    private boolean isCreateRequest(String title, String author, String libraryId) {
        return StringUtils.isNotBlank(title) && StringUtils.isNotBlank(author)
               && StringUtils.isNotBlank(libraryId);
    }

    /**
     * Check method for Handle DELETE Request
     *
     * @param idParam
     * @param libraryId
     * @return boolean
     */
    private boolean isDeleteRequest(String idParam, String libraryId) {
        return StringUtils.isNotBlank(idParam) && StringUtils.isNotBlank(libraryId);
    }

    /**
     * Handle POST Request that create new book
     *
     * @param title
     * @param author
     * @param libraryId
     * @param req
     * @param resp
     * @throws IOException
     */
    private void handleCreateRequest(String title, String author, String libraryId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BooksDto bookDto = new BooksDto(null, title, author, getLibrary(libraryId, resp));
        booksService.add(bookDto);
        resp.sendRedirect(req.getContextPath() + "/libraries?id=" + libraryId);
    }

    /**
     * Handles PUT requests for updating existing books.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        String title = req.getParameter("title");
        String author = req.getParameter("author");
        String libraryId = req.getParameter("libraryId");

        if (!isUpdateRequest(idParam, title, author, libraryId)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book's data not correct");
        } else {
            var bookDto = new BooksDto(Long.valueOf(idParam), title, author, getLibrary(libraryId, resp));
            booksService.update(bookDto);
            resp.sendRedirect(req.getContextPath() + "/libraries?id=" + libraryId);
        }
    }

    /**
     * Handles DELETE requests for removing a book by its ID.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        String libraryId = req.getParameter("libraryId");

        if (!isDeleteRequest(idParam, libraryId)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Not exists book");
        } else {
            booksService.delete(Long.valueOf(idParam));
            resp.sendRedirect(req.getContextPath() + "/libraries?id=" + req.getParameter("libraryId"));
        }
    }

    /**
     * Retrieves a Library object based on the provided library ID.
     *
     * @param libraryId the ID of the library to retrieve
     * @param resp      the HttpServletResponse object for sending errors
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
