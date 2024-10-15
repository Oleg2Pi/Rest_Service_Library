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

    private static final LibraryService libraryService = LibraryService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if  (idParam != null) {
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        } else{
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Library name cannot be empty");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = Long.valueOf(req.getParameter("id"));
        String libraryName = req.getParameter("libraryName");

        var libraryDto = new LibraryDto(id, libraryName);
        libraryService.update(libraryDto);
        resp.sendRedirect(req.getContextPath() + "/libraries");

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = Long.valueOf(req.getParameter("id"));
        libraryService.delete(id);
        resp.sendRedirect(req.getContextPath() + "/libraries");
    }
}
