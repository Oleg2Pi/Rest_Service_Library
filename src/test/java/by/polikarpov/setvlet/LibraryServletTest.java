package by.polikarpov.setvlet;

import by.polikarpov.dto.BooksDto;
import by.polikarpov.dto.LibraryDto;
import by.polikarpov.entity.Library;
import by.polikarpov.service.BooksService;
import by.polikarpov.service.LibraryService;
import by.polikarpov.servlet.LibraryServlet;
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
class LibraryServletTest {

    private LibraryServlet libraryServlet;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        libraryServlet = new LibraryServlet();
        libraryServlet.setLibraryService(libraryService);
    }

    @Test
    void doGetOneLibraryAndBooks() throws ServletException, IOException {
        Library library = new Library("library");
        library.setId(1L);
        LibraryDto libraryDto = new LibraryDto(library.getId(), library.getLibraryName());
        BooksDto bookDto1 = new BooksDto(1L, "title 1", "author", library);
        BooksDto bookDto2 = new BooksDto(2L, "title 2", "author", library);

        try (MockedStatic<BooksService> booksServiceMockedStatic = mockStatic(BooksService.class)) {
            when(req.getParameter("id")).thenReturn("1");

            BooksService booksService = mock(BooksService.class);
            booksServiceMockedStatic.when(BooksService::getInstance).thenReturn(booksService);
            when(booksService.getAllByLibraryId(libraryDto.id())).thenReturn(Arrays.asList(bookDto1, bookDto2));

            when(libraryService.getById(libraryDto.id())).thenReturn(Optional.of(libraryDto));

            when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

            libraryServlet.doGet(req, resp);

            verify(booksService).getAllByLibraryId(libraryDto.id());
            verify(libraryService).getById(libraryDto.id());
            verify(req).setAttribute("library", libraryDto);
            verify(req).setAttribute("books", Arrays.asList(bookDto1, bookDto2));
            verify(requestDispatcher).forward(req, resp);
        }
    }

    @Test
    void doGetAllLibraries() throws ServletException, IOException {
        LibraryDto libraryDto1 = new LibraryDto(1L, "lib1");
        LibraryDto libraryDto2 = new LibraryDto(2L, "lib2");

        when(req.getParameter("id")).thenReturn(null);

        when(libraryService.getAll()).thenReturn(Arrays.asList(libraryDto1, libraryDto2));
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        libraryServlet.doGet(req, resp);

        verify(libraryService).getAll();
        verify(req).setAttribute("libraries", Arrays.asList(libraryDto1, libraryDto2));
        verify(requestDispatcher).forward(req, resp);
    }

    @Test
    void doGetWithIdNotExistsBook() throws ServletException, IOException {
        when(req.getParameter("id")).thenReturn("1");
        when(libraryService.getById(1L)).thenReturn(Optional.empty());

        libraryServlet.doGet(req, resp);

        verify(libraryService).getById(1L);
        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doPostWithoutId() throws ServletException, IOException {
        String libraryName = "Name";
        LibraryDto libraryDto = new LibraryDto(null, libraryName);

        when(req.getParameter("id")).thenReturn(null);
        when(req.getParameter("libraryName")).thenReturn(libraryName);

        when(req.getContextPath()).thenReturn("/WEB-INF");

        libraryServlet.doPost(req, resp);

        verify(libraryService).add(libraryDto);
        verify(req).getContextPath();
        verify(resp).sendRedirect("/WEB-INF" + "/libraries");
    }

    @Test
    void doPostAndPut() throws ServletException, IOException {
        String id = "1";
        String libraryName = "name";
        LibraryDto libraryDto = new LibraryDto(Long.valueOf(id), libraryName);

        when(req.getParameter("id")).thenReturn(id);
        when(req.getParameter("libraryName")).thenReturn(libraryName);

        when(req.getContextPath()).thenReturn("/WEB-INF");

        libraryServlet.doPost(req, resp);

        verify(libraryService).update(libraryDto);
        verify(req).getContextPath();
        verify(resp).sendRedirect("/WEB-INF" + "/libraries");
    }

    @Test
    void doPostAndDelete() throws ServletException, IOException {
        String id = "1";

        when(req.getParameter("id")).thenReturn(id);

        when(req.getContextPath()).thenReturn("/WEB-INF");

        libraryServlet.doPost(req, resp);

        verify(libraryService).delete(Long.valueOf(id));
        verify(req).getContextPath();
        verify(resp).sendRedirect("/WEB-INF" + "/libraries");
    }

    @Test
    void doPostNotValid() throws ServletException, IOException {
        when(req.getParameter("id")).thenReturn(null);
        when(req.getParameter("libraryName")).thenReturn(null);

        libraryServlet.doPost(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND, "Library name cannot be empty");
    }

    @Test
    void doPutNotValid() throws ServletException, IOException {

        libraryServlet.doPut(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND, "Library's data not correct");
    }

    @Test
    void doDeleteNotValid() throws ServletException, IOException {

        libraryServlet.doDelete(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND, "Not exists library");
    }
}
