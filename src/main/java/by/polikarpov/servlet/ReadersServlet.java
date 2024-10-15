package by.polikarpov.servlet;

import by.polikarpov.dto.ReadersDto;
import by.polikarpov.service.ReadersService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/readers")
public class ReadersServlet extends HttpServlet {

    private static final ReadersService readersService = ReadersService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null) {
            Long id = Long.parseLong(idParam);
            readersService.getById(id).ifPresentOrElse(
                    reader -> {
                        req.setAttribute("reader", reader);
                        try {
                            req.getRequestDispatcher("/WEB-INF/jsp/readerDetail.jsp").forward(req, resp);
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
            List<ReadersDto> readers = readersService.getAll();
            req.setAttribute("readers", readers);
            req.getRequestDispatcher("WEB-INF/jsp/readers.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String readerName = req.getParameter("readerName");

        if (idParam != null && !idParam.isEmpty() && readerName != null && !readerName.isEmpty()) {
            doPut(req, resp);
        } else if (readerName != null && !readerName.isEmpty()) {
            ReadersDto readerDto = new ReadersDto(null, readerName);
            readersService.add(readerDto);

            resp.sendRedirect(req.getContextPath() + "/readers");
        } else if (idParam != null && !idParam.isEmpty()) {
            doDelete(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Reader's name cannot be empty");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = Long.valueOf(req.getParameter("id"));
        String readerName = req.getParameter("readerName");

        var readerDto = new ReadersDto(id, readerName);
        readersService.update(readerDto);
        resp.sendRedirect(req.getContextPath() + "/readers");

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = Long.valueOf(req.getParameter("id"));
        readersService.delete(id);
        resp.sendRedirect(req.getContextPath() + "/readers");
    }
}
