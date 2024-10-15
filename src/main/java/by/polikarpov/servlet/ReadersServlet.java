package by.polikarpov.servlet;

import by.polikarpov.dto.BookLendingDto;
import by.polikarpov.dto.BooksDto;
import by.polikarpov.dto.ReadersDto;
import by.polikarpov.service.BookLendingService;
import by.polikarpov.service.BooksService;
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

    private ReadersService readersService;

    public ReadersServlet() {
        this.readersService = ReadersService.getInstance();
    }

    public void setReadersService(ReadersService readersService) {
        this.readersService = readersService;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null) {
            Long id = Long.parseLong(idParam);
            BookLendingService bookLendingService = BookLendingService.getInstance();
            List<BooksDto> books = bookLendingService.getByReaderId(id);
            List<BooksDto> booksNot = bookLendingService.getByNotReaderId(id);
            readersService.getById(id).ifPresentOrElse(
                    reader -> {
                        req.setAttribute("reader", reader);
                        req.setAttribute("books", books);
                        req.setAttribute("booksNot", booksNot);
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
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String readerName = req.getParameter("readerName");
        String bookIdSave = req.getParameter("bookIdSave");

        if (idParam != null && !idParam.isEmpty() && readerName != null && !readerName.isEmpty()) {
            doPut(req, resp);
        } else if (readerName != null && !readerName.isEmpty()) {
            ReadersDto readerDto = new ReadersDto(null, readerName);
            readersService.add(readerDto);

            resp.sendRedirect(req.getContextPath() + "/readers");
        } else if (bookIdSave != null && !bookIdSave.isEmpty()) {
            BookLendingService.getInstance().add(
                    new BookLendingDto(
                            readersService.getById(Long.valueOf(idParam)).get(),
                            BooksService.getInstance().getById(Long.valueOf(bookIdSave)).get()
                    )
            );
            resp.sendRedirect(req.getContextPath() + "/readers?id=" + idParam);
        } else if (idParam != null && !idParam.isEmpty()) {
            doDelete(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Reader's name cannot be empty");
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("id") == null || req.getParameter("id").isEmpty()
            || req.getParameter("readerName") == null || req.getParameter("readerName").isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Reader not exists");
        } else {
            Long id = Long.valueOf(req.getParameter("id"));
            String readerName = req.getParameter("readerName");

            var readerDto = new ReadersDto(id, readerName);
            readersService.update(readerDto);
            resp.sendRedirect(req.getContextPath() + "/readers");
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("id") == null || req.getParameter("id").isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Reader not exists");
        } else {
            Long id = Long.valueOf(req.getParameter("id"));
            String bookId = req.getParameter("bookId");
            if (bookId != null && !bookId.isEmpty()) {
                BookLendingService.getInstance().delete(id, Long.valueOf(bookId));
                resp.sendRedirect(req.getContextPath() + "/readers?id=" + id);
            } else {
                readersService.delete(id);
                resp.sendRedirect(req.getContextPath() + "/readers");
            }
        }
    }
}
