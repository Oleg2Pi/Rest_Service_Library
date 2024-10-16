package by.polikarpov.servlet;

import by.polikarpov.dto.BooksDto;
import by.polikarpov.dto.LibraryDto;
import by.polikarpov.service.BooksService;
import by.polikarpov.service.LibraryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet for handling library-related operations including retrieving,
 * adding, updating, and deleting libraries.
 */
@WebServlet("/libraries")
public class LibraryServlet extends HttpServlet {

    private LibraryService libraryService;

    /**
     * Initializes the servlet and retrieves the LibraryService instance.
     */
    public LibraryServlet() {
        this.libraryService = LibraryService.getInstance();
    }

    /**
     * Sets the LibraryService instance to be used by this servlet.
     *
     * @param libraryService the LibraryService instance to set
     */
    public void setLibraryService(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    /**
     * Handles GET requests to retrieve library details or list all libraries.
     *
     * @param req the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if an error occurs during request processing
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null) {
            Long id = Long.parseLong(idParam);
            List<BooksDto> books = BooksService.getInstance().getAllByLibraryId(id);
            libraryService.getById(id).ifPresentOrElse(
                    library -> {
                        req.setAttribute("library", library);
                        req.setAttribute("books", books);
                        try {
                            req.getRequestDispatcher("/WEB-INF/jsp/libraryDetail.jsp").forward(req, resp);
                        } catch (ServletException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
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
            List<LibraryDto> libraries = libraryService.getAll();
            req.setAttribute("libraries", libraries);
            req.getRequestDispatcher("WEB-INF/jsp/libraries.jsp").forward(req, resp);
        }
    }

    /**
     * Handles POST requests for adding new libraries or updating/deleting existing libraries.
     *
     * @param req the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if an error occurs during request processing
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String libraryName = req.getParameter("libraryName");

        if (idParam != null && !idParam.isEmpty() && libraryName != null && !libraryName.isEmpty()) {
            doPut(req, resp);
        } else if (libraryName != null && !libraryName.isEmpty()) {
            LibraryDto libraryDto = new LibraryDto(null, libraryName);
            libraryService.add(libraryDto);

            resp.sendRedirect(req.getContextPath() + "/libraries");
        } else if (idParam != null && !idParam.isEmpty()) {
            doDelete(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Library name cannot be empty");
        }
    }

    /**
     * Handles PUT requests for updating existing libraries.
     *
     * @param req the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if an error occurs during request processing
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("id") == null || req.getParameter("id").isEmpty()
            || req.getParameter("libraryName") == null || req.getParameter("libraryName").isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Library's data not correct");
        } else {
            Long id = Long.valueOf(req.getParameter("id"));
            String libraryName = req.getParameter("libraryName");

            var libraryDto = new LibraryDto(id, libraryName);
            libraryService.update(libraryDto);
            resp.sendRedirect(req.getContextPath() + "/libraries");
        }
    }

    /**
     * Handles DELETE requests for removing a library by its ID.
     *
     * @param req the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if an error occurs during request processing
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("id") == null || req.getParameter("id").isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Not exists library");
        } else {
            Long id = Long.valueOf(req.getParameter("id"));
            libraryService.delete(id);
            resp.sendRedirect(req.getContextPath() + "/libraries");
        }
    }
}
