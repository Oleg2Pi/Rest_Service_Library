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

@WebServlet("/libraries")
public class LibraryServlet extends HttpServlet {

    private LibraryService libraryService;

    public LibraryServlet() {
        this.libraryService = LibraryService.getInstance();
    }

    public void setLibraryService(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

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
