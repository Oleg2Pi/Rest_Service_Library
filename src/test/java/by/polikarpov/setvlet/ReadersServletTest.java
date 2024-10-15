package by.polikarpov.setvlet;

import by.polikarpov.dto.BookLendingDto;
import by.polikarpov.dto.BooksDto;
import by.polikarpov.dto.ReadersDto;
import by.polikarpov.entity.Library;
import by.polikarpov.service.BookLendingService;
import by.polikarpov.service.BooksService;
import by.polikarpov.service.ReadersService;
import by.polikarpov.servlet.ReadersServlet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadersServletTest {

    private ReadersServlet readersServlet;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private ReadersService readersService;

    @BeforeEach
    void setUp() {
        readersServlet = new ReadersServlet();
        readersServlet.setReadersService(readersService);
    }

    @Test
    void doGetOneReaderAndBooksAndNotReaderBooks() throws ServletException, IOException {
        ReadersDto readerDto = new ReadersDto(1L, "name 1");
        BooksDto bookDto1 = new BooksDto(1L, "title 1", "author", new Library("library"));
        BooksDto bookDto2 = new BooksDto(2L, "title 2", "author", new Library("library"));
        BooksDto bookDto3 = new BooksDto(3L, "title 3", "author", new Library("library"));
        BooksDto bookDto4 = new BooksDto(4L, "title 4", "author", new Library("library"));

        try (MockedStatic<BookLendingService> bookLendingServiceMockedStatic = mockStatic(BookLendingService.class)) {
            when(req.getParameter("id")).thenReturn(String.valueOf(readerDto.id()));

            BookLendingService bookLendingService = mock(BookLendingService.class);
            bookLendingServiceMockedStatic.when(BookLendingService::getInstance).thenReturn(bookLendingService);
            when(bookLendingService.getByReaderId(readerDto.id())).thenReturn(Arrays.asList(bookDto1, bookDto2));
            when(bookLendingService.getByNotReaderId(readerDto.id())).thenReturn(Arrays.asList(bookDto3, bookDto4));

            when(readersService.getById(readerDto.id())).thenReturn(Optional.of(readerDto));

            when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

            readersServlet.doGet(req, resp);

            verify(bookLendingService).getByReaderId(readerDto.id());
            verify(bookLendingService).getByNotReaderId(readerDto.id());
            verify(readersService).getById(readerDto.id());
            verify(req).setAttribute("reader", readerDto);
            verify(req).setAttribute("books", Arrays.asList(bookDto1, bookDto2));
            verify(req).setAttribute("booksNot", Arrays.asList(bookDto3, bookDto4));
            verify(requestDispatcher).forward(req, resp);
        }
    }

    @Test
    void doGetAllReaders() throws ServletException, IOException {
        ReadersDto reader1 = new ReadersDto(1L, "reader 1");
        ReadersDto reader2 = new ReadersDto(2L, "reader 2");

        when(req.getParameter("id")).thenReturn(null);

        when(readersService.getAll()).thenReturn(Arrays.asList(reader1, reader2));
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        readersServlet.doGet(req, resp);

        verify(readersService).getAll();
        verify(req).setAttribute("readers", Arrays.asList(reader1, reader2));
        verify(requestDispatcher).forward(req, resp);
    }

    @Test
    void doGetWithIdNotExistsReader() throws ServletException, IOException {
        when(req.getParameter("id")).thenReturn("1");
        when(readersService.getById(1L)).thenReturn(Optional.empty());

        readersServlet.doGet(req, resp);

        verify(readersService).getById(1L);
        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doPostWithoutId() throws ServletException, IOException {
        String readerName = "reader";
        ReadersDto readersDto = new ReadersDto(null, readerName);

        when(req.getParameter("id")).thenReturn(null);
        when(req.getParameter("readerName")).thenReturn(readerName);

        when(req.getContextPath()).thenReturn("/WEB-INF");

        readersServlet.doPost(req, resp);

        verify(readersService).add(readersDto);
        verify(req).getContextPath();
        verify(resp).sendRedirect("/WEB-INF" + "/readers");
    }

    @Test
    void doPostWithBookIdSave() throws ServletException, IOException {
        BooksDto booksDto = new BooksDto(1L, "book", "author", new Library("library"));
        ReadersDto readerDto = new ReadersDto(1L, "reader");
        BookLendingDto bookLendingDto = new BookLendingDto(readerDto, booksDto);

        try (MockedStatic<BookLendingService> bookLendingServiceMockedStatic = mockStatic(BookLendingService.class);
             MockedStatic<BooksService> booksServiceMockedStatic = mockStatic(BooksService.class)) {
            when(req.getParameter("id")).thenReturn("1");
            when(req.getParameter("readerName")).thenReturn(null);
            when(req.getParameter("bookIdSave")).thenReturn("1");
            when(req.getContextPath()).thenReturn("/WEB-INF");

            BookLendingService bookLendingService = mock(BookLendingService.class);
            bookLendingServiceMockedStatic.when(BookLendingService::getInstance).thenReturn(bookLendingService);

            when(readersService.getById(readerDto.id())).thenReturn(Optional.of(readerDto));

            BooksService booksService = mock(BooksService.class);
            booksServiceMockedStatic.when(BooksService::getInstance).thenReturn(booksService);
            when(booksService.getById(booksDto.id())).thenReturn(Optional.of(booksDto));

            readersServlet.doPost(req, resp);

            verify(bookLendingService).add(bookLendingDto);
            verify(readersService).getById(readerDto.id());
            verify(booksService).getById(booksDto.id());
            verify(req).getContextPath();
            verify(resp).sendRedirect("/WEB-INF" + "/readers?id=" + readerDto.id());
        }
    }

    @Test
    void doPostAndPut() throws ServletException, IOException {
        String id = "1";
        String readerName = "reader";
        ReadersDto reader = new ReadersDto(Long.valueOf(id), readerName);

        when(req.getParameter("id")).thenReturn(id);
        when(req.getParameter("readerName")).thenReturn(readerName);

        when(req.getContextPath()).thenReturn("/WEB-INF");

        readersServlet.doPost(req, resp);

        verify(readersService).update(reader);
        verify(req).getContextPath();
        verify(resp).sendRedirect("/WEB-INF" + "/readers");
    }

    @Test
    void doPostAndDelete() throws ServletException, IOException {
        String id = "1";

        when(req.getParameter("id")).thenReturn(id);

        when(req.getContextPath()).thenReturn("/WEB-INF");

        readersServlet.doPost(req, resp);

        verify(readersService).delete(Long.valueOf(id));
        verify(req).getContextPath();
        verify(resp).sendRedirect("/WEB-INF" + "/readers");
    }

    @Test
    void doPostAndDeleteByBookId() throws ServletException, IOException {
        String readerId = "1";
        String bookId = "1";

        try (MockedStatic<BookLendingService> bookLendingServiceMockedStatic = mockStatic(BookLendingService.class)) {
            when(req.getParameter("id")).thenReturn(readerId);
            when(req.getParameter("readerName")).thenReturn(null);
            when(req.getParameter("bookIdSave")).thenReturn(null);
            when(req.getParameter("bookId")).thenReturn(bookId);
            when(req.getContextPath()).thenReturn("WEB-INF");

            BookLendingService bookLendingService = mock(BookLendingService.class);
            bookLendingServiceMockedStatic.when(BookLendingService::getInstance).thenReturn(bookLendingService);

            readersServlet.doPost(req, resp);

            verify(bookLendingService).delete(Long.valueOf(readerId), Long.valueOf(bookId));
            verify(req).getContextPath();
            verify(resp).sendRedirect("WEB-INF" + "/readers?id=" + readerId);
        }
    }

    @Test
    void doPostNotValid() throws ServletException, IOException {

        readersServlet.doPost(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND, "Reader's name cannot be empty");
    }

    @Test
    void doPutNotValid() throws ServletException, IOException {

        readersServlet.doPut(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND, "Reader not exists");
    }

    @Test
    void doDeleteNotValid() throws ServletException, IOException {

        readersServlet.doDelete(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND, "Reader not exists");
    }
}
