package by.polikarpov.setvlet;

import by.polikarpov.dto.BooksDto;
import by.polikarpov.dto.LibraryDto;
import by.polikarpov.dto.ReadersDto;
import by.polikarpov.entity.Library;
import by.polikarpov.service.BookLendingService;
import by.polikarpov.service.BooksService;
import by.polikarpov.service.LibraryService;
import by.polikarpov.servlet.BookServlet;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServletTest {

    private BookServlet bookServlet;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private BooksService booksService;

    @BeforeEach
    void setUp() {
        bookServlet = new BookServlet();
        bookServlet.setBooksService(booksService);
    }

    @Test
    void doGetOneBookAndReaders() throws ServletException, IOException {
        BooksDto bookDto = new BooksDto(1L, "title", "author", new Library("library"));
        ReadersDto readerDto1 = new ReadersDto(1L, "name 1");
        ReadersDto readerDto2 = new ReadersDto(2L, "name 2");

        try (MockedStatic<BookLendingService> bookLendingServiceMockedStatic = mockStatic(BookLendingService.class)) {
            when(req.getParameter("id")).thenReturn(String.valueOf(bookDto.id()));

            BookLendingService bookLendingService = mock(BookLendingService.class);
            bookLendingServiceMockedStatic.when(BookLendingService::getInstance).thenReturn(bookLendingService);
            when(bookLendingService.getByBookId(bookDto.id())).thenReturn(Arrays.asList(readerDto1, readerDto2));

            when(booksService.getById(bookDto.id())).thenReturn(Optional.of(bookDto));

            when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

            bookServlet.doGet(req, resp);

            verify(bookLendingService).getByBookId(bookDto.id());
            verify(booksService).getById(bookDto.id());
            verify(req).setAttribute("book", bookDto);
            verify(req).setAttribute("readers", Arrays.asList(readerDto1, readerDto2));
            verify(requestDispatcher).forward(req, resp);
        }
    }

    @Test
    void doGetAllBooks() throws ServletException, IOException {
        Library library = new Library("Library");
        BooksDto book1 = new BooksDto(1L, "title 1", "author", library);
        BooksDto book2 = new BooksDto(2L, "title 2", "author", library);

        when(req.getParameter("id")).thenReturn(null);

        when(booksService.getAll()).thenReturn(Arrays.asList(book1, book2));
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        bookServlet.doGet(req, resp);

        verify(booksService).getAll();
        verify(req).setAttribute("books", Arrays.asList(book1, book2));
        verify(requestDispatcher).forward(req, resp);
    }

    @Test
    void doGetWithIdNotExistsBook() throws ServletException, IOException {
        when(req.getParameter("id")).thenReturn("1");
        when(booksService.getById(1L)).thenReturn(Optional.empty());

        bookServlet.doGet(req, resp);

        verify(booksService).getById(1L);
        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doPostWithoutId() throws ServletException, IOException {
        String title = "title";
        String author = "author";
        String libraryId = "1";
        Library library = new Library("library");
        library.setId(Long.valueOf(libraryId));
        BooksDto book = new BooksDto(null, title, author, library);
        LibraryDto libraryDto = new LibraryDto(library.getId(), library.getLibraryName());

        try (MockedStatic<LibraryService> libraryServiceMockedStatic = mockStatic(LibraryService.class)) {
            when(req.getParameter("id")).thenReturn(null);
            when(req.getParameter("title")).thenReturn(title);
            when(req.getParameter("author")).thenReturn(author);
            when(req.getParameter("libraryId")).thenReturn(libraryId);

            when(req.getContextPath()).thenReturn("/WEB-INF");

            LibraryService libraryService = mock(LibraryService.class);
            libraryServiceMockedStatic.when(LibraryService::getInstance).thenReturn(libraryService);
            when(libraryService.getById(Long.valueOf(libraryId))).thenReturn(Optional.of(libraryDto));

            bookServlet.doPost(req, resp);

            verify(libraryService).getById(Long.valueOf(libraryId));
            verify(booksService).add(book);
            verify(req).getContextPath();
            verify(resp).sendRedirect("/WEB-INF" + "/libraries?id=" + libraryId);
        }
    }

    @Test
    void doPostAndPut() throws ServletException, IOException {
        String id = "1";
        String title = "title";
        String author = "author";
        String libraryId = "1";
        Library library = new Library("library");
        library.setId(Long.valueOf(libraryId));
        BooksDto book = new BooksDto(Long.valueOf(id), title, author, library);
        LibraryDto libraryDto = new LibraryDto(library.getId(), library.getLibraryName());

        try (MockedStatic<LibraryService> libraryServiceMockedStatic = mockStatic(LibraryService.class)) {
            when(req.getParameter("id")).thenReturn(id);
            when(req.getParameter("title")).thenReturn(title);
            when(req.getParameter("author")).thenReturn(author);
            when(req.getParameter("libraryId")).thenReturn(libraryId);

            when(req.getContextPath()).thenReturn("/WEB-INF");

            LibraryService libraryService = mock(LibraryService.class);
            libraryServiceMockedStatic.when(LibraryService::getInstance).thenReturn(libraryService);
            when(libraryService.getById(Long.valueOf(libraryId))).thenReturn(Optional.of(libraryDto));

            bookServlet.doPost(req, resp);

            verify(libraryService).getById(Long.valueOf(libraryId));
            verify(booksService).update(book);
            verify(req).getContextPath();
            verify(resp).sendRedirect("/WEB-INF" + "/libraries?id=" + libraryId);
        }
    }

    @Test
    void doPostAndDelete() throws ServletException, IOException {
        String id = "1";
        String libraryId = "1";

        when(req.getParameter("id")).thenReturn(id);
        when(req.getParameter("title")).thenReturn(null);
        when(req.getParameter("author")).thenReturn(null);
        when(req.getParameter("libraryId")).thenReturn(libraryId);

        when(req.getContextPath()).thenReturn("/WEB-INF");

        bookServlet.doPost(req, resp);

        verify(booksService).delete(Long.valueOf(id));
        verify(req).getContextPath();
        verify(resp).sendRedirect("/WEB-INF" + "/libraries?id=" + libraryId);
    }

    @Test
    void doPostNotValid() throws ServletException, IOException {
        when(req.getParameter("id")).thenReturn(null);
        when(req.getParameter("title")).thenReturn(null);
        when(req.getParameter("author")).thenReturn(null);
        when(req.getParameter("libraryId")).thenReturn(null);

        bookServlet.doPost(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND, "Book's data cannot be empty");
    }

    @Test
    void doPutNotValid() throws ServletException, IOException {

        bookServlet.doPut(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND, "Book's data not correct");
    }

    @Test
    void doDeleteNotValid() throws ServletException, IOException {

        bookServlet.doDelete(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND, "Not exists book");
    }

}