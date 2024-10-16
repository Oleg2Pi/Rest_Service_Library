<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
  <title>${requestScope.library.libraryName()}</title>
  <style>
    table {
      border-collapse: collapse; /* Убирает двойные границы */
      width: 100%; /* Ширина таблицы */
    }
    th, td {
      border: 2px solid black; /* Толщина и цвет линий */
      padding: 10px; /* Отступы внутри ячеек */
      text-align: left; /* Выравнивание текста */
    }
    th {
      background-color: #f2f2f2; /* Цвет фона заголовков */
    }
  </style>
</head>
<body>
<h1>${requestScope.library.libraryName()}</h1>

<table>
  <tr>
    <th>Books</th>
    <th>Link</th>
    <th>Action</th>
  </tr>
  <c:choose>
    <c:when test="${not empty requestScope.books}">
      <c:forEach var="book" items="${requestScope.books}">
        <tr>
          <td>
            <h4>${book.title()}</h4>
          </td>

          <td>
            <a href="${PageContext.request.contextPath}/books?id=${book.id()}">View</a>
          </td>

          <td>
            <form action="books" method="post" style="display:inline;">
              <input type="hidden" name="id" value="${book.id()}"/>
              <!-- Optionally add hidden input for update -->
              <input type="text" name="title" required placeholder="Title"/>
              <input type="text" name="author" required placeholder="Author"/>
              <input type="hidden" name="libraryId" value="${requestScope.library.id()}"/>
              <input type="submit" value="Update"/>
            </form>
          </td>

          <td>
            <form action="books" method="post" style="display:inline;">
              <input type="hidden" name="id" value="${book.id()}"/>
              <input type="hidden" name="libraryId" value="${requestScope.library.id()}"/>
               <input type="submit" value="Delete"/>
            </form>
          </td>
        </tr>
      </c:forEach>
    </c:when>
    <c:otherwise>
      <h3>Not found books</h3>
    </c:otherwise>
  </c:choose>
</table>

<h2>Add New Book</h2>
<form action="books" method="post">
  <input type="text" name="title" required placeholder="Title"/>
  <input type="text" name="author" required placeholder="Author"/>
  <input type="hidden" name="libraryId" value="${requestScope.library.id()}"/>
  <input type="submit" value="Add Book"/>
</form>

<br>
<a href="${PageContext.request.contextPath}/libraries">Back to Library List</a>
</body>
</html>